package id.jaljayo.core

import java.time.LocalTime

/**
 * Representasi jadwal tidur pengguna berdasarkan siklus tidur ilmiah (90 menit per siklus):
 * @property wakeTime Jam bangun yang diinginkan pengguna.
 * @property bedtime Jam tidur ideal (5 siklus tidur = wakeTime - 7 jam 30 menit).
 * @property anchorTime Jam notifikasi pengingat tidur (bedtime - 45 menit).
 */
data class SleepSchedule(
    val wakeTime: LocalTime,
    val bedtime: LocalTime,
    val anchorTime: LocalTime
)

/**
 * Menghitung jadwal tidur dan waktu anchor pengingat tidur berdasarkan jam bangun.
 *
 * Formula sesuai AGENTS.md Bagian 4:
 * - jamTidur = jamBangun - 7 jam 30 menit
 * - jamAnchor = jamTidur - 45 menit
 *
 * Perhitungan ini secara otomatis menangani transisi melewati tengah malam
 * (contoh: bangun 08:00 -> tidur 00:30 -> anchor 23:45 hari sebelumnya).
 */
fun calculateSleepSchedule(wakeTime: LocalTime): SleepSchedule {
    val bedtime = wakeTime.minusHours(7).minusMinutes(30)
    val anchorTime = bedtime.minusMinutes(45)
    return SleepSchedule(
        wakeTime = wakeTime,
        bedtime = bedtime,
        anchorTime = anchorTime
    )
}

/**
 * Jam bangun default untuk tombol "Lewati" pada onboarding (06:00 pagi).
 */
val DEFAULT_WAKE_TIME: LocalTime = LocalTime.of(6, 0)

/**
 * Jadwal tidur default JalJayo (bangun 06:00, tidur 22:30, notifikasi 21:45).
 */
val DEFAULT_SLEEP_SCHEDULE: SleepSchedule = calculateSleepSchedule(DEFAULT_WAKE_TIME)
