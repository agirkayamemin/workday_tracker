# Workday Tracker App

Bu uygulama, çalışanların aylık ve günlük çalışma durumlarını, mesai ve normal çalışma saatlerini kolayca takip edebilmeleri için Cursor kullanarak geliştirilmiş bir Android uygulamasıdır.

## Ekran Görüntüleri

![Ana Ekran](screenshots/ana_ekran.png.jpg)
![Ay Detay](screenshots/ay_detay.png.jpg)

## Özellikler
- Her ay ve gün için çalışma durumu seçimi (Gidildi, Gidilmedi, Mesai, G+M)
- Girilen saatlerin kaydedilmesi (normal ve mesai)
- Tüm veriler cihazda kalıcı olarak saklanır (Room Database)
- Modern ve kullanıcı dostu arayüz (Jetpack Compose)

## Kurulum
1. Bu repoyu klonlayın:
   ```sh
   git clone https://github.com/agirkayamemin/workday_tracker.git
   ```
2. Android Studio ile açın.
3. Gerekli bağımlılıkların otomatik olarak kurulmasını bekleyin.
4. Uygulamayı bir emülatör veya gerçek cihazda çalıştırın.

## Kullanım
- Uygulama açıldığında, yılın tüm aylarını görebilirsiniz.
- Her ayın kartına tıklayarak günleri açabilirsiniz.
- Her gün için çalışma durumunu seçin ve gerekiyorsa saat bilgisini girin.
- Girdiğiniz tüm bilgiler cihazınızda saklanır, uygulama kapatılsa bile kaybolmaz.

## Kullanılan Teknolojiler
- Kotlin
- Jetpack Compose
- Room Database
- MVVM (Model-View-ViewModel)

## Katkı
Katkıda bulunmak isterseniz, lütfen bir fork oluşturun ve pull request gönderin.

---

**Geliştirici:** Muhammed Emin Agirkaya

# Ek Bilgi
Bu uygulama yapay zeka destekli kod yazım ve geliştirme platformu olan Cursor ile yapılmıştır.
