package id.jaljayo.core

import java.time.LocalTime

/**
 * Mode operasional JalJayo berdasarkan siklus sirkadian & Night Lockout:
 * - [DAY]: 06:00 <= jam < 20:00 (mode siang: kartu harian & video aktif).
 * - [NIGHT]: 20:00 - 05:59 (mode malam: hanya pemutar audio, tanpa stimulasi visual).
 */
enum class Mode {
    DAY,
    NIGHT
}

/**
 * Menentukan mode berdasarkan [LocalTime] yang diberikan.
 *
 * Aturan batas waktu (AGENTS.md Bagian 4 & 10):
 * - 06:00:00 -> DAY
 * - 19:59:59 -> DAY
 * - 20:00:00 -> NIGHT
 * - 05:59:59 -> NIGHT
 */
fun getMode(time: LocalTime): Mode {
    val dayStart = LocalTime.of(6, 0)
    val nightStart = LocalTime.of(20, 0)

    return if (!time.isBefore(dayStart) && time.isBefore(nightStart)) {
        Mode.DAY
    } else {
        Mode.NIGHT
    }
}

/**
 * Mengambil mode saat ini menggunakan abstraksi [AppClock].
 */
fun AppClock.getCurrentMode(): Mode = getMode(nowTime())
