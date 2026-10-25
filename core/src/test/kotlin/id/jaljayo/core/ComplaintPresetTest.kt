package id.jaljayo.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ComplaintPresetTest {

    @Test
    fun `keluhan racing thoughts memetakan ke preset audio THETA_6HZ`() {
        assertEquals(AudioPreset.THETA_6HZ, getPresetForComplaint(Complaint.RACING_THOUGHTS))
    }

    @Test
    fun `keluhan noisy room memetakan ke preset audio BROWN_NOISE`() {
        assertEquals(AudioPreset.BROWN_NOISE, getPresetForComplaint(Complaint.NOISY_ROOM))
    }

    @Test
    fun `keluhan screen fatigue memetakan ke preset audio RAIN_SOUND`() {
        assertEquals(AudioPreset.RAIN_SOUND, getPresetForComplaint(Complaint.SCREEN_FATIGUE))
    }

    @Test
    fun `keluhan irregular schedule memetakan ke preset audio OCEAN_WAVES`() {
        assertEquals(AudioPreset.OCEAN_WAVES, getPresetForComplaint(Complaint.IRREGULAR_SCHEDULE))
    }

    @Test
    fun `menekan tombol lewati (null) memetakan ke preset default THETA_6HZ`() {
        assertEquals(AudioPreset.THETA_6HZ, getPresetForComplaint(null))
    }
}
