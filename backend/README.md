# 🌤️ Hava Uygulaması — Geri Bildirim & Hata Raporlama Backend'i

Bu backend servisi, Android uygulamasından gönderilen **"Hava durumu yanlış mı? Bildir"** raporlarını karşılar, kalıcı olarak `feedbacks.json` dosyasına kaydeder ve tarayıcı üzerinden canlı izleyebileceğiniz modern bir web paneli sunar.

---

## 🚀 Nasıl Çalıştırılır?

Backend klasörüne gidip Node.js ile tek komutla başlatabilirsiniz:

```bash
cd backend
node server.js
```
*(veya `npm start`)*

Çalıştığında konsolda şu çıktıyı görürsünüz:
```
=================================================
🚀 Hava Feedback Backend Çalışıyor!
📡 URL (Yerel):   http://localhost:5000
📱 Mobil URL:     http://10.0.2.2:5000 (Android Emülatör)
📬 Endpoint:      POST http://localhost:5000/api/feedback
=================================================
```

---

## 🖥️ Web Kontrol Paneli (Dashboard)

Sunucuyu başlattıktan sonra tarayıcınızda şu adresi açın:
👉 **[http://localhost:5000](http://localhost:5000)**

Bu panelde:
* Toplam bildirim sayısı
* En çok bildirim yapılan şehirler
* Kullanıcıların seçtiği gerçek hava durumu (☀️ Açık, 🌧️ Yağmurlu vb.)
* Kullanıcının yazdığı ek açıklamalar
* Bildirimin geldiği anlık tarih ve saat
modern ve şık bir tabloda listelenir.

---

## 📡 API Endpoint'leri

### 1. `POST /api/feedback`
Uygulamadan gelen hava durumu düzeltme bildirimini kaydeder.

**Örnek İstek (JSON):**
```json
{
  "location": "Tatvan",
  "latitude": 38.50,
  "longitude": 42.28,
  "reportedCondition": "Açık / Güneşli",
  "currentTemp": 14.0,
  "userComment": "Burada yağmur yağmıyor, hava tamamen açık.",
  "timestamp": 1729898000000
}
```

**Başarılı Cevap (200 OK):**
```json
{
  "success": true,
  "message": "Geri bildirim başarıyla kaydedildi.",
  "id": "1729898123456",
  "totalCount": 1
}
```

---

### 2. `GET /api/feedback`
Tüm kayıtlı geri bildirimleri JSON formatında listeler.

---

## 📱 Android Entegrasyonu
* Android Emülatöründe çalışan uygulama, bilgisayarın `localhost:5000` portuna `http://10.0.2.2:5000/` üzerinden bağlanır.
* Backend kapalı olsa bile Android uygulaması çökmez; bildirimi yerel olarak saklar ve kullanıcıya bildiriminin kaydedildiğini zarifçe iletir.
