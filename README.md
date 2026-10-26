# 🌤️ Hava — Modern Android Hava Durumu Uygulaması

Jetpack Compose ile geliştirilmiş; Türkiye'nin tüm il ve ilçelerini destekleyen, günün saatine göre değişen dinamik atmosferik arka plan degradelerine sahip, modern cam efektli (glassmorphic) arayüzü ve kullanıcı hata bildirim sistemi olan şık bir Android hava durumu uygulaması.

---

## ✨ Özellikler

* **🎨 Zamana Göre Dinamik Atmosferik Arka Plan:**
  * **🌅 Sabah (06:00 - 12:00):** Güneşli, taze ve ferah gökyüzü mavisi degrade.
  * **🌇 İkindi / Gün Batımı (12:00 - 18:00):** Sıcak altın sarısı, kehribar ve gün batımı alacakaranlık tonları.
  * **🌆 Akşam (18:00 - 24:00):** Derin akşam mavisi ve serinliği.
  * **🌌 Gece (24:00 - 06:00):** Dingin dumanlı gece laciverti ve parıldayan yıldızlar.
* **📍 81 İl ve Tüm İlçe Desteği:**
  * Tatvan, Ahlat, Gemlik gibi tüm ilçeler bağlı olduğu ilin plaka koduyla birlikte görüntülenir.
  * İlçe alt başlıklarında resmi il adı gösterilir (`🇹🇷 Bitlis, Türkiye`).
* **🔎 İkili Arama Mantığı:**
  * **Sol Üstte `+`:** Şehirleri kalıcı olarak sekmelere ekleme.
  * **Sağ Üstte `🔍`:** Listeye eklemeden anlık hızlı hava durumuna bakma.
* **⏱️ 24 Saatlik & 5 Günlük Tahmin:**
  * Her saatin kendi doğuş/batış durumuna göre güneş (`☀️`) ve ay (`🌙`) simgeleri.
  * Nem, rüzgar hızı/yönü, hava basıncı ve yağış miktarı metrik kartları.
* **📢 Kullanıcı Geri Bildirimi & Hata Raporlama:**
  * "Hava durumu yanlış mı? Bildir" butonu ile anlık hava durumu düzeltmesi bildirebilme.
  * Çevrimdışı kayıt (offline fallback) desteği.
* **🖥️ Node.js Canlı Web Paneli:**
  * Gelen bildirimleri anlık olarak `http://localhost:5000` üzerinden listeleyen modern web kontrol paneli.

---

## 🛠️ Kullanılan Teknolojiler

* **Android / Kotlin:**
  * Jetpack Compose & Material 3
  * Coroutines & Flow (StateFlow, Asenkron Mimari)
  * ViewModel (MVVM)
  * Retrofit 2 & OkHttp 4
  * Open-Meteo Geocoding & Forecast API
  * Fused Location Provider
* **Backend:**
  * Node.js HTTP Server & File Persistence (`feedbacks.json`)
  * Glassmorphism Web Dashboard

---

## 🚀 Çalıştırma

1. Projeyi Android Studio ile açın ve **Run (▶)** butonuna basın.
2. Geri bildirim backend'ini başlatmak için:
   * `backend/start.bat` dosyasına çift tıklayın *(veya `cd backend && node server.js`)*.
   * Tarayıcınızdan `http://localhost:5000` adresini açarak gelen raporları izleyebilirsiniz.
