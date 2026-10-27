package id.jaljayo.app

import id.jaljayo.app.notification.BedtimeReceiver
import id.jaljayo.core.Complaint
import id.jaljayo.core.StreakStatus
import id.jaljayo.core.StreakTracker
import id.jaljayo.core.calculateSleepSchedule
import id.jaljayo.core.getPresetForComplaint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * End-to-End (E2E) State Machine & User Journey Testing untuk alur lengkap aplikasi JalJayo:
 * 1. Journey Onboarding -> Priming Izin Notifikasi -> Penjadwalan Sirkadian Otomatis
 * 2. Journey Transisi Hari-ke-Malam & Retensi Streak Gamifikasi
 * 3. Journey Tombol Aksi Cepat Bilah Notifikasi (Direct Lockscreen Trigger)
 */
class UserJourneyE2ETest {

    @Test
    fun testJourneyOnboardingToAutomaticCircadianSchedule() {
        // Tahap 1: Pengguna memilih keluhan overthinking malam hari
        val selectedComplaint = Complaint.RACING_THOUGHTS
        val mappedPreset = getPresetForComplaint(selectedComplaint)
        assertNotNull(mappedPreset)
        assertEquals("Theta Waves 6Hz", mappedPreset.displayName)

        // Tahap 2: Pengguna menyetel target bangun jam 06:00
        val wakeHour = 6
        val wakeMinute = 0
        val wakeTime = LocalTime.of(wakeHour, wakeMinute)
        val schedule = calculateSleepSchedule(wakeTime)

        // Verifikasi kalkulasi otomatis sirkadian (Zero-Cognitive Setup)
        assertEquals(LocalTime.of(22, 30), schedule.bedtime)
        assertEquals(LocalTime.of(21, 45), schedule.anchorTime)
        assertEquals(LocalTime.of(14, 30), schedule.caffeineCurfew)

        // Tahap 3: Pembuatan teks notifikasi berbasis gamifikasi dan streak hari pertama
        val currentStreak = 1
        val bedtimeStr = "%02d:%02d".format(schedule.bedtime.hour, schedule.bedtime.minute)
        val (title, message) = StreakTracker.formatGamifiedNotification(currentStreak, bedtimeStr)

        assertTrue(title.contains("🔥"))
        assertTrue(title.contains("1 Hari"))
        assertTrue(message.contains(bedtimeStr))
        assertTrue(message.contains("45 menit lagi"))
    }

    @Test
    fun testJourneyStreakGamificationRetentionLoop() {
        var streak = 0
        val baseDate = LocalDate.of(2026, 10, 25)

        // Hari 1: Selesai onboarding dan sesi pertama
        val day1Result = StreakTracker.evaluateStreak(streak, null, baseDate)
        assertEquals(1, day1Result.newStreak)
        assertTrue(day1Result.isCelebrationEarned)
        streak = day1Result.newStreak

        // Hari 1 Malam: Buka kembali di hari yang sama (streak tidak boleh bertambah ganda)
        val reaccessResult = StreakTracker.evaluateStreak(streak, baseDate, baseDate)
        assertEquals(1, reaccessResult.newStreak)
        assertFalse(reaccessResult.isCelebrationEarned)
        assertEquals(StreakStatus.ALREADY_COMPLETED_TODAY, reaccessResult.status)

        // Hari 2: Konsistensi berturut-turut
        val day2Date = baseDate.plusDays(1)
        val day2Result = StreakTracker.evaluateStreak(streak, baseDate, day2Date)
        assertEquals(2, day2Result.newStreak)
        assertTrue(day2Result.isCelebrationEarned)
        streak = day2Result.newStreak

        // Hari 4: Terlewat 1 hari (Buffer toleransi 24 jam menyelamatkan streak)
        val day4Date = day2Date.plusDays(2)
        val day4Result = StreakTracker.evaluateStreak(streak, day2Date, day4Date)
        assertEquals(3, day4Result.newStreak)
        assertEquals(StreakStatus.SAVED_BY_BUFFER, day4Result.status)
        assertTrue(day4Result.isCelebrationEarned)
        streak = day4Result.newStreak

        // Terlewat 4 hari: Reset kembali ke 1
        val longAbsenceDate = day4Date.plusDays(4)
        val resetResult = StreakTracker.evaluateStreak(streak, day4Date, longAbsenceDate)
        assertEquals(1, resetResult.newStreak)
        assertEquals(StreakStatus.RESET, resetResult.status)
    }

    @Test
    fun testJourneyLockscreenNotificationDirectActionConstants() {
        // Verifikasi konstanta kunci untuk Intent aksi cepat di BedtimeReceiver
        assertEquals("extra_start_relaxation", BedtimeReceiver.EXTRA_START_RELAXATION)
        assertEquals("jaljayo_bedtime_reminder_v5", BedtimeReceiver.CHANNEL_ID)
        assertEquals(2001, BedtimeReceiver.NOTIFICATION_ID)
    }
}
