# 🌙 JalJayo (Good Sleep, Habits)

> **Aplikasi Intervensi Kebiasaan Tidur & Pereda *Racing Thoughts* Mahasiswa Gen-Z**  
> Proyek Mata Kuliah **Manajemen Inovasi** — Kelompok 2, Offering D6  
> Fakultas Teknik, Universitas Negeri Malang

[![Platform](https://img.shields.io/badge/Platform-Android%20Native-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Audio](https://img.shields.io/badge/Audio-AndroidX%20Media3%20ExoPlayer-FF6F00)](https://developer.android.com/media/media3)
[![minSdk](https://img.shields.io/badge/minSdk-28%20(Android%209.0+)-blue)](#)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline%20(No%20Internet)-success)](#)

---

## 📌 Latar Belakang Masalah

Berdasarkan riset kuesioner internal terhadap **27 responden mahasiswa Fakultas Teknik Universitas Negeri Malang (FT UM)**:
* **81,5%** mahasiswa mengalami durasi tidur defisit (< 6 jam per hari).
* **77,8%** terjebak siklus *doomscrolling* media sosial di atas kasur hingga larut malam akibat *overthinking* tugas kuliah, praktikum, dan masa depan (*racing thoughts*).

Aplikasi tidur konvensional seringkali gagal karena:
1. Mengharuskan registrasi dan login yang rumit (*high friction*).
2. Menyajikan modul bacaan panjang di malam hari yang justru menyilaukan mata dan menunda tidur.
3. Memerlukan koneksi internet dan berlangganan berbayar.

**JalJayo** hadir dengan pendekatan **Zero-Login, Zero-Friction, 100% Data di Perangkat (Offline-First)** yang berlandaskan ilmu ritme sirkadian.

---

## 🏛️ Tiga Pilar Intervensi Sirkadian

```
           ☀️ SIANG (06:00 - 20:00)           🔔 TRANSISI (-45 Mnt)         🌙 MALAM (20:00 - 05:59)
     ┌───────────────────────────────────┐    ┌────────────────────┐    ┌───────────────────────────────┐
     │  Edukasi Sains Tidur Mikro        │    │  Bedtime Anchor    │    │  One-Tap Wind-Down Player     │
     │  - 1 Kartu Bacaan Singkat (≤80 kta)│ ─► │  - Notifikasi jam  │ ─► │  - UI Gelap Pekat (OLED Black)│
     │  - 1 Video Vertikal Mikro (35 dtk)│    │    weker lokal     │    │  - Layar Mati Audio Berputar  │
     │  - 1 Aksi Kebiasaan Nyata Hari Ini│    │  - Bangunkan layar │    │  - Cubic Fade-Out Timer       │
     └───────────────────────────────────┘    └────────────────────┘    └───────────────────────────────┘
```

1. **☀️ Siang Hari (*Daylight Habits* | 06.00 – 20.00):**
   Membangun kebiasaan tidur dari siang hari lewat **1 kartu edukasi sains tidur harian** (bacaan $\le$ 80 kata) dan **1 video edukasi vertikal pendek (35 detik)** berisikan 1 aksi nyata (misal: aturan batas kafein 6 jam sebelum tidur).
2. **🔔 Transisi Tidur (*Local Bedtime Anchor* | 45 Menit Sebelum Tidur):**
   Notifikasi jam weker presisi via `AlarmManager` yang membangunkan layar kunci ponsel dan mengajak mahasiswa mulai menutup laptop/tugas untuk bersiap istirahat.
3. **🌙 Malam Hari (*Zero-Visual Wind-Down* | 20.00 – 05.59):**
   **Night Lockout aktif otomatis pukul 20.00.** Akses konten video dan bacaan diistirahatkan untuk menjaga hormon melatonin. Layar berubah menjadi pemutar audio gelap tanpa stimulasi visual menyala dengan kurva peredupan otomatis (*cubic exponential fade-out*).

---

## ✨ Fitur Utama Aplikasi

- **⚡ Smart Diagnostic Micro-Onboarding (BR-00):** Selesai dalam $\le$ 3 ketukan visual (< 60 detik) saat pertama kali buka. Otomatis memetakan keluhan:
  - *Otak muter terus / overthinking* $\to$ Preset **Theta Waves 6Hz**.
  - *Kamar bising / tidak tenang* $\to$ Preset **Brown Noise**.
  - Menghitung jadwal tidur sirkadian otomatis (5 siklus tidur $\times$ 90 menit = 7,5 jam).
- **🎧 One-Tap Wind-Down Player:**
  - 4 Preset Audio Relaksasi: **Theta Waves 6Hz**, **Brown Noise**, **Suara Hujan**, dan **Ombak Laut**.
  - **Tahan Layar Mati (Screen-off playback):** Audio tetap stabil berputar di latar belakang meski HP dikunci atau aplikasi di-swipe dari recent apps.
  - **Cubic Fade-Out Sleep Timer:** Pilihan durasi 15, 30, atau 45 menit. Pada sisa 2 menit terakhir, volume meredup secara halus dengan rumus matematis $(1 - progress)^3$ tanpa mengejutkan otak.
  - Peringatan cerdas jika volume perangkat dalam keadaan 0% / hening (*UCS-01 Exc-1*).
- **🔒 Night Lockout Otomatis:**
  - Pukul 20.00: Aplikasi otomatis mengunci konten edukasi dan menampilkan pesan: *"Mode Malam Aktif. Konten video diistirahatkan untuk menjaga hormon tidurmu."*
  - Pukul 06.00: Aplikasi kembali ke Mode Siang.
- **📅 14 Hari Kartu Edukasi Tidur (Offline JSON):**
  - Berisi 14 materi kebiasaan sirkadian lengkap tanpa koneksi server. Status "Sudah Dibaca" dicatat secara lokal.
- **🎬 Pemutar Video Vertikal Ramah Gawai (Reels/Shorts style):**
  - Pratinjau video mikro vertikal 9:16 di siang hari dengan penghitung waktu sirkadian dan tombol aksi selesai.
- **⚙️ Pengaturan Mandiri & Riwayat Sesi Lokal:**
  - Ubah target jam bangun, amati pratinjau kalkulasi sirkadian secara dinamis, ganti preset audio default, dan lihat total sesi relaksasi yang berhasil diselesaikan.
- **🛠️ Panel Khusus Demo Dosen & Pengujian:**
  - Tombol simulasi waktu instan: **"Paksa Siang"** dan **"Paksa Malam"** (memungkinkan demo Night Lockout kapan saja di depan dosen).
  - Pemicu alarm tes 10 detik (verifikasi notifikasi jam weker di layar kunci).
  - Dialog satu-langkah pembebasan optimasi baterai (pencegahan sistem XOS/MIUI membekukan aplikasi).

---

## 🏗️ Arsitektur Proyek

JalJayo dibangun menggunakan arsitektur multi-modul yang rapi (*Clean Architecture & Separation of Concerns*):

```
JalJayo/
├── :core                    # Modul Kotlin Murni (TANPA android.* SDK)
│   ├── Mode.kt              # Enum DAY/NIGHT & fungsi evaluasi waktu getMode()
│   ├── SleepSchedule.kt     # Algoritma sirkadian 5 siklus & midnight crossing
│   ├── ComplaintPreset.kt   # Pemetaan keluhan onboarding ke preset audio
│   └── AppClock.kt          # Abstraksi jam sistem / jam virtual untuk tes
│
└── :app                     # Modul Android Native & Jetpack Compose
    ├── audio/
    │   ├── PlaybackService.kt # MediaSessionService Media3 + ExoPlayer
    │   └── SleepTimer.kt      # Timer coroutine + fade-out kubik di service
    ├── data/
    │   ├── SettingsRepository.kt # DataStore Preferences (konfigurasi & riwayat sesi)
    │   └── ContentRepository.kt  # Parser kartu edukasi JSON dari assets
    ├── notification/
    │   ├── BedtimeScheduler.kt   # AlarmManager setAlarmClock presisi tinggi
    │   ├── BedtimeReceiver.kt    # Pemicu notifikasi heads-up di lock screen
    │   └── BootReceiver.kt       # Pemulih alarm otomatis setelah ponsel reboot
    └── ui/
        ├── home/                 # DayScreen.kt & NightScreen.kt
        ├── onboarding/           # OnboardingScreen.kt (diagnostik kilat)
        ├── settings/             # SettingsScreen.kt (pengaturan sirkadian)
        └── MainActivity.kt       # Single-activity controller & routing
```

---

## 💻 Tech Stack & Kebutuhan Sistem

| Komponen | Teknologi yang Digunakan | Alasan Pemilihan |
|---|---|---|
| **Bahasa** | Kotlin 2.0 (Kotlin DSL) | Bahasa resmi Android modern, null-safety, dan coroutines. |
| **UI Toolkit** | Jetpack Compose (Material 3) | Declarative UI reaktif, modern, dan performa tinggi tanpa XML. |
| **Audio Engine** | AndroidX Media3 (ExoPlayer 1.11.1) | Standar resmi Google untuk background audio dan kontrol lock screen. |
| **Penyimpanan** | Jetpack DataStore Preferences | Pengganti modern SharedPreferences, asinkron via Coroutine Flow. |
| **Penjadwal** | Android `AlarmManager` (`setAlarmClock`) | Membangunkan Doze mode & bypass freezer baterai vendor Android. |
| **Kebutuhan OS** | Android 9.0+ (`minSdk` 28 s/d Android 16) | Mencakup >92% perangkat Android aktif di Indonesia. |
| **Izin Internet** | **NOL (Tidak ada izin `INTERNET`)** | Menjamin 100% privasi: data pengguna tidak pernah keluar dari HP. |

---

## 🚀 Panduan Menjalankan Aplikasi

### 1. Prasyarat Sistem
* **Android Studio:** Versi *Ladybug*, *Koala*, atau yang lebih baru.
* **JDK:** Java Development Kit versi 17 atau 21.
* **Perangkat Uji:** Ponsel Android fisik (direkomendasikan) atau Android Emulator dengan API Level 28 ke atas.

### 2. Kloning & Buka Proyek
```bash
git clone https://github.com/USERNAME/JalJayo.git
```
Buka Android Studio, pilih **Open**, lalu arahkan ke folder `JalJayo`. Tunggu hingga proses **Gradle Sync** selesai.

### 3. Menjalankan Tes Unit (Unit Test)
Modul `:core` memiliki 18 pengujian unit otomatis yang memvalidasi logika sirkadian dan pergantian mode melewati tengah malam.
```powershell
.\gradlew test
```
*Hasil yang diharapkan: **BUILD SUCCESSFUL** (18/18 tes lulus 100%).*

### 4. Melakukan Build APK Debug
Untuk membuat file APK siap instal:
```powershell
.\gradlew assembleDebug
```
File APK akan ter-generate di:  
`app/build/outputs/apk/debug/app-debug.apk`

### 5. Memasang ke HP Fisik via ADB
Pastikan USB Debugging atau Wireless Debugging aktif di ponselmu:
```powershell
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
```

---

## 🎬 Panduan Demo di Depan Dosen

Aplikasi telah dilengkapi **Menu Demo** bawaan sehingga kamu tidak perlu menunggu jam 20.00 malam atau menyetel jam HP secara manual:

1. **Demo Smart Onboarding:**
   - Hapus data aplikasi atau instal pertama kali.
   - Pilih keluhan *"Otak muter terus"* $\to$ Pilih jam bangun `06:00` $\to$ Tekan *"Mulai JalJayo"*. Tunjukkan bahwa sistem otomatis menghitung jam tidur `22:30` dan pengingat `21:45`.
2. **Demo Edukasi Siang & Video Vertikal (Pilar Siang):**
   - Di beranda siang, tunjukkan ringkasan jadwal sirkadian dan kartu aksi hari ini.
   - Ketuk kartu video vertikal: tampilkan pemutar video mikro 35 detik interaktif.
3. **Demo Night Lockout Instan (Simulasi Waktu):**
   - Ketuk menu **🛠️ Menu Demo** di bagian bawah layar.
   - Pilih opsi **"Paksa Malam"**.
   - Perhatikan bahwa UI seketika terkunci menjadi layar gelap pekat tanpa video/teks dengan pesan Snackbar resmi.
4. **Demo Audio Latar Belakang & Fade-Out (Pilar Malam):**
   - Tekan tombol **Play** (One-Tap). Audio Theta Waves/Brown Noise akan langsung berbunyi (< 200 ms).
   - Kunci layar HP (*lock screen*): tunjukkan bahwa audio tetap berputar jernih dan kontrol Media Session muncul di lock screen.
   - Hapus (*swipe-up*) aplikasi dari daftar *Recent Apps*: tunjukkan bahwa audio tidak mati.
5. **Demo Notifikasi Jam Weker (Pilar Transisi):**
   - Di panel Menu Demo, tekan tombol **"Picu Alarm (10 dtk)"**, lalu langsung kunci layar HP.
   - Tepat setelah 10 detik, notifikasi *Bedtime Anchor* akan berbunyi dan menyalakan layar kunci HP.

---

## 👥 Tim Pengembang (Kelompok 2 - Offering D6)

Proyek ini dikembangkan untuk memenuhi tugas mata kuliah **Manajemen Inovasi**, Program Studi S1 Teknik Informatika, Fakultas Teknik, **Universitas Negeri Malang**:

* **Kelompok 2 (Offering D6)**
* **Fokus Riset:** *Sleep Intervention & Habit Formation for Freshmen Gen-Z*

---

## 📄 Lisensi & Hak Cipta

Dilisensikan di bawah lisensi akademik untuk keperluan edukasi dan riset inovasi di lingkungan Universitas Negeri Malang. Hak cipta © 2026 Tim Pengembang JalJayo.
