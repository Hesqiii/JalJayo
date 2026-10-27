package id.jaljayo.app

import id.jaljayo.core.StreakTracker
import id.jaljayo.core.calculateSleepSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

/**
 * Functional Test untuk kalkulasi jadwal dan parameter pengingat tidur (BedtimeScheduler).
 * Memverifikasi logika presisi formula sirkadian dan konten pesan gamifikasi.
 */
class BedtimeSchedulerFunctionalTest {

    @Test
    fun testBedtimeScheduleCalculationLogic() {
        val wakeTime = LocalTime.of(6, 0)
        val schedule = calculateSleepSchedule(wakeTime)

        // Verifikasi formula: Jam Bangun 06:00 -> Jam Tidur 22:30 (-7.5 jam)
        assertEquals(LocalTime.of(22, 30), schedule.bedtime)

        // Verifikasi anchor time: 45 menit sebelum tidur -> 21:45
        assertEquals(LocalTime.of(21, 45), schedule.anchorTime)

        // Verifikasi batas kafein: 8 jam sebelum tidur -> 14:30
        assertEquals(LocalTime.of(14, 30), schedule.caffeineCurfew)
    }

    @Test
    fun testGamifiedNotificationContentForVariousStreaks() {
        val bedtimeStr = "22:30"

        // Pengujian streak hari pertama
        val (titleDay1, messageDay1) = StreakTracker.formatGamifiedNotification(1, bedtimeStr)
        assertTrue(titleDay1.contains("🔥"))
        assertTrue(titleDay1.contains("1 Hari"))
        assertTrue(messageDay1.contains("22:30"))

        // Pengujian streak hari ke-7 (pencapaian kritis 1 minggu)
        val (titleDay7, messageDay7) = StreakTracker.formatGamifiedNotification(7, bedtimeStr)
        assertTrue(titleDay7.contains("7 Hari"))
        assertTrue(messageDay7.contains("Mulai relaksasi"))
    }

    @Test
    fun testMidnightCrossingScheduleLogic() {
        // Kasus bangun jam 08:30 pagi
        val wakeTime = LocalTime.of(8, 30)
        val schedule = calculateSleepSchedule(wakeTime)

        // Jam tidur melewati tengah malam: 01:00
        assertEquals(LocalTime.of(1, 0), schedule.bedtime)
        // Waktu anchor pengingat: 00:15
        assertEquals(LocalTime.of(0, 15), schedule.anchorTime)
        // Batas kafein: 17:00
        assertEquals(LocalTime.of(17, 0), schedule.caffeineCurfew)
    }
}
