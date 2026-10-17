package id.jaljayo.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class ModeTest {

    @Test
    fun `jam tepat 06-00 adalah mode DAY`() {
        assertEquals(Mode.DAY, getMode(LocalTime.of(6, 0, 0)))
    }

    @Test
    fun `jam 06-00 lewat 1 detik adalah mode DAY`() {
        assertEquals(Mode.DAY, getMode(LocalTime.of(6, 0, 1)))
    }

    @Test
    fun `siang hari 12-00 adalah mode DAY`() {
        assertEquals(Mode.DAY, getMode(LocalTime.of(12, 0, 0)))
    }

    @Test
    fun `sore hari 19-59-59 adalah mode DAY`() {
        assertEquals(Mode.DAY, getMode(LocalTime.of(19, 59, 59)))
    }

    @Test
    fun `jam tepat 20-00 adalah mode NIGHT`() {
        assertEquals(Mode.NIGHT, getMode(LocalTime.of(20, 0, 0)))
    }

    @Test
    fun `malam hari 20-00 lewat 1 detik adalah mode NIGHT`() {
        assertEquals(Mode.NIGHT, getMode(LocalTime.of(20, 0, 1)))
    }

    @Test
    fun `tengah malam 00-00 adalah mode NIGHT`() {
        assertEquals(Mode.NIGHT, getMode(LocalTime.of(0, 0, 0)))
    }

    @Test
    fun `dini hari 05-59-59 adalah mode NIGHT`() {
        assertEquals(Mode.NIGHT, getMode(LocalTime.of(5, 59, 59)))
    }

    @Test
    fun `getCurrentMode dengan FakeAppClock bekerja sesuai waktu yang disuntikkan`() {
        val fakeClock = FakeAppClock(currentTime = LocalTime.of(14, 30))
        assertEquals(Mode.DAY, fakeClock.getCurrentMode())

        fakeClock.currentTime = LocalTime.of(21, 15)
        assertEquals(Mode.NIGHT, fakeClock.getCurrentMode())
    }
}
