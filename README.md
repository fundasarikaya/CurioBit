# CurioBit

Her gün dünya tarihinden küçük bir kırıntı. CurioBit, Türkçe Vikipedi'nin
"Tarihte bugün" verisinden (olaylar, doğumlar ve ölümler) seçtiği kısa bir
bilgiyi günde 1–2 kez bildirim olarak gönderir. Bildirime dokununca bilginin ayrıntısı ve Vikipedi kaynağı
açılır. Aynı bilgi iki kez gösterilmez.

Arayüz eski bir gazete sayfası gibi tasarlandı: sepya kâğıt, mürekkep rengi
yazılar ve tek bir pas kahvesi vurgu rengi. Açık ve koyu tema desteklenir.

## Özellikler

- **Ana sayfa:** Günün bilgisi manşet olarak, altında arşivden iki bilgi.
- **Detay:** Olayın tam metni, ilgili Vikipedi özeti ve makaleye bağlantı.
- **Beğen / beğenme:** Beğenilen bilgiler favorilere eklenir. Beğenilmeyen bir
  bilginin konusu sonraki seçimlerde geri plana atılır.
- **Başka bir bilgi:** Günün bilgisi gün boyunca sabit kalır; yenisi butonla
  ya da sayfayı aşağı çekerek istenir.
- **Dönem seçimi:** Bilgiler tüm yıllardan ya da seçilen dönemden (2010+,
  2000–09, 1900–99, 1900 öncesi) gelir. Bildirimler de seçime uyar.
- **Arşiv:** Son 7 günde gösterilen bilgiler ve tüm favoriler.

## Teknolojiler

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) ve
  [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/):
  Android ve iOS için ortak arayüz
- [Ktor](https://ktor.io/): Vikipedi REST API istekleri
- [SQLDelight](https://sqldelight.github.io/sqldelight/): Yerel veritabanı
  (gösterilen bilgiler, tepkiler)
- Android'de WorkManager, iOS'ta UserNotifications ile bildirimler

## Proje yapısı

```
composeApp/
  src/commonMain/   Ortak kod: arayüz, tema, veri katmanı
  src/androidMain/  Android'e özel kod: bildirim işçisi, veritabanı sürücüsü
  src/iosMain/      iOS'a özel kod: bildirim zamanlayıcı, veritabanı sürücüsü
iosApp/             Xcode projesi
```

## Çalıştırma

**Android:** Projeyi Android Studio ile açıp `composeApp` yapılandırmasını
çalıştırın ya da komut satırından:

```bash
./gradlew :composeApp:installDebug
```

**iOS:** `iosApp/iosApp.xcodeproj` dosyasını Xcode ile açıp bir simülatörde
çalıştırın.

## Veri kaynağı

Bilgiler [Türkçe Vikipedi](https://tr.wikipedia.org/)'nin "Tarihte bugün"
REST API'sinden (`/api/rest_v1/feed/onthisday/all`) alınır. Olaylar, doğumlar
ve ölümler 2:1:1 ağırlıkla seçilir; doğumlar sayıca çok olduğu için bu denge
gözetilir. Vikipedi
içeriği [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)
lisansı altındadır.

## Fontlar

Uygulamada şu fontlar kullanılır. Hepsi
[SIL Open Font License 1.1](https://openfontlicense.org/) altındadır ve lisans
metinleri [`LICENSES/fonts`](LICENSES/fonts) klasöründedir.

| Font | Kullanım |
|---|---|
| Old Standard TT | Künye ve manşetler |
| Libre Caslon Text | Gövde metni |
| Libre Franklin | Etiketler |

## Lisans

Kod [MIT lisansı](LICENSE) ile paylaşılmaktadır. Fontlar kendi lisanslarına
(SIL OFL 1.1) tabidir.
