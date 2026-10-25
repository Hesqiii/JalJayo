package id.jaljayo.app.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// PALET WARNA ILMIAH JALJAYO (MVP & FOTOBIOLOGI SIRKADIAN)
// Rujukan: Chang et al. (2015) & Walker (2017)
// ============================================================================

// === MODE SIANG: Hangat, Lembut, Ramah Mata (Gaya Duolingo 2026) ===
val DayBackground = Color(0xFFFFFDF8)       // Krem hangat, tidak menyilaukan
val DaySurface = Color(0xFFFFFFFF)          // Putih bersih untuk kartu
val DaySurfaceBorder = Color(0xFFE2E8F0)    // Garis tepi kartu tebal
val DaySurfaceShadow = Color(0xFFCBD5E1)    // Efek bevel 3D dasar kartu
val DayOnBackground = Color(0xFF0F172A)     // Teks gelap tajam (kontras WCAG AAA)
val DayTextSecondary = Color(0xFF64748B)    // Teks keterangan slate

// Aksen Duolingo Siang: Vibrant Indigo & Cobalt
val DayPrimary = Color(0xFF2563EB)          // Biru energik ramah
val DayPrimaryDark = Color(0xFF1D4ED8)      // Bevel 3D bawah tombol utama
val DayOnPrimary = Color(0xFFFFFFFF)

// Aksen Kedua: Amber Sunrise & Peach (Batas Kafein / Sorotan)
val DayAccent = Color(0xFFF59E0B)           // Kuning amber matahari
val DayAccentDark = Color(0xFFD97706)       // Bevel 3D aksen
val DayOnAccent = Color(0xFFFFFFFF)
val DayAccentSoft = Color(0xFFFEF3C7)

// Status Sukses: Emerald Green khas Duolingo
val DaySuccess = Color(0xFF10B981)          // Hijau selesai/paham
val DaySuccessDark = Color(0xFF059669)      // Bevel 3D hijau
val DayOnSuccess = Color(0xFFFFFFFF)

val DayOutline = Color(0xFFE2E8F0)
val DayError = Color(0xFFEF4444)
val DayErrorDark = Color(0xFFDC2626)

// === MODE MALAM: Pure OLED Black (Proteksi Hormon Melatonin) ===
// Sesuai Chang et al. (2015): 0% rangsangan cahaya biru, ramah pupil mengantuk
val NightBackground = Color(0xFF030712)     // Nyaris hitam pekat OLED
val NightSurface = Color(0xFF0F172A)        // Slate gelap pekat
val NightSurfaceBorder = Color(0xFF1E293B)  // Border gelap
val NightSurfaceShadow = Color(0xFF0B0F19)  // Bevel 3D malam
val NightOnBackground = Color(0xFFF1F5F9)   // Putih lembut tidak silau
val NightTextSecondary = Color(0xFF94A3B8)  // Abu-abu redup

val NightPrimary = Color(0xFF3B82F6)        // Biru diredam
val NightPrimaryDark = Color(0xFF1E40AF)    // Bevel 3D malam
val NightOnPrimary = Color(0xFFFFFFFF)

val NightAccent = Color(0xFFEAB308)         // Bintang/bulan lembut
val NightAccentDark = Color(0xFFCA8A04)
val NightOnAccent = Color(0xFF0F172A)

val NightSuccess = Color(0xFF10B981)
val NightSuccessDark = Color(0xFF059669)

val NightOutline = Color(0xFF1E293B)
val NightError = Color(0xFFF87171)
