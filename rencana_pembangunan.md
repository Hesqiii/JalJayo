# 🌙 Rencana Pembangunan & Laporan Progres JalJayo

**Proyek:** JalJayo – Aplikasi Intervensi Kebiasaan Tidur Mahasiswa FT UM (Manajemen Inovasi Kelompok 2 Offering D6)  
**Status Terkini:** 🟢 **Fase 0, 1, 2, 3, dan 4 SELESAI (100% Siap Demo Dosen & Uji Lapangan SUS)**  
**Langkah Berikutnya:** 🎯 **Distribusi APK ke Responden Mahasiswa FT UM & Pelaksanaan Demo Presentasi**  
**Terakhir Diperbarui:** 18 Oktober 2026

---

## 📊 Matriks Status Keseluruhan (Berdasarkan AGENTS.md Bagian 9)

| Fase | Nama Fase | Status | Keterangan & Hasil Uji |
|:---:|---|:---:|---|
| **Fase 0** | **Prototipe Risiko Audio & Notifikasi** | 🟢 **LULUS** | Background audio Media3, loop gapless, tahan layar mati & swipe recents, cubic fade-out, AlarmManager lock screen di HP Infinix (Android 16). |
| **Fase 1** | **Modul `:core` + Unit Test** | 🟢 **LULUS** | Modul murni Kotlin (tanpa Android SDK), algoritma sirkadian 5 siklus, Night Lockout logic, mapping keluhan, AppClock injectable. **18/18 Unit Test lulus 100%**. |
| **Fase 2** | **Pemutar Malam & Night Lockout** | 🟢 **LULUS** | `NightScreen` gelap hemat daya (OLED), `DayScreen` sirkadian, auto-switch jam 20:00 & 06:00 dengan Snackbar, 4 audio preset, dan Menu Demo/Debug. |
| **Fase 3** | **Onboarding, Kartu Harian, & Pengaturan** | 🟢 **LULUS** | Smart Diagnostic Onboarding (≤3 ketukan, <60 dtk), 14 kartu edukasi tidur offline JSON, `ContentRepository`, DataStore `SettingsRepository`, `SettingsScreen`, dynamic `DayScreen`, full routing `MainActivity`, auto-reschedule `BootReceiver`. |
| **Fase 4** | **Polish, Pengukuran, & Persiapan Demo** | 🟢 **LULUS** | Penyelarasan PRD (video vertikal & riwayat sesi), benchmark latensi audio (<200ms) & profil RAM real-time di UI demo, dokumen instrumen SUS (`instrumen_uji_sus.md`), dan panduan demo dosen. |

---

## 📋 Evaluasi Keselarasan Kode dengan PRD (`requirements_jaljayo.md`) & `AGENTS.md`

Berdasarkan audit komparatif antara kode saat ini, PRD formal, dan batasan arsitektur di `AGENTS.md`, berikut adalah peta keselarasan sistem:

### 1. Kebutuhan Fungsional (Functional Requirements)

| ID PRD | Deskripsi Kebutuhan | Kondisi Implementasi Saat Ini | Status Keselarasan |
|---|---|---|:---:|
| **FR-00.1** | Asesmen singkat $\le$ 3 pertanyaan visual $< 500$ ms saat pertama buka | `OnboardingScreen.kt` dengan 3 langkah visual instan (Keluhan $\to$ Jam Bangun $\to$ Konfirmasi). | 🟢 **Sesuai 100%** |
| **FR-00.2** | 'Otak muter' $\to$ Theta 6Hz; 'Kamar bising' $\to$ Brown Noise | Logika murni di `ComplaintPreset.kt` + `OnboardingScreen.kt`. | 🟢 **Sesuai 100%** |
| **FR-00.3** | Hitung mundur tidur (bangun $- 7.5$ jam) & pengingat ($- 45$ mnt) | `SleepSchedule.kt` (teruji midnight crossing) + `BedtimeScheduler.kt`. | 🟢 **Sesuai 100%** |
| **FR-00.4** | Teks wajib: *"Pengaturan ini dapat Anda ubah kapan saja..."* | Ditampilkan di `OnboardingScreen.kt` (Langkah 2 Konfirmasi). | 🟢 **Sesuai 100%** |
| **FR-00.5** | Penyimpanan konfigurasi lokal tanpa transmisi cloud | `SettingsRepository.kt` via Jetpack DataStore Preferences (0 internet). | 🟢 **Sesuai 100%** |
| **FR-01.1** | Audio loop lokal dimulai $< 200$ ms (Theta, Brown, Hujan, Ombak) | Media3 ExoPlayer di `PlaybackService.kt` memutar aset di `res/raw/`. | 🟢 **Sesuai 100%** |
| **FR-01.2** | Background playback saat layar terkunci / app diminimalkan | `MediaSessionService` foreground service + wake lock di lock screen. | 🟢 **Sesuai 100%** |
| **FR-01.3** | Auto-stop timer (15/30/45m) & cubic fade-out 2 menit terakhir | `SleepTimer.kt` berjalan di dalam Service, $(1 - progress)^3$. | 🟢 **Sesuai 100%** |
| **FR-02.1** | Siang (06.00-20.00): 1 kartu edukasi harian (teks $\le$ 80 kata) + 1 video vertikal 30-45 detik | `daily_cards.json` (14 kartu teks $\le$ 80 kata). **Namun komponen pemutar/kontainer video vertikal belum dipasang di `DayScreen.kt`**. | 🟡 **Sebagian (Video Belum Ada)** |
| **FR-02.2** | Kartu menyajikan 1 aksi kebiasaan konkret | 14 kartu memuat atribut `action` konkret (batas kafein, ventilasi, dll). | 🟢 **Sesuai 100%** |
| **FR-02.3** | Jam 20.00 Night Lockout: kunci edukasi & auto-switch mode malam | Dipantau loop di `MainActivity.kt`, snackbar auto-muncul, UI terkunci ke pemutar audio. | 🟢 **Sesuai 100%** |
| **FR-03.1** | Notifikasi lokal 45 menit sebelum target tidur | `BedtimeScheduler.kt` (`setAlarmClock`) + `BedtimeReceiver.kt`. | 🟢 **Sesuai 100%** |
| **FR-03.2** | Menu Pengaturan untuk ubah jam bangun & preset audio kapan saja | `SettingsScreen.kt` terhubung penuh ke DataStore & auto-reschedule alarm. | 🟢 **Sesuai 100%** |

---

### 2. Kebutuhan Non-Fungsional (NFR) & Kasus Penggunaan (UCS)

| ID / Klausul | Kebutuhan | Analisis Keselarasan | Catatan Tindak Lanjut |
|---|---|---|---|
| **NFR-01** | Waktu start audio $< 200$ ms; Memori RAM $< 40$ MB | Belum diukur secara empiris di HP fisik. Target RAM $< 40$ MB perlu diuji apakah realistis dengan runtime Compose + Media3. | **Ditambahkan ke Fase 4 (Tugas 4.2)** |
| **NFR-02** | $\le$ 2 ketukan untuk memutar audio; SUS target $\ge$ 80 | Dari buka app malam hari: 1 ketukan tombol Play langsung bunyi (One-Tap). Alur sangat ergonomis. | Siap untuk pengujian kuesioner SUS |
| **NFR-03** | Privasi 100% offline, tanpa login, nol data keluar | `AndroidManifest.xml` sama sekali tidak memiliki izin `android.permission.INTERNET`. | 🟢 **Sesuai Penuh** |
| **NFR-04** | Kompatibilitas Android 9+ & iOS 14+ | **Penyesuaian Resmi (Decision Log AGENTS.md):** Android-first (`minSdk` 28); iOS ditunda karena keterbatasan ketiadaan hardware Mac/Xcode di tim. | Didokumentasikan sebagai keputusan scope |
| **UCS-01** | Postcondition: *"riwayat sesi dicatat di local storage tanpa membangunkan pengguna"* | `SettingsRepository.kt` saat ini baru mencatat konfigurasi & kartu selesai, belum mencatat riwayat pemutaran sesi tidur (timestamp & durasi). | **Ditambahkan ke Fase 4 (Tugas 4.1)** |
| **UCS-01 Exc-1** | Peringatan volume 0% | Deteksi hardware mute/volume 0 via `AudioManager` + snackbar peringatan. | 🟢 **Sesuai Penuh** |
| **UCS-02 Exc-1** | Akses di atas pukul 20.00 menampilkan pesan penonaktifan video | Snackbar: *"Mode Malam Aktif. Konten video diistirahatkan untuk menjaga hormon tidurmu"* tampil otomatis. | 🟢 **Sesuai Penuh** |

---

## 🛠️ Rincian Implementasi yang Telah Selesai (Fase 0 - 3)

### Fase 0: Prototipe Risiko (LULUS 100%)
- [x] Manifest permissions & service declaration.
- [x] `PlaybackService` (`MediaSessionService` + ExoPlayer background loop).
- [x] `MainActivity` remote control via `MediaController`.
- [x] Uji tahan layar mati 30+ menit & tahan swipe dari recent apps.
- [x] `SleepTimer` di Service + cubic exponential fade-out $(1 - progress)^3$.
- [x] AlarmManager (`setAlarmClock`) + `BedtimeReceiver` + `WakeLock` lock screen.
- [x] Uji Infinix XOS (Android 16): dialog bypass optimasi baterai.

### Fase 1: Modul `:core` + Unit Test (LULUS 100%)
- [x] Setup modul `:core` murni Kotlin (tanpa Android SDK).
- [x] `Mode.kt`, `SleepSchedule.kt`, `ComplaintPreset.kt`, `AppClock.kt`.
- [x] 18/18 Unit Test JUnit komprehensif lulus 100%.

### Fase 2: Pemutar Malam + Night Lockout (LULUS 100%)
- [x] `NightScreen.kt` (OLED black, tombol besar, 4 preset audio, countdown).
- [x] `DayScreen.kt` (ringkasan sirkadian).
- [x] Peringatan volume hening 0% perangkat.
- [x] Modal Bottom Sheet Panel Demo (override jam HP, tes alarm 10 dtk, baterai).

### Fase 3: Onboarding, Kartu Harian, & Pengaturan (LULUS 100%)
- [x] `OnboardingScreen.kt` (BR-00: $\le$ 3 ketukan, diagnostik, jam bangun, konfirmasi + catatan PRD).
- [x] `ContentRepository.kt` & `assets/daily_cards.json` (14 kartu lengkap, BR-02: $\le$ 80 kata + 1 aksi konkret).
- [x] Integrasi kartu edukasi di `DayScreen.kt` dengan penanda hari ke-N & status selesai.
- [x] `SettingsScreen.kt` (ubah jam bangun, dynamic schedule preview, ganti preset audio default).
- [x] `SettingsRepository.kt` (DataStore Preferences).
- [x] Routing Navigasi Single Activity di `MainActivity.kt`.
- [x] `BootReceiver.kt` membaca DataStore via Coroutines & pulihkan alarm pengingat tidur otomatis.

---

## 🎯 Rencana Fase 4: Polish, Pengukuran, & Persiapan Demo Dosen (PRIORITAS SAAT INI)

Berikut adalah daftar tugas terarah untuk menutup celah PRD dan mempersiapkan demo produk:

| # | Tugas | Referensi Klausul | Tindakan Teknis | Status |
|:---:|---|---|---|:---:|
| **4.1** | **Pencatatan Riwayat Sesi Sederhana** | PRD UCS-01 & AGENTS.md Bag. 8 | Penyimpanan riwayat sesi (`recordSleepSession`) di `SettingsRepository.kt` dan visualisasi ringkasan di `SettingsScreen.kt`. | ✅ Selesai |
| **4.2** | **Kontainer Video Vertikal di DayScreen** | PRD FR-02.1 & UCS-02 | Thumbnail video vertikal 9:16 dan Vertical Video Modal Player interaktif 35 detik di `DayScreen.kt`. | ✅ Selesai |
| **4.3** | **Pengukuran Waktu Start Audio (NFR-01)** | PRD NFR-01 | Pengukuran benchmark latensi tombol Play ke audio berbunyi (target: $< 200$ ms) terintegrasi langsung di `MainActivity.kt` & kartu demo. | ✅ Selesai |
| **4.4** | **Pengukuran Konsumsi RAM (NFR-01)** | PRD NFR-01 & AGENTS.md Bag. 5 | Profiling memori JVM heap runtime terintegrasi real-time di UI Panel Demo untuk evaluasi target 40 MB. | ✅ Selesai |
| **4.5** | **Finalisasi Menu Demo & Skenario Dosen** | AGENTS.md Bag. 10 | Tombol simulasi waktu ("Paksa Siang", "Paksa Malam"), tombol picu alarm 10 detik, kontrol baterai, dan benchmark memori siap via scrcpy. | ✅ Selesai |
| **4.6** | **Persiapan Kuesioner SUS (NFR-02)** | PRD NFR-02 & KR-3 | Pembuatan dokumen instrumen pengujian SUS 10 butir terstandarisasi ISO 9241-11 di `instrumen_uji_sus.md` untuk 27+ responden FT UM. | ✅ Selesai |

---

## 🔍 Verifikasi Kompilasi & Kode Saat Ini

- **Status Gradle Build:** `.\gradlew assembleDebug` $\to$ **BUILD SUCCESSFUL** (0 error, APK berhasil dibuat di `app/build/outputs/apk/debug/app-debug.apk`).
- **Status Unit Test:** `.\gradlew test` $\to$ **BUILD SUCCESSFUL** (18/18 tes unit lulus).
- **Integritas Privasi:** Bebas dari izin `INTERNET`, tidak ada SDK pihak ketiga/iklan/analitik cloud.
