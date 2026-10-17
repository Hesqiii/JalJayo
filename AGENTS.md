# JalJayo – Konteks Proyek untuk AI Asisten

> Taruh file ini di root repo. Baca seluruhnya sebelum menjawab atau menulis kode.
> Terakhir diperbarui: 17 Oktober 2026. Status: **Fase 0 (prototipe risiko) sedang berjalan**.

---

## 0. Cara membantu saya (WAJIB diikuti)

- Saya mahasiswa **Informatika semester 5**. Saya nyaman dengan pemrograman dan sering membuat website, tetapi **baru di Android native dan belum paham Jetpack Compose**.
- Jawab dalam **Bahasa Indonesia**. Identifier kode, nama class, dan komentar kode singkat boleh Bahasa Inggris.
- Jelaskan **kenapa**, bukan hanya apa. Kalau memakai konsep Compose/Android yang mungkin baru bagi saya, beri analogi singkat ke React/web.
- Kerjakan **langkah kecil**. Jangan mengubah banyak file sekaligus tanpa menjelaskan.
- **Jangan menambah library baru** tanpa menyebut alasannya dan meminta persetujuan saya.
- **Jangan mengarang API.** Kalau tidak yakin soal versi library, aturan Android 14/15/16, atau nama method, katakan tidak yakin dan arahkan saya memeriksa dokumentasi resmi di developer.android.com.
- Jangan menambahkan: login/akun, analytics, iklan, request jaringan, atau izin `INTERNET` (lihat Bagian 6).
- Kalau permintaan saya bertentangan dengan dokumen ini (scope, privasi, arsitektur), **peringatkan saya dulu**, jangan langsung menurut.

---

## 1. Ringkasan produk

**JalJayo** adalah aplikasi intervensi kebiasaan tidur untuk **mahasiswa baru Gen-Z (FT Universitas Negeri Malang)**. Proyek mata kuliah Manajemen Inovasi (Kelompok 2, Offering D6).

**Masalah:** bedtime procrastination dan racing thoughts. Dari kuesioner 27 responden FT UM, 81,5% tidur < 6 jam dan 77,8% doomscrolling di kasur karena overthinking.

**Pendekatan (tiga pilar):**
- **Siang:** edukasi kebiasaan lewat 1 kartu teks + 1 video pendek per hari.
- **Malam:** hanya pemutar audio relaksasi. Tanpa stimulasi visual, tanpa konten video.
- **Transisi:** pengingat lokal 45 menit sebelum jam tidur.

**Prinsip desain:** zero-login, zero-friction, offline-first, 100% data di perangkat.

**Target keberhasilan (OKR):**
- KR1: ≥ 70% pengguna aktif melaporkan main ponsel di kasur < 15 menit dalam 14 hari pertama.
- KR2: sleep onset latency turun ≥ 20 menit.
- KR3: retensi hari ke-7 ≥ 60%.

---

## 2. Keputusan platform dan alasannya (decision log)

| Keputusan | Alasan |
|---|---|
| **Android-first, iOS ditunda** | Tidak ada yang punya Mac di tim (build iOS butuh Mac/Xcode, uji di iPhone butuh signing). Mayoritas HP di Indonesia Android. Prioritas: Android bisa dipakai dan bisa didemokan. |
| **Native Android: Kotlin + Jetpack Compose (Material 3)** | Dua fitur tersulit di PRD (audio saat layar mati dan notifikasi terjadwal tanpa server) paling andal lewat API native. |
| **Bukan PWA murni** | Notifikasi terjadwal saat app ditutup tidak andal tanpa server push, padahal PRD melarang server. Fade-out volume di iOS Safari bermasalah. |
| **Bukan Flutter/React Native** | Menambah bahasa/framework baru tanpa keuntungan berarti kalau iOS ditunda. |
| **Bukan Kotlin Multiplatform dulu** | Setup Gradle KMP menambah beban, dan sisi iOS tidak bisa diuji. Logika inti dijaga murni Kotlin supaya bisa dipindah ke KMP nanti. |
| **`minSdk` 28 (Android 9)** | Sesuai NFR-04. |
| **MVVM, satu Activity, tanpa DI framework** | Layar hanya ~3, DI framework berlebihan. |
| **Media3 (ExoPlayer + MediaSessionService)** | Standar Android untuk audio background dengan foreground service dan kontrol lock screen. |
| **DataStore (Preferences), bukan Room** | Data hanya konfigurasi kecil dan status kartu. |
| **Konten di-bundle di app (JSON + MP4)** | Offline penuh, tanpa backend, biaya server nol. |
| **Distribusi lewat file APK** | Cukup untuk demo dan uji SUS. Play Store tidak perlu untuk proyek ini. |

---

## 3. Scope

**In-scope (MVP):**
1. Smart Diagnostic Micro-Onboarding (maks. 3 ketukan, < 60 detik).
2. One-Tap Wind-Down Player (Theta Waves 6Hz, Brown Noise, Suara Hujan; timer 15/30/45 menit; fade-out).
3. Daily Daylight Micro-Card (teks + video vertikal 30-45 detik, jam 06.00-20.00).
4. Local Bedtime Anchor + Night Lockout (notifikasi lokal 45 menit sebelum tidur; kunci konten pukul 20.00).
5. Menu Pengaturan (ubah jam bangun, pengingat, preset audio).

**Out-of-scope (jangan dikerjakan, jangan disarankan):**
- Registrasi, login, OAuth, verifikasi email.
- Gamifikasi (leaderboard, koin, badge sosial).
- Sensor/hardware (smartwatch, pelacak gerakan, mikrofon).
- Sinkronisasi cloud atau multi-device.
- iOS (ditunda sampai ada akses Mac).

---

## 4. Aturan domain (sumber kebenaran logika)

**Mode siang/malam (Night Lockout)**
- `DAY`: 06:00 ≤ jam < 20:00. `NIGHT`: selain itu (20:00-05:59).
- Mode `NIGHT`: UI hanya pemutar audio, layar gelap, tanpa video/teks edukasi, tanpa efek visual menyala.
- Saat berpindah ke `NIGHT` pada pukul 20.00, UI beralih otomatis (jangan menunggu app dibuka ulang). Tampilkan pesan: "Mode Malam Aktif. Konten video diistirahatkan untuk menjaga hormon tidurmu".

**Perhitungan jadwal tidur**
- `jamTidur = jamBangun − 7 jam 30 menit` (5 siklus tidur × 90 menit).
- `jamAnchor (notifikasi) = jamTidur − 45 menit`.
- Contoh: bangun 06:00 → tidur 22:30 → notifikasi 21:45. Harus melewati tengah malam dengan benar (contoh bangun 05:00 → tidur 21:30).

**Preset dari keluhan (onboarding)**
- "Otak muter terus / overthinking" → default audio `THETA_6HZ`.
- "Kamar bising / tidak tenang" → default audio `BROWN_NOISE`.
- Tombol "Lewati" → `THETA_6HZ`, bangun 06:00.
- Layar onboarding wajib menampilkan: "Pengaturan ini dapat Anda ubah kapan saja di menu Pengaturan".

**Timer dan fade-out**
- Pilihan timer: 15, 30, 45 menit (default 30).
- Saat sisa 2 menit terakhir: fade-out **eksponensial** dari 100% ke 0%, lalu berhenti otomatis.
- Timer dan fade dijalankan **di dalam service**, bukan di Activity/Composable.

**Kartu harian**
- 1 kartu per hari: teks ≤ 80 kata (≤ 45 detik baca) + 1 video vertikal ≤ 45 detik (BR-02).
- Setiap kartu wajib memuat **1 aksi kebiasaan konkret** (contoh: batas kafein 6 jam sebelum tidur).
- Kartu dipilih berdasarkan hari ke-N sejak instalasi. Status "Selesai Dibaca" disimpan lokal.
- Konten minimum MVP: **14 kartu** (selaras dengan KR1 14 hari).

**Aturan bisnis**
- BR-00: onboarding ≤ 60 detik (maks. 3 ketukan).
- BR-01: dilarang iklan, notifikasi promosi, atau efek visual menyala saat audio aktif.
- BR-02: teks ≤ 80 kata, video ≤ 45 detik.

**Pengecualian**
- Volume perangkat 0% saat Play → tampilkan pesan singkat: "Volume ponsel Anda dalam keadaan hening, harap naikkan volume".

---

## 5. Kebutuhan non-fungsional (disesuaikan untuk Android)

| ID | Kebutuhan | Catatan |
|---|---|---|
| NFR-01 | Audio mulai < 200 ms setelah tombol ditekan; app cepat dibuka | Target memori PRD (< 40 MB) **kemungkinan tidak realistis**. Ukur dulu, lalu revisi targetnya. |
| NFR-02 | Fungsi utama ≤ 2 ketukan; SUS ≥ 80 | Dari buka app ke audio berbunyi: ≤ 2 ketukan. |
| NFR-03 | Tidak ada pengumpulan nama, email, nomor HP, GPS, biometrik | Ganti klaim PRD "nol risiko kebocoran" menjadi "tidak ada data pengguna yang dikirim keluar perangkat". |
| NFR-04 | Android 9+ (`minSdk` 28) | iOS ditunda. Perlu dicatat sebagai keputusan scope di PRD. |

---

## 6. Privasi (dijaga lewat konfigurasi, bukan hanya janji)

- **Jangan** menambahkan izin `INTERNET` di manifest. Ini membuat klaim "tidak ada data keluar" bisa diverifikasi.
- Jangan menambahkan SDK analytics, crash reporter berbasis cloud, atau iklan.
- Semua data (konfigurasi, status kartu, riwayat sesi) hanya di DataStore lokal.

---

## 7. Arsitektur

**Target struktur (bertahap; Fase 0 boleh satu modul dulu):**

```
:core        (modul Kotlin murni, TANPA import android.*)
  Mode.kt                 enum DAY/NIGHT + getMode(time)
  SleepSchedule.kt        hitung jam tidur & anchor
  ComplaintPreset.kt      mapping keluhan -> preset audio
  AppClock.kt             interface jam (bisa disuntik untuk tes & demo)

:app
  ui/
    onboarding/           layar diagnostik
    home/                 DayScreen, NightScreen
    settings/             pengaturan
    theme/
  audio/
    PlaybackService.kt    MediaSessionService + player
    SleepTimer.kt         timer + fade-out eksponensial
  notification/
    BedtimeScheduler.kt   AlarmManager
    BedtimeReceiver.kt    tampilkan notifikasi
    BootReceiver.kt       jadwalkan ulang setelah restart
  data/
    SettingsRepository.kt DataStore
    ContentRepository.kt  baca JSON kartu dari assets
    CardProgress.kt       status "Selesai Dibaca"
  debug/
    DebugClock.kt         jam virtual untuk demo (hanya build debug)
```

**Aturan arsitektur**
- Logika yang menentukan perilaku (`getMode`, jadwal tidur, preset) **hanya** di `:core`, berupa fungsi murni dan **bisa diuji dengan JUnit**. Jam harus disuntik lewat `AppClock`, jangan memanggil `LocalTime.now()` langsung di logika.
- UI: Compose + `ViewModel` + `StateFlow`; di layar gunakan `collectAsStateWithLifecycle()`.
- **Jangan taruh efek samping di fungsi `@Composable`** (memutar audio, menjadwalkan alarm, menulis DataStore). Taruh di ViewModel, service, atau `LaunchedEffect`.
- Perpindahan mode pukul 20.00: pakai `LaunchedEffect` yang mengecek jam berkala, atau jadwalkan sesuai kebutuhan. Jelaskan pilihannya sebelum menulis.
- Player hidup di `PlaybackService`. Activity hanya "remote control" lewat `MediaController`.

---

## 8. Spesifik Android (verifikasi dengan dokumentasi resmi sebelum dipakai)

**Audio (Media3)**
- Dependency: `media3-exoplayer` dan `media3-session`. Versi stabil terbaru **harus dicek** di halaman rilis Media3 (kandidat saat ini: 1.10.x, belum diverifikasi).
- `PlaybackService : MediaSessionService`, `ExoPlayer` dengan `setAudioAttributes(..., handleAudioFocus = true)`, `setHandleAudioBecomingNoisy(true)`, `setWakeMode(C.WAKE_MODE_LOCAL)`, `repeatMode = REPEAT_MODE_ONE`.
- Aset: file loop pendek di `res/raw/` (contoh `brown_noise.ogg`). Durasi 15/30/45 diatur timer, bukan file panjang. Cek celah (gap) saat loop.
- Fade-out: ubah `player.volume` lewat coroutine di service dengan kurva eksponensial.

**Manifest**
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />

<service
    android:name=".PlaybackService"
    android:foregroundServiceType="mediaPlayback"
    android:exported="true">
    <intent-filter>
        <action android:name="androidx.media3.session.MediaSessionService" />
    </intent-filter>
</service>
```

**Notifikasi terjadwal**
- `AlarmManager` (`setExactAndAllowWhileIdle`) + `BroadcastReceiver`. Jadwalkan ulang di `BOOT_COMPLETED`.
- Izin runtime `POST_NOTIFICATIONS` (Android 13+). Aturan alarm presisi berubah di Android 12/13/14+ (`SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`), jadi **cek dokumentasi terbaru**. Opsi lain yang perlu dibandingkan: `setAlarmClock()`.
- HP target pengembangan: Android 16. HP uji kedua: merek berbeda (Xiaomi/Oppo/Vivo/Samsung) karena penghemat baterai vendor sering mematikan service latar belakang.

**Penyimpanan**
- DataStore Preferences untuk: jam bangun, preset audio, jadwal pengingat, hari instalasi, status kartu selesai, riwayat sesi sederhana.

---

## 9. Roadmap dan status

- [x] Keputusan stack dan arsitektur
- [ ] **Fase 0: Prototipe risiko** (di HP asli, wireless debugging)
  - [ ] Setup proyek (Empty Activity Compose, `minSdk` 28, Kotlin DSL) dan Run ke HP
  - [ ] Audio loop via `MediaSessionService` tetap jalan saat layar mati 30+ menit (HP tidak dicas, debugger tidak terhubung)
  - [ ] Audio tetap jalan setelah app di-swipe dari recents
  - [ ] Timer + fade-out eksponensial di service
  - [ ] Notifikasi terjadwal muncul setelah app di-swipe dan setelah HP restart
  - [ ] Ulangi semua uji di HP merek lain; catat perilaku penghemat baterai
- [ ] Fase 1: Modul `:core` + tes unit (mode, jadwal tidur, preset)
- [ ] Fase 2: Pemutar malam, Night Lockout, mode malam tanpa stimulasi visual
- [ ] Fase 3: Onboarding, kartu dan video harian, pengaturan, pengingat
- [ ] Fase 4: Pengukuran (waktu start, memori), uji SUS, persiapan demo
- [ ] Nanti: iOS (butuh akses Mac), pertimbangkan migrasi ke KMP

Kriteria lulus Fase 0: jika audio atau notifikasi tidak bisa andal di Android native, **hentikan dan diskusikan ulang rencana** sebelum membuat fitur lain.

---

## 10. Pengujian dan demo

- **Tes unit:** semua fungsi di `:core` (termasuk jam tidur yang melewati tengah malam dan batas tepat 06:00 dan 20:00).
- **Uji manual di HP asli:** audio layar mati, swipe dari recents, restart HP, notifikasi terjadwal, audio focus saat pindah app.
- **Uji paling jujur:** app terpasang, HP tidak dicas (mode Doze berlaku lebih ketat saat tidak dicas), layar mati, debugger terputus.
- **Menu debug (hanya build debug):** geser jam virtual (contoh "anggap sekarang 19.59") dan picu notifikasi dalam 1 menit. Tanpa ini Night Lockout dan Bedtime Anchor tidak bisa didemokan live.
- **Demo:** matikan optimasi baterai untuk JalJayo di HP demo, siapkan HP cadangan, tampilkan layar lewat scrcpy, distribusi uji SUS lewat APK.

---

## 11. Risiko dan pertanyaan terbuka

| # | Risiko / pertanyaan | Mitigasi / status |
|---|---|---|
| 1 | Penghemat baterai vendor Android mematikan service/alarm | Uji di HP merek berbeda; dialog panduan 1 langkah untuk menonaktifkan optimasi baterai JalJayo |
| 2 | Aturan izin alarm presisi dan foreground service berbeda per versi Android | Cek dokumentasi resmi saat implementasi |
| 3 | Target RAM < 40 MB (NFR-01) | Ukur di HP asli, revisi target |
| 4 | "Theta Waves 6Hz" diimplementasikan sebagai apa (binaural beat butuh headphone stereo, isochronic tone, atau lainnya)? | **Belum diputuskan.** Klaim manfaat ilmiahnya perlu hati-hati saat presentasi. Bahas dengan tim. |
| 5 | Siapa yang memproduksi 14+ kartu dan video (naskah, rekaman, kompresi)? | **Belum diputuskan.** Mulai dari konten awal agar tidak menjadi bottleneck. |
| 6 | Package name final (tentatif: `id.jaljayo.app`) | Putuskan sebelum rilis pertama |
| 7 | iOS | Ditunda; butuh akses Mac dan akun developer |

**Revisi yang perlu dicatat di PRD:**
- NFR-04 → "Android 9+ pada v1; iOS fase berikutnya".
- Bagian 8 (Batasan Teknis) → ganti localStorage / Web Notification / PWA dengan DataStore / AlarmManager / Media3.
- Risiko 2 (iOS Web Notification) → pindahkan ke daftar risiko fase berikutnya.
- NFR-01 dan NFR-03 → sesuaikan seperti Bagian 5.
