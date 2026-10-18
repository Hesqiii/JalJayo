# JalJayo – Panduan Desain (Arah A: "Senja Tenang")

> Untuk AI asisten di editor dan anggota tim. Baca bersama `AGENTS.md`.
> Jika bertentangan, **`AGENTS.md` (aturan privasi, scope, arsitektur) menang atas dokumen ini.**
> Status: disepakati arah A. Motif saat ini hanya **bulan dan bintang** (malam). Motif siang ditandai *usulan* dan perlu disetujui tim.

---

## 0. Cara memakai dokumen ini (untuk AI)

- Aplikasi memakai **Jetpack Compose + Material 3**. Semua warna, huruf, dan bentuk harus lewat **token tema** di Bagian 3-5. **Dilarang menulis hex warna atau ukuran huruf langsung di layar.**
- Kerjakan **berurutan**: (1) tema (`Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt`), (2) komponen, (3) layar satu per satu dengan **data palsu**, lalu (4) sambungkan ke logika yang sudah ada. Jangan mengubah logika audio, notifikasi, atau `core`.
- Saya (pengguna) belum paham Compose. Jelaskan singkat kenapa struktur kode dipilih, dan beri analogi ke React bila perlu.
- Setelah selesai satu layar, saya akan mengirim **screenshot**. Perbaiki berdasarkan screenshot, bukan asumsi.
- **Jangan menambah library** (animasi, ikon, font downloader, image loader) tanpa menyebut alasannya dan meminta persetujuan.
- Tidak ada izin `INTERNET` (lihat `AGENTS.md` Bagian 6): font, ikon, dan ilustrasi **harus ada di dalam app**.

---

## 1. Konsep dan prinsip

**Cerita visual: siang → senja → malam.** Produk ini memindahkan edukasi ke siang dan menyisakan malam untuk audio. Tampilan harus terasa seperti hari yang perlahan meredup.

1. **Dua mode, dua kepribadian.** Siang hangat dan informatif. Malam gelap, sunyi, dan minimal.
2. **Malam dirancang untuk orang mengantuk di kamar gelap.** Tombol besar, tidak ada yang berkedip, tidak ada yang menyilaukan.
3. **Satu aksi utama per layar.** Di malam: satu tombol Play.
4. **Ramah, tidak ramai.** Nada santai Gen-Z, tanpa keriuhan visual.
5. **BR-01 mutlak:** tidak ada iklan, banner, promosi, atau efek visual menyala saat audio aktif.

---

## 2. Aturan tema (penting, mudah salah)

- Mode **`DAY` / `NIGHT` ditentukan oleh `getMode()` dari modul `core`** (06.00-19.59 = siang, 20.00-05.59 = malam), **bukan** oleh pengaturan dark mode sistem.
- **Matikan dynamic color / Material You.** Palet sudah ditetapkan.
- Buat satu wrapper: `JalJayoTheme(mode: Mode) { ... }`. Semua layar di dalamnya membaca warna lewat `MaterialTheme.colorScheme` atau objek token khusus (`JalJayoColors`).
- **Status bar dan navigation bar** ikut mode (ikon gelap di siang, ikon terang di malam; latar menyatu dengan latar layar, edge-to-edge).
- Saat app terbuka dan jam melewati 20.00, mode berpindah otomatis dengan **crossfade pelan (~800 ms)**, tanpa kilatan putih di tengah transisi.

---

## 3. Warna

Rasio kontras di bawah **sudah dihitung** (rumus WCAG). Target: teks biasa ≥ 4,5:1, teks besar dan komponen UI ≥ 3:1.

### Token

| Token | Siang | Malam |
|---|---|---|
| `background` | `#FFF8F0` (krem) | `#05060F` (nyaris hitam) |
| `surface` | `#FFFFFF` | `#0E1020` |
| `onBackground` / `onSurface` (teks utama) | `#1F2142` | `#C9C3B8` (putih hangat redup) |
| `textSecondary` | `#5A5C80` | `#8A8798` |
| `primary` | `#4B4FA3` (indigo) | `#7479C0` (indigo diredam) |
| `onPrimary` | `#FFFFFF` | `#05060F` |
| `accent` | `#FFB68A` (persik) | `#D9A66B` (amber redup) |
| `onAccent` | `#1F2142` | `#05060F` |
| `outline` / pemisah | `#E6DCCF` | `#1B1E33` |
| `error` | `#B3261E` | `#E58B84` *(periksa kontras sebelum dipakai)* |

### Kontras terukur

| Pasangan | Rasio |
|---|---|
| Siang: teks utama / background | 14,7 |
| Siang: textSecondary / background | 6,1 |
| Siang: putih di atas primary | 7,1 |
| Siang: onAccent di atas accent | 9,1 |
| Malam: teks utama / background | 11,5 |
| Malam: textSecondary / background | 5,8 |
| Malam: primary / background | 5,0 |
| Malam: primary / surface | 4,7 |
| Malam: onPrimary di atas primary | 5,0 |
| Malam: onAccent di atas accent | 9,3 |

### Aturan pemakaian warna

- **Siang `accent` (`#FFB68A`) kontrasnya hanya 1,6:1 terhadap background.** Pakai untuk **latar** (chip, sorotan aksi harian, ilustrasi), **jangan untuk teks atau ikon tipis**. Untuk teks beraksen di siang, pakai `primary`.
- **Malam: jangan pernah memakai putih murni (`#FFFFFF`), biru terang, atau warna jenuh.** Teks terang tertinggi adalah `#C9C3B8`.
- Malam boleh memakai `#000000` sebagai `background` untuk layar OLED. Jika dipakai, kontras lain tetap terpenuhi.
- Jangan menyampaikan informasi hanya lewat warna; tambahkan teks atau ikon.
- Jangan membuat gradien terang di malam. Gradien senja hanya di siang (krem → persik sangat tipis di bagian atas, opsional).

Contoh pemetaan (acuan untuk `Color.kt`):

```kotlin
// Siang
val DayBackground = Color(0xFFFFF8F0)
val DaySurface = Color(0xFFFFFFFF)
val DayOnSurface = Color(0xFF1F2142)
val DayPrimary = Color(0xFF4B4FA3)
val DayAccent = Color(0xFFFFB68A)

// Malam
val NightBackground = Color(0xFF05060F)
val NightSurface = Color(0xFF0E1020)
val NightOnSurface = Color(0xFFC9C3B8)
val NightPrimary = Color(0xFF7479C0)
val NightAccent = Color(0xFFD9A66B)
```

---

## 4. Tipografi

- Huruf: **Plus Jakarta Sans** (cadangan: Nunito). Keduanya berlisensi terbuka di Google Fonts; **cek ulang lisensi** dan catat di tabel Kredit pada `README.md`.
- **File font harus di-bundle di `res/font/`** (tidak boleh memakai downloadable fonts karena tidak ada izin internet). Sertakan hanya bobot yang dipakai: Regular (400), Medium (500), SemiBold (600), Bold (700).
- Definisikan lewat `Typography` Material 3, bukan per layar.

| Peran | Ukuran | Bobot | Dipakai untuk |
|---|---|---|---|
| `displaySmall` | 32sp | Bold | Judul onboarding, jam hitung mundur malam |
| `headlineSmall` | 24sp | SemiBold | Judul kartu |
| `titleMedium` | 18sp | SemiBold | Judul bagian, label tombol |
| `bodyLarge` | 16sp | Regular | Isi kartu (kenyamanan baca) |
| `bodyMedium` | 14sp | Regular | Keterangan |
| `labelMedium` | 12sp | Medium | Chip, label kecil |

- Tinggi baris isi ≈ 1,5× ukuran huruf.
- Teks harus tetap terbaca pada **ukuran font sistem hingga 200%** tanpa terpotong atau bertabrakan.
- Satuan teks selalu `sp`, satuan jarak selalu `dp`.

---

## 5. Bentuk, jarak, dan area sentuh

- Grid jarak **8dp** (4dp untuk detail kecil). Margin layar 24dp.
- Sudut membulat besar:
  - Kartu: **24dp**
  - Tombol utama dan chip: **bentuk pil** (radius penuh)
  - Thumbnail video: **20dp**
- **Area sentuh minimal 48×48dp** untuk semua elemen interaktif.
- **Tombol Play malam: lingkaran ≥ 112dp**, di tengah layar. Tombol Stop saat memutar: ≥ 72dp.
- Elevasi/bayangan: ringan di siang; **tanpa bayangan di malam** (pisahkan dengan `surface` dan `outline`).

---

## 6. Motif dan ilustrasi

**Aturan umum:** semua ilustrasi dibuat dari **vektor** (`ImageVector`/Compose `Canvas` atau Vector Drawable), **bukan gambar raster**, supaya APK tetap kecil dan tajam di semua layar. Bentuknya sederhana, datar (flat), tanpa wajah dan tanpa karakter.

### Malam (disepakati): bulan dan bintang
- **Bulan sabit** di bagian atas layar, warna `NightAccent` pada opasitas rendah (sekitar 70-85%), tanpa glow terang.
- **Bintang**: 8-14 titik kecil dan beberapa bintang 4 sudut, tersebar di area atas, warna `NightOnSurface` pada opasitas 25-60%.
- Posisi bintang **tetap (deterministik, seed konstan)**. Jangan diacak ulang tiap recomposition.
- **Gerak: tidak ada kedipan, tidak ada animasi berulang.** Boleh satu efek sangat pelan (misalnya bintang muncul fade-in sekali saat layar dibuka). Saat audio memutar, motif **diam**.
- Motif tidak boleh menutupi tombol atau teks, dan tidak boleh lebih terang dari tombol Play.

### Siang (USULAN, perlu disetujui tim): matahari dan awan
- **Matahari** bulat sederhana (`DayAccent`) di pojok atas dan **1-2 awan** datar lembut (`surface` atau krem sedikit lebih gelap dari background).
- Tujuannya menjadi pasangan visual bulan dan bintang, sehingga pergantian mode terasa sebagai pergantian waktu.
- Ukuran kecil; ilustrasi hanya mengisi bagian atas beranda siang dan layar onboarding, **tidak** di dalam kartu edukasi.
- Jika tim tidak setuju, beranda siang cukup bersih tanpa ilustrasi (latar krem + kartu).

### Dilarang tanpa persetujuan tim
- Karakter/maskot, wajah pada bulan atau matahari, foto, dan ilustrasi kompleks.

---

## 7. Gerak

| Mode | Aturan |
|---|---|
| Siang | Transisi lembut 200-300 ms (fade/slide pendek). Tidak ada animasi berulang. |
| Malam | Hampir tanpa animasi. Hanya fade pelan (≥ 400 ms). |
| Pergantian siang ↔ malam | Crossfade ~800 ms. |

- Tidak ada elemen yang berkedip atau berpulsa di malam, termasuk tombol Play dan hitung mundur.
- Hormati pengaturan sistem untuk mengurangi atau menghapus animasi. Periksa perilakunya di HP asli.
- Saat audio aktif: tidak ada efek visual menyala (BR-01).

---

## 8. Spesifikasi layar

Teks berwarna biru di bawah adalah **teks tampilan (copy) dari PRD** dan harus dipakai persis.

### 8.1 Onboarding (maks. 3 ketukan, < 60 detik)
- Mode **siang** (hangat), dengan motif matahari/awan bila disetujui.
- Langkah 1: pertanyaan **"Apa hal yang paling mengganggu tidurmu?"** dengan **kartu pilihan besar** (satu ketuk):
  - "Otak muter terus / overthinking"
  - "Kamar bising / tidak tenang"
- Langkah 2: **"Jam berapa kamu harus bangun pagi?"** dengan pemilih jam (pilihan cepat + pemilih jam penuh). Tampilkan hasil jam tidur dan pengingat sebagai konfirmasi ringan.
- Catatan kecil di layar: **"Pengaturan ini dapat Anda ubah kapan saja di menu Pengaturan"**.
- Tombol utama pil lebar: **"Mulai JalJayo"**. Tombol teks sekunder: **"Lewati"**.
- Indikator langkah berupa titik (bukan angka).

### 8.2 Beranda siang
- Sapaan singkat dan indikator **"Hari X dari 14"** (penanda kemajuan biasa, **bukan** gamifikasi).
- **Kartu hari ini:** thumbnail video vertikal (9:16), judul, teks ≤ 80 kata, dan **blok "Hari ini coba:"** yang menyorot satu aksi (latar `DayAccent`, teks `onAccent`).
- Tombol **"Selesai Dibaca"**. Setelah ditekan, tampilkan keadaan selesai yang tenang, bukan perayaan meriah.
- Navigasi ke Pengaturan lewat ikon di pojok (cukup, tanpa bottom bar).

### 8.3 Beranda malam (siap tidur)
- Latar `NightBackground`, **bulan dan bintang** di atas. Tanpa teks edukasi atau video.
- Satu baris: **"Mode Malam Aktif"** (kecil, `textSecondary`).
- Pilihan suara dalam chip horizontal (Theta Waves 6Hz, dan pilihan lain sesuai yang tersedia di app). Preset default terpilih sesuai onboarding.
- Pilihan timer 15 / 30 / 45 menit (default 30).
- **Tombol Play raksasa di tengah** (≥ 112dp, `NightAccent` dengan ikon `onAccent`).

### 8.4 Malam, sedang memutar
- Hampir semua padam: latar gelap, motif bulan dan bintang diam dan redup.
- Hitung mundur `displaySmall` berwarna `textSecondary` (redup). **Tidak berkedip.**
- Tombol **Stop** besar tapi tidak mencolok (garis tepi, bukan blok terang).
- Opsional (uji di HP asli sebelum dipakai): meredupkan kecerahan jendela app.

### 8.5 Pengaturan
- Daftar sederhana: jam bangun, pengingat, preset audio default, panduan optimasi baterai.
- Mengikuti mode (siang/malam) seperti layar lain; saat malam tetap gelap dan tenang.

### 8.6 Keadaan khusus
- **Volume 0%:** pesan singkat non-agresif: **"Volume ponsel Anda dalam keadaan hening, harap naikkan volume"** (Snackbar/teks kecil, bukan dialog besar).
- **Buka app setelah 20.00:** langsung masuk beranda malam dengan pesan **"Mode Malam Aktif. Konten video diistirahatkan untuk menjaga hormon tidurmu"**.
- **Kartu habis (setelah hari ke-14):** keadaan akhir yang ramah; isi menunggu keputusan tim.

---

## 9. Komponen yang perlu dibuat

| Komponen | Catatan |
|---|---|
| `PrimaryButton` | Pil, tinggi ≥ 56dp, `primary`/`onPrimary` |
| `ChoiceCard` | Kartu pilihan besar di onboarding (terpilih = garis tepi `primary` + ikon centang, bukan hanya warna) |
| `SoundChip`, `TimerChip` | Pil, ≥ 48dp tinggi sentuh |
| `DailyCard` | Thumbnail, judul, teks, blok aksi |
| `ActionHighlight` | Blok "Hari ini coba:" |
| `StepDots` | Indikator langkah onboarding |
| `PlayButton` | Lingkaran besar, malam |
| `NightSky` | Motif bulan dan bintang (Canvas, deterministik) |
| `DaySky` | Motif matahari dan awan (usulan) |

---

## 10. Nada tulisan (microcopy)

- Bahasa Indonesia santai, sapaan **"kamu"**, kalimat pendek.
- Hindari bahasa menggurui atau menakut-nakuti. Hindari tanda seru berlebihan.
- **Malam: teks seminimal mungkin.** Tidak ada emoji dan tidak ada kalimat panjang.
- Tombol berupa kata kerja jelas: "Mulai JalJayo", "Selesai Dibaca", "Putar".
- Teks edukasi bersifat umum, bukan nasihat medis.

---

## 11. Aksesibilitas

- Semua kontras sesuai Bagian 3. Jangan mengubah warna tanpa menghitung ulang.
- Semua ikon dan tombol berikon punya `contentDescription` bahasa Indonesia ("Putar suara", "Hentikan").
- Ukuran sentuh ≥ 48dp. Tata letak tetap rapi pada font 200% dan layar kecil.
- Kompatibel dengan TalkBack: urutan fokus logis, status (terpilih, selesai) dibacakan.
- Motif dekoratif (bulan, bintang, matahari, awan) ditandai agar **diabaikan** pembaca layar.

---

## 12. Larangan

- Iklan, banner, notifikasi promosi, atau efek menyala saat audio aktif (BR-01).
- Gamifikasi: leaderboard, koin, lencana sosial. Indikator "Hari X dari 14" diperbolehkan karena bukan kompetitif.
- Video atau teks edukasi di mode malam.
- Putih murni dan warna jenuh di mode malam.
- Animasi berulang atau berkedip di mode malam.
- Mengikuti dark mode sistem atau dynamic color.
- Menulis warna, ukuran huruf, atau jarak langsung di layar (harus lewat token).
- Menambah karakter/maskot, foto, library baru, atau izin baru tanpa persetujuan tim.

---

## 13. Pertanyaan terbuka untuk tim

1. Motif siang (matahari dan awan): setuju atau tidak?
2. Daftar final pilihan suara di app (menentukan jumlah chip di beranda malam).
3. Keadaan setelah hari ke-14: kartu diulang, atau muncul pesan selesai?
4. Meredupkan kecerahan layar saat memutar malam: dipakai atau tidak (perlu uji di HP asli).
5. Final huruf (Plus Jakarta Sans atau Nunito) setelah dicoba di layar.
