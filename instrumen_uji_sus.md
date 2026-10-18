# 📋 Panduan & Instrumen Uji System Usability Scale (SUS)

**Proyek:** JalJayo – Aplikasi Intervensi Kebiasaan Tidur Mahasiswa Gen-Z  
**Mata Kuliah:** Manajemen Inovasi (Offering D6, Kelompok 2) — Fakultas Teknik Universitas Negeri Malang  
**Standar Metodologis:** ISO 9241-11 & John Brooke (1996) System Usability Scale  
**Target Metrik:** Skor SUS $\ge$ 80 (*Grade A / Excellent*, sesuai **NFR-02** & **KR-3**)

---

## 🎯 1. Tujuan Pengujian

1. Mengukur tingkat kebergunaan (*usability*), kemudahan pemakaian, dan kepuasan pengguna mahasiswa baru Gen-Z Fakultas Teknik Universitas Negeri Malang terhadap aplikasi **JalJayo**.
2. Memvalidasi hipotesis **Zero-Friction**: membuktikan bahwa ketiadaan proses registrasi/login dan alur $\le$ 2 ketukan mampu menghasilkan tingkat kenyamanan penggunaan yang tinggi.
3. Menjadi bukti empiris kuantitatif untuk laporan akhir dan presentasi mata kuliah Manajemen Inovasi.

---

## 👥 2. Karakteristik & Jumlah Responden

* **Jumlah Target Responden:** Minimal 27–30 mahasiswa baru FT UM (selaras dengan basis responden kuesioner awal).
* **Kriteria Responden:**
  * Mahasiswa aktif FT UM angkatan 2024 / Gen-Z.
  * Menggunakan ponsel pintar Android (Android versi 9.0 ke atas).
  * Memiliki kecenderungan tidur larut malam (< 6 jam) dan bermain gawai di atas kasur (*bedtime procrastination / racing thoughts*).

---

## 🧪 3. Skenario Pengujian Berbasis Tugas (Task-Based Usability Test)

Sebelum mengisi kuesioner, responden diminta untuk menginstal file APK `app-debug.apk` dan menjalankan **4 tugas singkat** berikut secara mandiri tanpa bantuan penguji:

| No | Nama Tugas | Instruksi untuk Responden | Kriteria Keberhasilan |
|:---:|---|---|---|
| **Tugas 1** | **Smart Micro-Onboarding** | Buka aplikasi untuk pertama kali, pilih keluhan tidurmu, atur jam bangun pagi, dan masuk ke halaman beranda. | Berhasil masuk ke beranda dalam waktu $< 60$ detik ($\le$ 3 ketukan). |
| **Tugas 2** | **Edukasi Siang & Video Vertikal** | Baca kartu edukasi sains tidur hari ini, putar video mikro vertikal, dan tandai kartu sebagai selesai dibaca. | Berhasil menonton video mikro 35 detik dan status kartu berubah menjadi "✅ Selesai". |
| **Tugas 3** | **One-Tap Wind-Down Player** | Buka pemutar audio malam, pilih preset suara yang kamu sukai (misal: Brown Noise / Hujan), pilih timer 15/30 menit, dan tekan tombol Play. Coba kunci layar HP-mu. | Audio berbunyi instan dan tetap stabil bersuara saat layar ponsel dimatikan. |
| **Tugas 4** | **Pengaturan Mandiri** | Buka menu Pengaturan (⚙️ Atur), ubah jam bangun pagimu, simpan, dan perhatikan pembaruan jadwal jam tidur sirkadianmu. | Jadwal jam tidur dan pengingat terhitung otomatis sesuai rumus 5 siklus tidur. |

---

## 📝 4. Instrumen Kuesioner SUS (10 Butir Pertanyaan Resmi)

Responden memberikan penilaian terhadap 10 pernyataan berikut menggunakan **Skala Likert 1 hingga 5**:
* **1** = Sangat Tidak Setuju (STS)
* **2** = Tidak Setuju (TS)
* **3** = Ragu-Ragu / Netral (N)
* **4** = Setuju (S)
* **5** = Sangat Setuju (SS)

| No | Butir Pernyataan SUS (Bahasa Indonesia) | STS (1) | TS (2) | N (3) | S (4) | SS (5) |
|:---:|---|:---:|:---:|:---:|:---:|:---:|
| **P1** | Saya rasa saya akan sering menggunakan aplikasi JalJayo ini untuk membantu rutinitas tidur saya. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P2** | Saya merasa aplikasi JalJayo ini rumit, padahal seharusnya bisa lebih sederhana. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P3** | Saya merasa aplikasi ini sangat mudah digunakan tanpa perlu berpikir keras. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P4** | Saya rasa saya membutuhkan bantuan orang lain atau panduan teknis khusus untuk dapat menggunakan aplikasi ini. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P5** | Saya merasa fitur-fitur pada aplikasi ini (edukasi siang, pengingat, dan pemutar audio malam) terintegrasi dengan sangat baik. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P6** | Saya merasa ada terlalu banyak hal yang tidak konsisten pada aplikasi ini. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P7** | Saya rasa kebanyakan teman-teman mahasiswa akan dapat mempelajari aplikasi ini dengan sangat cepat. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P8** | Saya merasa aplikasi ini membingungkan saat pertama kali dijalankan. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P9** | Saya merasa percaya diri dan nyaman saat menggunakan aplikasi ini karena tidak meminta login atau data pribadi. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |
| **P10**| Saya perlu mempelajari banyak hal baru terlebih dahulu sebelum saya bisa terbiasa menggunakan aplikasi ini. | ⭕ | ⭕ | ⭕ | ⭕ | ⭕ |

---

## 🧮 5. Rumus & Metode Perhitungan Skor SUS

Untuk setiap lembar jawaban responden:
1. **Untuk Pertanyaan Bernomor GANJIL (P1, P3, P5, P7, P9):**  
   $$Skor\_Ganjil = \text{Skor Jawaban} - 1$$
2. **Untuk Pertanyaan Bernomor GENAP (P2, P4, P6, P8, P10):**  
   $$Skor\_Genap = 5 - \text{Skor Jawaban}$$
3. **Total Skor Responden:**  
   $$Skor\_SUS = \left(\sum Skor\_Ganjil + \sum Skor\_Genap\right) \times 2,5$$
4. **Rata-Rata Seluruh Responden:**  
   $$\overline{SUS} = \frac{\sum_{i=1}^{N} Skor\_SUS_i}{N}$$

---

## 📊 6. Interpretasi Skor SUS (Bangor et al., 2008 & Sauro, 2011)

```
 Skor SUS        Grade        Adjective Rating         Penerimaan (Acceptability)
 ────────────────────────────────────────────────────────────────────────────────
   ≥ 85           A+             Best Imaginable              Acceptable
   80 – 84        A              Excellent                    Acceptable  <-- [TARGET JALJAYO: ≥ 80]
   70 – 79        B              Good                         Acceptable
   68             C              OK (Average Industry)        Marginal
   50 – 67        D              Poor                         Marginal
   < 50           F              Awful                        Not Acceptable
```

> **Target Keberhasilan (NFR-02):**  
> JalJayo dinyatakan lulus uji kebergunaan jika skor rata-rata $\overline{SUS} \ge 80$ (*Kategori Excellent / Grade A*).
