package id.jaljayo.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class SleepScheduleTest {

    @Test
    fun `bangun jam 06-00 menghasilkan tidur 22-30 dan anchor 21-45`() {
        val schedule = calculateSleepSchedule(LocalTime.of(6, 0))
        assertEquals(LocalTime.of(6, 0), schedule.wakeTime)
        assertEquals(LocalTime.of(22, 30), schedule.bedtime)
        assertEquals(LocalTime.of(21, 45), schedule.anchorTime)
    }

    @Test
    fun `bangun jam 05-00 menghasilkan tidur 21-30 dan anchor 20-45`() {
        val schedule = calculateSleepSchedule(LocalTime.of(5, 0))
        assertEquals(LocalTime.of(5, 0), schedule.wakeTime)
        assertEquals(LocalTime.of(21, 30), schedule.bedtime)
        assertEquals(LocalTime.of(20, 45), schedule.anchorTime)
    }

    @Test
    fun `bangun jam 07-00 menghasilkan tidur 23-30 dan anchor 22-45`() {
        val schedule = calculateSleepSchedule(LocalTime.of(7, 0))
        assertEquals(LocalTime.of(7, 0), schedule.wakeTime)
        assertEquals(LocalTime.of(23, 30), schedule.bedtime)
        assertEquals(LocalTime.of(22, 45), schedule.anchorTime)
    }

    @Test
    fun `melewati tengah malam - bangun jam 08-00 menghasilkan tidur 00-30 dan anchor 23-45`() {
        // 08:00 - 7 jam 30 menit = 00:30 (tengah malam)
        // 00:30 - 45 menit = 23:45 (hari sebelumnya)
        val schedule = calculateSleepSchedule(LocalTime.of(8, 0))
        assertEquals(LocalTime.of(8, 0), schedule.wakeTime)
        assertEquals(LocalTime.of(0, 30), schedule.bedtime)
        assertEquals(LocalTime.of(23, 45), schedule.anchorTime)
    }

    @Test
    fun `melewati tengah malam - bangun jam 08-30 menghasilkan tidur 01-00 dan anchor 00-15`() {
        val schedule = calculateSleepSchedule(LocalTime.of(8, 30))
        assertEquals(LocalTime.of(8, 30), schedule.wakeTime)
        assertEquals(LocalTime.of(1, 0), schedule.bedtime)
        assertEquals(LocalTime.of(0, 15), schedule.anchorTime)
    }

    @Test
    fun `default sleep schedule sesuai dengan bangun jam 06-00`() {
        assertEquals(LocalTime.of(6, 0), DEFAULT_SLEEP_SCHEDULE.wakeTime)
        assertEquals(LocalTime.of(22, 30), DEFAULT_SLEEP_SCHEDULE.bedtime)
        assertEquals(LocalTime.of(21, 45), DEFAULT_SLEEP_SCHEDULE.anchorTime)
    }
}
