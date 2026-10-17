**PRODUCT REQUIREMENT DOCUMENT (PRD)**

*Standar Rekayasa Perangkat Lunak: ISO/IEC/IEEE 29148:2018 & Alistair Cockburn UCS 2.0*

| Nama Produk / Sistem | JalJayo (Good Sleep, Habits) |
| :---- | :---- |
| **Target Pengguna** | Mahasiswa Baru Gen-Z (Fakultas Teknik, Universitas Negeri Malang) |
| **Versi Dokumen** | v2.0 (Comprehensive Engineering & Functional Specification) |
| **Status Persetujuan** | Approved / Ready for Development |
| **Tim Pengembang** | Kelompok 2 (Offering D6 \- Manajemen Inovasi Universitas Negeri Malang) |
| **Standar Metodologis** | ISO/IEC/IEEE 29148 (Requirements), Cockburn UCS 2.0, Marty Cagan (SVPG) |

**1\. PENDAHULUAN & LATAR BELAKANG PRODUK (INTRODUCTION)**

**1.1 Latar Belakang Masalah:** JalJayo adalah platform intervensi dan edukasi kebiasaan tidur berbasis zero-friction yang dirancang untuk mengatasi krisis penundaan tidur (bedtime procrastination) dan pikiran berputar (racing thoughts) di kalangan mahasiswa Gen-Z. Berdasarkan riset kuesioner terhadap 27 responden mahasiswa FT UM, sebanyak 81,5% mengalami durasi tidur defisit (\<6 jam) dan 77,8% terjebak doomscrolling di kasur hingga larut malam akibat overthinking tugas dan masa depan. JalJayo membedakan dirinya dari aplikasi konvensional dengan mengeliminasi friksi registrasi (Zero-Login), memindahkan edukasi kebiasaan ke siang hari, serta mendedikasikan malam hari untuk relaksasi audio tanpa interaksi layar.

**1.2 Pernyataan Misi Produk:** Mengubah kebiasaan tidur mahasiswa menjadi rutinitas yang diperhatikan dan diprioritaskan melalui penggantian kebiasaan (habit replacement) yang mudah diakses, bebas friksi teknis, dan berlandaskan ilmu ritme sirkadian.

**1.3 Tujuan Dokumen:** Dokumen ini mendefinisikan seluruh kebutuhan fungsional (FR), kebutuhan non-fungsional (NFR), spesifikasi kasus penggunaan (UCS), dan batasan teknis sistem yang mengikat tim pengembang (developer), desainer antarmuka (UI/UX), dan penguji mutu (QA).

**2\. TUJUAN PRODUK & METRIK KEBERHASILAN (OBJECTIVES & OKRs)**

Keberhasilan produk diukur secara kuantitatif menggunakan metrik Objectives and Key Results (OKRs):

·       **KR 1 (Penurunan Doomscrolling):** Minimal 70% pengguna aktif melaporkan pengurangan durasi bermain ponsel di atas kasur menjadi di bawah 15 menit dalam 14 hari pertama penggunaan.

·       **KR 2 (Percepatan Latensi Tidur):** Memangkas rata-rata waktu yang dibutuhkan untuk mulai terlelap (sleep onset latency) sebesar minimal 20 menit (merujuk Al-Kandari et al., 2023).

·       **KR 3 (Adopsi Bebas Friksi):** Tingkat retensi hari ke-7 (Day-7 Retention) mencapai minimal 60% berkat ketiadaan rintangan login (zero onboarding friction).

**3\. RUANG LINGKUP PRODUK (IN-SCOPE VS OUT-OF-SCOPE)**

| In-Scope (Fitur Wajib MVP) | Out-of-Scope (Dieliminasi untuk Menghindari Bloat) |
| :---- | :---- |
| 1\. Smart Diagnostic Micro-Onboarding (Setup kilat 3-ketukan berbasis keluhan nyata). | 1\. Sistem registrasi, verifikasi email, login kata sandi, atau OAuth multi-faktor. |
| 2\. One-Tap Zero-Visual Wind-Down Player (Theta Waves 6Hz, Brown Noise, auto-stop timer). | 2\. Gamifikasi kompleks (leaderboard kompetitif, koin virtual, badge sosial). |
| 3\. Daily Daylight Sleep Micro-Card (konten teks kilat & video 30-45 detik di siang hari). | 3\. Integrasi sensor perangkat keras (smartwatch, pelacak gerakan kasur, atau mikrofon). |
| 4\. Local Bedtime Anchor & Night Lockout (pengingat lokal 45 menit sebelum tidur). | 4\. Sinkronisasi multi-device berbasis cloud database tersentralisasi. |

**4\. TARGET PENGGUNA & USER PERSONA**

**Nama Samaran & Profil:** Dimas Pratama (18 Tahun), Mahasiswa Baru Angkatan 2024 Fakultas Teknik Universitas Negeri Malang.

·       **Pains (Hambatan):** Mengalami self-control depletion di malam hari akibat kelelahan kuliah/praktikum, terjebak siklus scrolling TikTok/IG di kasur gelap hingga jam 2 pagi.

·       **Pains (Hambatan):** Pikiran terus berputar (racing thoughts) memikirkan tugas kuliah, laporan praktikum, dan masa depan saat mencoba memejamkan mata.

·       **Pains (Hambatan):** Bangun pagi dalam kondisi lemas dan tidak berenergi (baterai tubuh sisa 5%), menurunkan konsentrasi perkuliahan pagi.

·       **Needs (Kebutuhan):** Membutuhkan intervensi langsung satu ketukan tanpa tuntutan membaca modul di malam hari, serta panduan kebiasaan yang mudah dipahami.

**5\. SPESIFIKASI KEBUTUHAN FUNGSIONAL (FUNCTIONAL REQUIREMENTS / FR)**

Disusun menggunakan sintaksis formal baku ISO/IEC/IEEE 29148: \[Kondisi Pemicu\] \+ \[Sistem/Aktor\] \+ WAJIB (SHALL) \+ \[Tindakan Terukur\] \+ \[Kriteria Hasil\]:

**Modul 0: Smart Diagnostic Micro-Onboarding (Pilar Personalisasi Cerdas \- P0)**

·       **FR-00.1 (Diagnostic Trigger):** Saat pertama kali aplikasi dibuka, sistem WAJIB menampilkan dialog asesmen singkat maksimal 3 pertanyaan visual berbasis keluhan nyata pengguna dalam waktu \< 500 ms.

·       **FR-00.2 (Smart Defaults Mapping):** Jika pengguna memilih keluhan 'Otak muter terus/overthinking', sistem WAJIB menetapkan preset audio 'Theta Waves 6Hz' sebagai default pemutar malam; jika memilih 'Kamar bising/tidak tenang', sistem WAJIB menetapkan 'Brown Noise' sebagai default.

·       **FR-00.3 (Bedtime Calculation):** Berdasarkan target jam bangun yang dipilih pengguna, sistem WAJIB menghitung mundur jadwal tidur sirkadian ideal dan menyetel jadwal Local Bedtime Anchor 45 menit sebelum jam tidur.

·       **FR-00.4 (Transparency & Reconfigurability):** Pada layar onboarding, sistem WAJIB menampilkan catatan transparansi: 'Pengaturan ini dapat Anda ubah kapan saja di menu Pengaturan'.

·       **FR-00.5 (Local State Storage):** Seluruh hasil konfigurasi WAJIB disimpan secara lokal di perangkat (localStorage) tanpa transmisi ke server cloud.

**Modul 1: One-Tap Zero-Visual Wind-Down Player (Pilar Malam \- P0)**

·       **FR-01.1 (Audio Playback):** Saat pengguna menekan salah satu tombol frekuensi (Theta Waves 6Hz, Brown Noise, atau Suara Hujan), sistem WAJIB memulai audio loop lokal dalam waktu \< 200 ms.

·       **FR-01.2 (Screen-Off Playback):** Saat audio sedang aktif, sistem WAJIB mendukung Background Audio API sehingga suara tetap mengalun stabil saat layar ponsel dikunci (lock screen) atau aplikasi diminimalkan.

·       **FR-01.3 (Auto-Stop Fade-Out):** Saat durasi timer yang dipilih (15, 30, atau 45 menit) menyisakan 2 menit terakhir, sistem WAJIB mengeksekusi exponential volume fade-out dari 100% ke 0% lalu menghentikan pemutaran secara otomatis.

**Modul 2: Daily Daylight Sleep Micro-Card & Video (Pilar Siang \- P0)**

·       **FR-02.1 (Daytime Access Window):** Antara pukul 06.00 hingga 20.00 waktu lokal, sistem WAJIB menampilkan 1 kartu edukasi sains tidur harian yang memuat teks ringkas (\<80 kata / \<45 detik baca) dan 1 video edukasi vertikal pendek berdurasi 30-45 detik.

·       **FR-02.2 (Actionable Content):** Kartu dan video edukasi WAJIB menyajikan 1 aksi kebiasaan sirkadian konkret (seperti batas minum kafein 6 jam sebelum tidur atau teknik visualisasi pikiran).

·       **FR-02.3 (Night Lockout Rule):** Tepat pukul 20.00 waktu lokal, sistem WAJIB mengunci akses video dan teks (Night Lockout Rule) serta mengalihkan antarmuka secara otomatis ke mode malam yang hanya menyajikan pemutar audio relaksasi tanpa stimulasi visual.

**Modul 3: Local Bedtime Anchor & Reconfigurable Settings (Pilar Transisi \- P1)**

·       **FR-03.1 (Local Notification Cue):** Pada 45 menit sebelum target jam tidur pengguna, sistem WAJIB memicu notifikasi lokal ramah (Local Notification API) yang mengajak pengguna mengaktifkan mode istirahat.

·       **FR-03.2 (Reconfiguration Freedom):** Pengguna WAJIB dapat mengakses menu 'Pengaturan' kapan saja untuk mengubah target jam bangun, jadwal pengingat, dan preset audio default secara instan.

**6\. SPESIFIKASI KEBUTUHAN NON-FUNGSIONAL (NON-FUNCTIONAL REQUIREMENTS / NFR)**

·       **NFR-01 (Kinerja & Kecepatan):** Waktu muat awal aplikasi (First Contentful Paint) WAJIB berada di bawah 1,5 detik pada jaringan seluler standar. Penggunaan memori saat pemutaran audio \< 40 MB RAM.

·       **NFR-02 (Usability & Ergonomi):** Tingkat kemudahan penggunaan diukur dengan System Usability Scale (SUS) target minimal skor 80\. Seluruh fungsi utama dapat diakses dalam maksimal 2 ketukan (\<= 2 clicks to play).

·       **NFR-03 (Privasi & Keamanan Mutlak):** Privasi 100% terjamin tanpa pengumpulan nama, email, nomor ponsel, lokasi GPS, maupun data biometrik pengguna. Nol risiko kebocoran data pengguna.

·       **NFR-04 (Kompatibilitas Sistem):** Aplikasi WAJIB responsif dan berjalan lancar pada perangkat Android (versi 9+) dan iOS (versi 14+) serta browser web seluler modern.

**7\. SPESIFIKASI KASUS PENGGUNAAN (USE CASE SPECIFICATION / ALISTAIR COCKBURN UCS 2.0)**

**UCS-00: Konfigurasi Awal Cerdas (Smart Micro-Onboarding)**

| Use Case ID & Nama | UCS-00: Melakukan Konfigurasi Diagnostik Awal |
| :---- | :---- |
| **Aktor Utama & Pendukung** | Mahasiswa Baru (Aktor Utama), Local Storage Engine (Aktor Pendukung) |
| **Pra-Kondisi (Preconditions)** | Aplikasi baru pertama kali dibuka; belum ada data preferensi pada localStorage. |
| **Alur Utama (Happy Path)** | Pengguna membuka aplikasi untuk pertama kali. Sistem menampilkan antarmuka ramah berisikan pertanyaan: 'Apa hal yang paling mengganggu tidurmu?' Pengguna memilih opsi 'Otak muter terus / overthinking'. Sistem menetapkan preset default audio ke 'Theta Waves 6Hz'. Sistem menampilkan pertanyaan kedua: 'Jam berapa kamu harus bangun pagi?' Pengguna memilih jam bangun '06.00'. Sistem menghitung jam tidur ideal (22.30) dan menetapkan pengingat Bedtime Anchor pada 21.45. Sistem menampilkan catatan: 'Pengaturan ini dapat Anda ubah kapan saja di menu Pengaturan'. Pengguna menekan tombol 'Mulai JalJayo'. Sistem menyimpan seluruh konfigurasi ke localStorage dan mengarahkan pengguna ke halaman utama. |
| **Alur Alternatif** | Alt-1 (Pengguna Melewati Konfigurasi): Jika pengguna memilih tombol 'Lewati', sistem menerapkan konfigurasi standar default (Theta Waves 6Hz, bangun 06.00) dan langsung menuju beranda. |
| **Pasca-Kondisi (Postconditions)** | Data profil konfigurasi tersimpan permanen di memori lokal ponsel tanpa membuat akun. |
| **Aturan Bisnis Terkait** | BR-00: Durasi proses onboarding tidak boleh melebihi 60 detik (maksimal 3 ketukan). |

 

**UCS-01: Memulai Rutinitas Tidur (One-Tap Wind-Down Player)**

| Use Case ID & Nama | UCS-01: Mengaktifkan Pemutar Audio Relaksasi Malam |
| :---- | :---- |
| **Aktor Utama & Pendukung** | Mahasiswa (Aktor Utama), Background Audio Service (Aktor Pendukung) |
| **Pra-Kondisi (Preconditions)** | Aplikasi dibuka pada malam hari; perangkat mendukung keluaran audio speaker/earphone. |
| **Alur Utama (Happy Path)** | Pengguna membuka aplikasi saat hendak tidur. Sistem otomatis menampilkan mode malam dengan tombol preset audio yang sudah terkonfigurasi. Pengguna menekan tombol 'Play' (One-Tap) dengan durasi timer default 30 menit. Sistem memutar audio frekuensi secara instan dan mengaktifkan hitung mundur timer. Pengguna menekan tombol kunci fisik ponsel (lock screen). Audio tetap berputar jernih di latar belakang dengan layar padam total (screen-off). Pada menit ke-28, sistem mengeksekusi volume fade-out bertahap hingga hening pada menit ke-30. |
| **Alur Pengecualian** | Exc-1 (Volume Perangkat 0%): Sistem memunculkan notifikasi singkat: 'Volume ponsel Anda dalam keadaan hening, harap naikkan volume'. |
| **Pasca-Kondisi (Postconditions)** | Audio berhenti otomatis, layar tetap padam, dan riwayat sesi dicatat di local storage tanpa membangunkan pengguna. |
| **Aturan Bisnis Terkait** | BR-01: Dilarang memunculkan iklan banner, notifikasi promosi, atau efek visual menyala saat audio aktif. |

 

**UCS-02: Mengakses Edukasi Siang Hari (Daily Daylight Micro-Card & Video)**

| Use Case ID & Nama | UCS-02: Mengonsumsi Konten Edukasi Sirkadian Mikro |
| :---- | :---- |
| **Aktor Utama & Pendukung** | Mahasiswa (Aktor Utama), Media Player Engine (Aktor Pendukung) |
| **Pra-Kondisi (Preconditions)** | Waktu lokal berada di antara rentang pukul 06.00 hingga 20.00. |
| **Alur Utama (Happy Path)** | Mahasiswa membuka aplikasi pada siang hari (misal pukul 13.00). Sistem menampilkan kartu edukasi harian berisikan teks kilat dan thumbnail video 30-45 detik. Mahasiswa membaca teks atau memutar video edukasi (seperti batas minum kafein jam 14.00). Sistem memutar video lancar dalam format vertikal ramah gawai. Mahasiswa menandai kartu sebagai 'Selesai Dibaca'. Sistem menyimpan status kartu di local storage. |
| **Alur Pengecualian** | Exc-1 (Akses di atas Pukul 20.00): Jika mahasiswa membuka aplikasi setelah jam 20.00, sistem menampilkan pesan: 'Mode Malam Aktif. Konten video diistirahatkan untuk menjaga hormon tidurmu' dan menyajikan pemutar audio. |
| **Pasca-Kondisi (Postconditions)** | Status kartu tersimpan di localStorage; pengguna memahami 1 aksi sirkadian sebelum malam tiba. |
| **Aturan Bisnis Terkait** | BR-02: Konten teks maksimal 80 kata dan durasi video tidak boleh melebihi 45 detik. |

**8\. BATASAN TEKNIS & KAPABILITAS ANTARMUKA (DESIGN CONSTRAINTS & APIS)**

**Batasan Arsitektur:** Merujuk standar rekayasa perangkat lunak (Marty Cagan, 2018; ISO/IEC/IEEE 29148:2018), detail bahasa pemrograman diserahkan kepada tim pengembang dalam Technical Design Document (TDD). Namun, PRD ini mengunci 3 kapabilitas antarmuka wajib:

·       **1\. Background Audio Playback API:** Aplikasi WAJIB mengintegrasikan MediaSession API / Background Audio Service bawaan OS untuk menjamin audio tetap berputar saat layar padam.

·       **2\. Local Notification API:** Menggunakan Web Notification API / Local Notification Service untuk memicu pengingat sirkadian 45 menit sebelum tidur tanpa ketergantungan koneksi cloud server.

·       **3\. Client-Side Storage:** Seluruh data konfigurasi onboarding, preferensi audio, dan riwayat disimpan murni di localStorage / IndexedDB perangkat pengguna (100% offline-first dan zero-server cost).

**9\. DAFTAR PERTANYAAN TERBUKA & MANAJEMEN RISIKO (OPEN QUESTIONS & RISKS)**

·       **Risiko 1 (Background OS Throttling):** Kebijakan agresif pemutus aplikasi latar belakang pada vendor Android tertentu. Mitigasi: Tampilkan dialog panduan 1 langkah pada pengaturan awal untuk menonaktifkan optimasi baterai bagi JalJayo.

·       **Risiko 2 (iOS Web Notification):** Pembatasan Web Push pada Safari iOS. Mitigasi: Kemas aplikasi menggunakan Progressive Web App (PWA) terinstalasi atau hybrid framework (Flutter/React Native).

