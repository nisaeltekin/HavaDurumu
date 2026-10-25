const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = process.env.PORT || 5000;
const DB_FILE = path.join(__dirname, 'feedbacks.json');

// feedbacks.json yoksa boş dizi ile oluştur
if (!fs.existsSync(DB_FILE)) {
    fs.writeFileSync(DB_FILE, JSON.stringify([], null, 2), 'utf8');
}

function readFeedbacks() {
    try {
        const data = fs.readFileSync(DB_FILE, 'utf8');
        return JSON.parse(data || '[]');
    } catch (e) {
        return [];
    }
}

function writeFeedbacks(feedbacks) {
    fs.writeFileSync(DB_FILE, JSON.stringify(feedbacks, null, 2), 'utf8');
}

function setCorsHeaders(res) {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, DELETE');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
}

const server = http.createServer((req, res) => {
    setCorsHeaders(res);

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;

    // ─────────────────────────────────────────────────────────────────────────────
    // 1. POST /api/feedback — Mobil uygulamadan gelen bildirimi kaydet
    // ─────────────────────────────────────────────────────────────────────────────
    if (req.method === 'POST' && pathname === '/api/feedback') {
        let body = '';
        req.on('data', chunk => {
            body += chunk.toString();
        });

        req.on('end', () => {
            try {
                const data = JSON.parse(body || '{}');
                const {
                    location,
                    latitude,
                    longitude,
                    reportedCondition,
                    currentTemp,
                    userComment,
                    timestamp
                } = data;

                if (!location || !reportedCondition) {
                    res.writeHead(400, { 'Content-Type': 'application/json; charset=utf-8' });
                    res.end(JSON.stringify({
                        success: false,
                        message: 'Eksik parametre: location ve reportedCondition zorunludur.'
                    }));
                    return;
                }

                const feedbacks = readFeedbacks();
                const newFeedback = {
                    id: Date.now().toString(),
                    location: location.trim(),
                    latitude: latitude !== undefined ? Number(latitude) : null,
                    longitude: longitude !== undefined ? Number(longitude) : null,
                    reportedCondition: reportedCondition.trim(),
                    currentTemp: currentTemp !== undefined ? Number(currentTemp) : null,
                    userComment: (userComment || '').trim(),
                    clientTimestamp: timestamp || Date.now(),
                    receivedAt: new Date().toISOString()
                };

                feedbacks.unshift(newFeedback); // En yeni başa
                writeFeedbacks(feedbacks);

                console.log(`[Yeni Bildirim] 📍 ${newFeedback.location} - ${newFeedback.reportedCondition} ("${newFeedback.userComment}")`);

                res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
                res.end(JSON.stringify({
                    success: true,
                    message: 'Geri bildirim başarıyla kaydedildi.',
                    id: newFeedback.id,
                    totalCount: feedbacks.length
                }));
            } catch (err) {
                console.error('Hata:', err);
                res.writeHead(400, { 'Content-Type': 'application/json; charset=utf-8' });
                res.end(JSON.stringify({ success: false, message: 'Geçersiz JSON verisi.' }));
            }
        });
        return;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 2. GET /api/feedback — Kayıtlı bildirimleri listele (JSON)
    // ─────────────────────────────────────────────────────────────────────────────
    if (req.method === 'GET' && pathname === '/api/feedback') {
        const feedbacks = readFeedbacks();
        res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
        res.end(JSON.stringify({
            success: true,
            count: feedbacks.length,
            feedbacks: feedbacks
        }, null, 2));
        return;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 3. GET / — Modern Web Dashboard (Yönetici / İzleme Arayüzü)
    // ─────────────────────────────────────────────────────────────────────────────
    if (req.method === 'GET' && (pathname === '/' || pathname === '/dashboard')) {
        const feedbacks = readFeedbacks();
        const html = renderDashboardHtml(feedbacks);
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end(html);
        return;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 4. 404 Not Found
    // ─────────────────────────────────────────────────────────────────────────────
    res.writeHead(404, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({ success: false, message: 'Endpoint bulunamadı.' }));
});

function renderDashboardHtml(feedbacks) {
    const totalCount = feedbacks.length;
    const locationCounts = {};
    const conditionCounts = {};

    feedbacks.forEach(f => {
        locationCounts[f.location] = (locationCounts[f.location] || 0) + 1;
        conditionCounts[f.reportedCondition] = (conditionCounts[f.reportedCondition] || 0) + 1;
    });

    const topLocations = Object.entries(locationCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 3)
        .map(([loc, cnt]) => `${loc} (${cnt})`)
        .join(', ') || 'Henüz veri yok';

    const tableRows = feedbacks.map(f => {
        const dateStr = new Date(f.receivedAt).toLocaleString('tr-TR', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
        const tempBadge = f.currentTemp !== null && f.currentTemp !== undefined
            ? `<span class="badge badge-temp">${f.currentTemp}°C</span>`
            : '-';

        return `
            <tr>
                <td style="font-weight:600; color:#fff;">📍 ${f.location}</td>
                <td><span class="badge badge-condition">${f.reportedCondition}</span></td>
                <td>${tempBadge}</td>
                <td style="color:#e2e8f0;">${f.userComment || '<span style="color:#64748b; font-style:italic;">Yorum yok</span>'}</td>
                <td style="color:#94a3b8; font-size:12px;">${dateStr}</td>
            </tr>
        `;
    }).join('');

    return `<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hava Durumu Geri Bildirim Paneli</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body {
            background: linear-gradient(135deg, #141E30 0%, #243B55 100%);
            color: #f8fafc;
            min-height: 100vh;
            padding: 30px 20px;
        }
        .container { max-width: 1000px; margin: 0 auto; }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 1px solid rgba(255,255,255,0.12);
        }
        .header h1 { font-size: 24px; font-weight: 700; display: flex; align-items: center; gap: 10px; }
        .refresh-btn {
            background: rgba(255,255,255,0.15);
            color: #fff;
            border: 1px solid rgba(255,255,255,0.25);
            padding: 8px 16px;
            border-radius: 20px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 500;
            transition: all 0.2s;
        }
        .refresh-btn:hover { background: rgba(255,255,255,0.25); }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 25px;
        }
        .stat-card {
            background: rgba(255,255,255,0.07);
            backdrop-filter: blur(12px);
            border: 1px solid rgba(255,255,255,0.12);
            border-radius: 16px;
            padding: 18px;
        }
        .stat-card .label { font-size: 12px; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.5px; }
        .stat-card .value { font-size: 26px; font-weight: 700; margin-top: 6px; }
        .card {
            background: rgba(255,255,255,0.06);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(255,255,255,0.12);
            border-radius: 18px;
            padding: 22px;
            overflow-x: auto;
        }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th {
            font-size: 12px;
            color: #94a3b8;
            padding: 12px 14px;
            text-transform: uppercase;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        td {
            padding: 14px;
            font-size: 14px;
            border-bottom: 1px solid rgba(255,255,255,0.05);
        }
        tr:hover td { background: rgba(255,255,255,0.03); }
        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        .badge-condition { background: rgba(56, 189, 248, 0.2); color: #38bdf8; border: 1px solid rgba(56, 189, 248, 0.3); }
        .badge-temp { background: rgba(251, 146, 60, 0.2); color: #fb923c; border: 1px solid rgba(251, 146, 60, 0.3); }
        .empty-state { text-align: center; padding: 40px 10px; color: #94a3b8; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🌤️ Hava Durumu — Kullanıcı Bildirim Paneli</h1>
            <button class="refresh-btn" onclick="window.location.reload()">🔄 Yenile</button>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="label">Toplam Bildirim</div>
                <div class="value" style="color:#38bdf8;">${totalCount}</div>
            </div>
            <div class="stat-card">
                <div class="label">API Durumu</div>
                <div class="value" style="color:#4ade80; font-size:20px;">● Aktif (Port ${PORT})</div>
            </div>
            <div class="stat-card">
                <div class="label">En Çok Bildirilen Şehirler</div>
                <div class="value" style="font-size:15px; font-weight:500; margin-top:8px;">${topLocations}</div>
            </div>
        </div>

        <div class="card">
            <h3 style="margin-bottom:16px; font-size:16px; color:#f1f5f9;">📋 Son Kullanıcı Raporları</h3>
            ${totalCount === 0 ? `
                <div class="empty-state">
                    <p style="font-size:16px;">Henüz hiç geri bildirim alınmadı.</p>
                    <p style="font-size:13px; margin-top:6px; color:#64748b;">Mobil uygulamadan "Hava durumu yanlış mı? Bildir" butonuna basarak ilk bildirimi gönderebilirsiniz.</p>
                </div>
            ` : `
                <table>
                    <thead>
                        <tr>
                            <th>Konum</th>
                            <th>Gerçek Durum</th>
                            <th>Uygulama Sıcaklığı</th>
                            <th>Kullanıcı Açıklaması</th>
                            <th>Tarih</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${tableRows}
                    </tbody>
                </table>
            `}
        </div>
    </div>
</body>
</html>`;
}

server.listen(PORT, '0.0.0.0', () => {
    console.log(`=================================================`);
    console.log(`🚀 Hava Feedback Backend Çalışıyor!`);
    console.log(`📡 URL (Yerel):   http://localhost:${PORT}`);
    console.log(`📱 Mobil URL:     http://10.0.2.2:${PORT} (Android Emülatör)`);
    console.log(`📬 Endpoint:      POST http://localhost:${PORT}/api/feedback`);
    console.log(`=================================================`);
});
