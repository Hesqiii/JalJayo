# Analisis Keselarasan Dokumen JalJayo

> Audit 6 gap antara PRD ([`requirements_jaljayo.md`](file:///D:/Project%20Android%20Studio/JalJayo/requirements_jaljayo.md)), AGENTS.md, laporan progres ([`rencana_pembangunan.md`](file:///D:/Project%20Android%20Studio/JalJayo/rencana_pembangunan.md)), dan kode implementasi.

---

## 1. Target Platform & Penyimpanan — PRD Masih Bicara Web/iOS

### Temuan

| Lokasi | Masalah |
|---|---|
| [PRD NFR-04 (baris 98)](file:///D:/Project%20Android%20Studio/JalJayo/requirements_jaljayo.md#L98) | Menyebut *"Android (versi 9+) **dan iOS (versi 14+) serta browser web seluler modern**"* |
| [PRD Bagian 8 (baris 139-147)](file:///D:/Project%20Android%20Studio/JalJayo/requirements_jaljayo.md#L139-L147) | Menyebut *"Web Notification API"*, *"localStorage / IndexedDB"*, *"Background Audio API"* generik |
| [PRD Risiko 2 (baris 153)](file:///D:/Project%20Android%20Studio/JalJayo/requirements_jaljayo.md#L153) | Masih membahas mitigasi PWA/Flutter untuk Safari iOS |
| [PRD FR-00.5 (baris 66)](file:///D:/Project%20Android%20Studio/JalJayo/requirements_jaljayo.md#L66) | Menyebut *"localStorage"* |
| Berbagai UCS | Menyebut *"localStorage"* (baris 108, 134, 136) |

Sementara itu, keputusan aktual sudah **Android-first** dengan stack DataStore + AlarmManager + Media3. Decision log di [AGENTS.md Bagian 2](file:///D:/Project%20Android%20Studio/JalJayo/AGENTS.md) sudah benar, tapi PRD belum mengikuti.

### Rekomendasi

```diff
 # NFR-04
-Aplikasi WAJIB responsif dan berjalan lancar pada perangkat Android (versi 9+) 
-dan iOS (versi 14+) serta browser web seluler modern.
+Aplikasi WAJIB berjalan pada perangkat Android versi 9+ (API level 28).  
+iOS dan web ditunda ke fase berikutnya (lihat Decision Log).

 # Bagian 8 — Batasan Teknis
-1. Background Audio Playback API
+1. Media3 (ExoPlayer + MediaSessionService) untuk audio background
-2. Web Notification API / Local Notification Service
+2. AlarmManager (setAlarmClock) + BroadcastReceiver untuk notifikasi lokal terjadwal
-3. localStorage / IndexedDB
+3. Jetpack DataStore Preferences untuk penyimpanan konfigurasi lokal

 # Risiko 2
-Pembatasan Web Push pada Safari iOS. Mitigasi: PWA / hybrid framework.
+iOS ditunda karena tidak ada akses Mac/Xcode di tim. 
+Ketika dilanjutkan, evaluasi apakah logika inti bisa dimigrasikan ke KMP.

 # FR-00.5 dan semua UCS yang menyebut "localStorage"
-disimpan secara lokal di perangkat (localStorage)
+disimpan secara lokal di perangkat (Jetpack DataStore Preferences)
```

> [!IMPORTANT]
> Ini bukan sekadar kosmetik — dosen atau reviewer yang membaca PRD dan melihat "iOS 14+, localStorage, Web Notification" akan bertanya kenapa deliverable-nya APK Android. Sinkronkan PRD dengan Decision Log supaya konsisten.

---

## 2. Fade Timer — "Eksponensial" vs. "Kubik"

### Temuan

| Dokumen / Kode | Menyebut |
|---|---|
| [PRD FR-01.3 (baris 74)](file:///D:/Project%20Android%20Studio/JalJayo/requirements_jaljayo.md#L74) | *"exponential volume fade-out"* |
| [AGENTS.md Bagian 4](file:///D:/Project%20Android%20Studio/JalJayo/AGENTS.md) | *"fade-out **eksponensial**"* |
| [SleepTimer.kt (baris 89-91)](file:///D:/Project%20Android%20Studio/JalJayo/app/src/main/java/id/jaljayo/app/audio/SleepTimer.kt#L89-L91) | `volume = (1 - progress).pow(3.0f)` — ini **power law (kubik)**, bukan eksponensial |
| [rencana_pembangunan.md (baris 14, 37, 67)](file:///D:/Project%20Android%20Studio/JalJayo/rencana_pembangunan.md#L14) | Menyebut keduanya bergantian: *"cubic fade-out"*, *"cubic exponential fade-out"* |

### Analisis teknis

Secara matematis, ini dua hal berbeda:

| Kurva | Formula | Karakter |
|---|---|---|
| **Power law (kubik)** | `v = (1 − t)³` | Turun pelan di awal, cepat di akhir. Deterministic, mudah dipahami. |
| **Eksponensial** | `v = e^(−k·t)` | Turun proporsional terhadap level saat ini. Mendekati 0 tapi tidak pernah benar-benar 0. |

Implementasi di [`SleepTimer.kt`](file:///D:/Project%20Android%20Studio/JalJayo/app/src/main/java/id/jaljayo/app/audio/SleepTimer.kt) menggunakan `(1 − progress)³`. Ini secara teknis **bukan** eksponensial — ini **polynomial (kubik)**. Tapi perilakunya mirip-mirip: turun pelan → cepat → diam.

### Rekomendasi

Kamu tidak perlu mengubah kode. Kurva kubik sudah bagus dan terasa natural. Yang perlu diubah adalah **istilah di dokumen** supaya seragam:

**Opsi A (Samakan ke istilah implementasi):**
Ganti semua sebutan "eksponensial" di PRD, AGENTS.md, dan laporan progres menjadi:
> *"fade-out non-linear (kubik): `volume = (1 − progress)³`, di mana progress naik dari 0 ke 1 selama 2 menit terakhir"*

**Opsi B (Tetap pakai "eksponensial" sebagai istilah kasual):**
Tambahkan catatan di AGENTS.md dan SleepTimer.kt bahwa "eksponensial" dipakai sebagai deskripsi perilaku umum (turun non-linear), bukan definisi matematis ketat. Ini agak kurang tepat tapi cukup untuk konteks proyek kuliah.

> [!TIP]
> Saya merekomendasikan **Opsi A**. Di presentasi dosen, lebih baik klaim yang bisa kamu jelaskan dengan jujur daripada istilah yang salah secara matematis kalau ditanya.

---

## 3. Status Video & Pengukuran — Tabel Evaluasi Tidak Sinkron

### Temuan

| Lokasi | Apa yang ditulis |
|---|---|
| [Tabel evaluasi FR-02.1 (baris 38)](file:///D:/Project%20Android%20Studio/JalJayo/rencana_pembangunan.md#L38) | 🟡 *"Sebagian (Video Belum Ada)"* — *"komponen pemutar/kontainer video vertikal belum dipasang di DayScreen.kt"* |
| [Tabel evaluasi NFR-01 (baris 50)](file:///D:/Project%20Android%20Studio/JalJayo/rencana_pembangunan.md#L50) | *"Belum diukur secara empiris di HP fisik"* |
| [Tugas Fase 4.2 (baris 100)](file:///D:/Project%20Android%20Studio/JalJayo/rencana_pembangunan.md#L100) | ✅ Selesai — *"Vertical Video Modal Player interaktif 35 detik"* |
| [Tugas Fase 4.3-4.4 (baris 101-102)](file:///D:/Project%20Android%20Studio/JalJayo/rencana_pembangunan.md#L101-L102) | ✅ Selesai |
| [DayScreen.kt (baris 288-351)](file:///D:/Project%20Android%20Studio/JalJayo/app/src/main/java/id/jaljayo/app/ui/home/DayScreen.kt#L288-L351) | `VerticalVideoThumbnailCard` dan `VerticalVideoModalPlayer` **sudah ada** |

**Jelas:** tabel evaluasi ditulis sebelum Fase 4, lalu Fase 4 menyelesaikan gap-nya, tapi tabel tidak diperbarui.

### Rekomendasi

Perbarui tabel evaluasi agar sinkron dengan kondisi aktual:

```diff
 # FR-02.1
-**Namun komponen pemutar/kontainer video vertikal belum dipasang di `DayScreen.kt`**.
-| 🟡 **Sebagian (Video Belum Ada)** |
+`VerticalVideoThumbnailCard` + `VerticalVideoModalPlayer` di `DayScreen.kt` 
+(simulasi visual 35 detik, bukan playback file .mp4 — lihat catatan konten Risiko #5).
+| 🟢 **Sesuai (Kontainer Video Ada)** |

 # NFR-01
-Belum diukur secara empiris di HP fisik.
+Benchmark terintegrasi di Panel Demo (Tugas 4.3 & 4.4). 
+Latensi audio < 200ms ✓. Target RAM 40 MB perlu evaluasi (runtime Compose + Media3).
```

> [!NOTE]
> Perlu jujur bahwa "video player" saat ini adalah **simulasi visual** (emoji + teks + timer), bukan playback file .mp4 yang sesungguhnya. Ini selaras dengan keputusan scope (Risiko #5 di AGENTS.md: belum ada yang memproduksi video), tapi sebaiknya dinyatakan eksplisit.

---

## 4. Privasi & Backup — Klaim Perlu Dipertegas

### Temuan

| Lokasi | Masalah |
|---|---|
| [AndroidManifest.xml baris 16](file:///D:/Project%20Android%20Studio/JalJayo/app/src/main/AndroidManifest.xml#L16) | `android:allowBackup="true"` |
| [AndroidManifest.xml baris 17-18](file:///D:/Project%20Android%20Studio/JalJayo/app/src/main/AndroidManifest.xml#L17-L18) | `dataExtractionRules` dan `fullBackupContent` di-set — ini berarti data DataStore **bisa** di-backup ke Google Drive (kalau user mengaktifkan backup Android) |
| PRD NFR-03 | *"Privasi 100% terjamin... Nol risiko kebocoran data pengguna"* |
| AGENTS.md Bagian 6 | *"Semua data hanya di DataStore lokal"* |

### Analisis

- **Backup Android ≠ pengiriman data oleh aplikasi.** Backup OS adalah fitur sistem yang user kendalikan sendiri. Ini berbeda dari app yang mengirim data ke server developer.
- Data JalJayo (jam bangun, preset audio, hari kartu) **bukan data sensitif** — tidak ada nama, email, atau lokasi.
- Tapi klaim "100% data hanya di perangkat" secara teknis tidak benar kalau backup OS aktif.

### Rekomendasi

Dua pilihan yang valid:

**Opsi A — Izinkan backup, perjelas klaimnya:**
```diff
 # PRD NFR-03
-Privasi 100% terjamin tanpa pengumpulan nama, email, nomor ponsel, lokasi GPS, 
-maupun data biometrik pengguna. Nol risiko kebocoran data pengguna.
+Aplikasi tidak mengumpulkan dan tidak mengirimkan data pribadi (nama, email, 
+nomor ponsel, lokasi GPS, biometrik). Tidak ada izin INTERNET di manifest.
+Backup sistem Android (Google Drive) diperbolehkan karena data yang disimpan 
+hanya konfigurasi non-sensitif (jam bangun, preset audio).
```

**Opsi B — Matikan backup sepenuhnya:**
```xml
<!-- AndroidManifest.xml -->
android:allowBackup="false"
android:dataExtractionRules="@xml/data_extraction_rules"
```
Dan buat `data_extraction_rules.xml` yang meng-exclude semua.

> [!TIP]
> Saya merekomendasikan **Opsi A**. Data JalJayo bukan sensitif, backup malah membantu UX (kalau user ganti HP, preferensinya ikut). Yang penting klaim di PRD akurat.

---

## 5. Perilaku Saat Izin Tidak Tersedia — Kriteria Penerimaan Hilang

### Temuan

Requirements dan AGENTS.md **tidak mendefinisikan** apa yang terjadi ketika:

| Skenario | Dampak |
|---|---|
| User menolak izin `POST_NOTIFICATIONS` (Android 13+) | Pengingat tidur tidak tampil. User tidak tahu kenapa. |
| User tidak memberikan izin alarm presisi (`SCHEDULE_EXACT_ALARM` dicabut di pengaturan, atau Android 14+ menolak) | `setAlarmClock` gagal atau dibulatkan. Pengingat tidak tepat waktu. |
| User mengubah jam HP atau berpindah zona waktu | Alarm yang sudah dijadwalkan bisa salah. Mode siang/malam bisa tertukar. |

### Rekomendasi

Tambahkan bagian **"Graceful Degradation"** di requirements atau AGENTS.md:

```markdown
### Perilaku Saat Izin Tidak Tersedia

| Skenario | Perilaku yang Diharapkan |
|---|---|
| POST_NOTIFICATIONS ditolak | Pengingat tidur tidak dijadwalkan. Tampilkan banner persisten 
  di DayScreen: "Aktifkan notifikasi untuk mendapat pengingat tidur". 
  Tombol mengarah ke pengaturan izin. |
| Alarm presisi tidak tersedia | Fallback ke `setWindow()` (kurang presisi tapi tetap berfungsi). 
  Tampilkan catatan di SettingsScreen: "Pengingat mungkin terlambat beberapa menit". |
| Jam HP atau zona waktu berubah | Jadwalkan ulang alarm via BroadcastReceiver 
  `ACTION_TIME_CHANGED` / `ACTION_TIMEZONE_CHANGED`. 
  Evaluasi ulang mode siang/malam segera. |
```

> [!WARNING]
> Ini bukan sekadar polishing — di HP Xiaomi/Oppo/Vivo, izin notifikasi sering di-default OFF. Kalau app tidak menangani ini, fitur Bedtime Anchor (salah satu pilar utama) bisa diam-diam tidak berfungsi tanpa user sadar.

---

## 6. Standar UI — Aksesibilitas & Kondisi Error

### Temuan

Requirements dan AGENTS.md **tidak menyebutkan** standar untuk:

| Aspek | Status |
|---|---|
| Aksesibilitas teks (ukuran diperbesar / `fontScale`) | Tidak ada kriteria. Compose `sp` unit sudah mendukung `fontScale` by default, tapi layout bisa pecah kalau tidak diuji. |
| Kontras warna | NightScreen menggunakan latar gelap + teks terang — kemungkinan sudah ok. DayScreen perlu dicek. |
| Target sentuh minimum | AGENTS.md menyebut `JalJayoSpace.touchTarget` di kode tapi tidak ada definisi angka minimalnya di requirements. Android merekomendasikan ≥ 48dp. |
| Kondisi error (konten gagal dimuat) | [DayScreen baris 146-161](file:///D:/Project%20Android%20Studio/JalJayo/app/src/main/java/id/jaljayo/app/ui/home/DayScreen.kt#L146-L161) punya fallback teks *"Konten kartu sedang dimuat..."* tapi tidak ada retry atau penjelasan. |

### Rekomendasi

Tambahkan **kriteria UI minimum** di requirements:

```markdown
### Standar UI Minimum

| Aspek | Kriteria |
|---|---|
| Target sentuh | ≥ 48dp × 48dp (Material Design guideline) |
| Kontras teks | ≥ 4.5:1 untuk body text, ≥ 3:1 untuk teks besar (WCAG AA) |
| Font scaling | Layout tidak boleh terpotong atau overlap pada fontScale 1.3× |
| Content description | Semua elemen interaktif wajib memiliki `contentDescription` 
  untuk TalkBack |
| Kondisi gagal muat | Tampilkan pesan spesifik (apa yang gagal, apa yang bisa 
  dilakukan user), bukan hanya "sedang dimuat" tanpa batas waktu |
```

> [!NOTE]
> Untuk proyek kuliah, ini tidak harus implementasi penuh WCAG. Tapi **menyatakan** kriteria-kriteria ini di requirements menunjukkan kesadaran desain yang baik, dan membuat redesign di kemudian hari punya acuan jelas.

---

## Ringkasan Prioritas

| # | Gap | Risiko jika dibiarkan | Effort |
|:---:|---|---|---|
| **1** | Platform & penyimpanan di PRD | 🔴 Inkonsistensi fatal saat presentasi dosen | Rendah (edit teks) |
| **3** | Tabel evaluasi tidak sinkron | 🟡 Mengurangi kredibilitas laporan | Rendah (edit teks) |
| **2** | Istilah fade "eksponensial" vs kubik | 🟡 Bisa ditanya dosen, jawaban salah | Rendah (edit teks) |
| **4** | Klaim privasi vs backup | 🟡 Klaim bisa dipertanyakan | Rendah (edit teks atau 1 baris XML) |
| **5** | Graceful degradation izin | 🔴 Fitur utama diam-diam mati di HP Xiaomi/Oppo | Sedang (perlu kode + doc) |
| **6** | Standar UI / aksesibilitas | 🟡 Tidak kritis untuk demo, tapi baik untuk evaluasi | Rendah (definisikan kriteria) |

> [!IMPORTANT]
> **Poin 1 dan 5 paling kritis.** Poin 1 bisa merusak kesan presentasi. Poin 5 bisa merusak pengalaman uji SUS kalau responden pakai HP vendor lokal.
