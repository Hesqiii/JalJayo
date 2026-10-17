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
    fun `menekan tombol lewati (null) memetakan ke preset default THETA_6HZ`() {
        assertEquals(AudioPreset.THETA_6HZ, getPresetForComplaint(null))
    }
}
