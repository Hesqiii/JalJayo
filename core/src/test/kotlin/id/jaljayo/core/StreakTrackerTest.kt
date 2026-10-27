package id.jaljayo.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class StreakTrackerTest {

    private val today = LocalDate.of(2026, 10, 27)

    @Test
    fun `pengguna baru mendapatkan streak 1 dan selebrasi`() {
        val result = StreakTracker.evaluateStreak(
            currentStreak = 0,
            lastCompletedDate = null,
            today = today
        )
        assertEquals(1, result.newStreak)
        assertTrue(result.isCelebrationEarned)
        assertEquals(StreakStatus.STARTED, result.status)
    }

    @Test
    fun `akses kedua di hari yang sama tidak menduplikasi streak`() {
        val result = StreakTracker.evaluateStreak(
            currentStreak = 3,
            lastCompletedDate = today,
            today = today
        )
        assertEquals(3, result.newStreak)
        assertFalse(result.isCelebrationEarned)
        assertEquals(StreakStatus.ALREADY_COMPLETED_TODAY, result.status)
    }

    @Test
    fun `akses berturut-turut di hari berikutnya menaikkan streak`() {
        val yesterday = today.minusDays(1)
        val result = StreakTracker.evaluateStreak(
            currentStreak = 4,
            lastCompletedDate = yesterday,
            today = today
        )
        assertEquals(5, result.newStreak)
        assertTrue(result.isCelebrationEarned)
        assertEquals(StreakStatus.INCREMENTED, result.status)
    }

    @Test
    fun `buffer toleransi 24 jam menyelamatkan streak dari reset`() {
        // Terlewat 1 hari (kemarin lusa)
        val twoDaysAgo = today.minusDays(2)
        val result = StreakTracker.evaluateStreak(
            currentStreak = 7,
            lastCompletedDate = twoDaysAgo,
            today = today
        )
        assertEquals(8, result.newStreak)
        assertTrue(result.isCelebrationEarned)
        assertEquals(StreakStatus.SAVED_BY_BUFFER, result.status)
    }

    @Test
    fun `absen lebih dari 2 hari mereset streak kembali ke 1`() {
        val fourDaysAgo = today.minusDays(4)
        val result = StreakTracker.evaluateStreak(
            currentStreak = 10,
            lastCompletedDate = fourDaysAgo,
            today = today
        )
        assertEquals(1, result.newStreak)
        assertTrue(result.isCelebrationEarned)
        assertEquals(StreakStatus.RESET, result.status)
    }

    @Test
    fun `format notifikasi gamifikasi memuat emoji api dan jam tidur ideal`() {
        val (title, message) = StreakTracker.formatGamifiedNotification(5, "22:30")
        assertTrue(title.contains("🔥"))
        assertTrue(title.contains("5 Hari"))
        assertTrue(message.contains("22:30"))
        assertTrue(message.contains("45 menit lagi"))
    }
}
