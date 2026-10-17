package id.jaljayo.core

/**
 * Pilihan keluhan tidur utama pada smart diagnostic onboarding (maks 3 ketukan):
 */
enum class Complaint(val label: String) {
    /** "Otak muter terus / overthinking" -> rekomendasi Theta Waves 6Hz */
    RACING_THOUGHTS("Otak muter terus / overthinking"),

    /** "Kamar bising / tidak tenang" -> rekomendasi Brown Noise */
    NOISY_ROOM("Kamar bising / tidak tenang")
}

/**
 * Preset audio relaksasi malam yang didukung JalJayo:
 */
enum class AudioPreset(val displayName: String) {
    THETA_6HZ("Theta Waves 6Hz"),
    BROWN_NOISE("Brown Noise"),
    RAIN_SOUND("Suara Hujan"),
    OCEAN_WAVES("Suara Ombak")
}

/**
 * Memetakan keluhan pengguna ke preset audio default (AGENTS.md Bagian 4).
 *
 * - Racing Thoughts -> [AudioPreset.THETA_6HZ]
 * - Noisy Room -> [AudioPreset.BROWN_NOISE]
 * - Tombol "Lewati" (null) -> [AudioPreset.THETA_6HZ] (default)
 */
fun getPresetForComplaint(complaint: Complaint?): AudioPreset {
    return when (complaint) {
        Complaint.RACING_THOUGHTS -> AudioPreset.THETA_6HZ
        Complaint.NOISY_ROOM -> AudioPreset.BROWN_NOISE
        null -> AudioPreset.THETA_6HZ
    }
}
