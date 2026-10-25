package id.jaljayo.core

/**
 * Pilihan keluhan tidur utama pada smart diagnostic onboarding.
 * Menggunakan bahasa Indonesia baku, formal, dan mudah dipahami tanpa label teknis.
 */
enum class Complaint(
    val title: String,
    val description: String,
    val iconEmoji: String
) {
    /** Ruminasi / overthinking sebelum tidur */
    RACING_THOUGHTS(
        title = "Pikiran Berputar & Ruminasi Cemas",
        description = "Kerap memikirkan beban studi, pekerjaan, atau kecemasan saat berbaring.",
        iconEmoji = "⚡"
    ),

    /** Gangguan polusi suara lingkungan */
    NOISY_ROOM(
        title = "Polusi Suara Lingkungan Sekitar",
        description = "Terganggu oleh suara bising kendaraan, tetangga, atau lingkungan yang kurang kondusif.",
        iconEmoji = "🔊"
    ),

    /** Kelelahan mata akibat gawai / screen fatigue */
    SCREEN_FATIGUE(
        title = "Stimulasi Layar Gawai Larut Malam",
        description = "Mata lelah dan sulit merasakan kantuk alami akibat paparan cahaya biru gawai.",
        iconEmoji = "📱"
    ),

    /** Ketegangan tubuh & jadwal tidak konsisten */
    IRREGULAR_SCHEDULE(
        title = "Ketegangan Fisik & Ritme Tidak Teratur",
        description = "Tubuh terasa tegang dan jam istirahat bergeser akibat jadwal harian yang tidak konsisten.",
        iconEmoji = "🌊"
    );

    /** Kompatibilitas terhadap kode legacy */
    val label: String get() = title
}

/**
 * Preset audio relaksasi malam yang didukung JalJayo:
 */
enum class AudioPreset(val displayName: String) {
    THETA_6HZ("Theta Waves 6Hz"),
    BROWN_NOISE("Brown Noise"),
    RAIN_SOUND("Suara Hujan Alami"),
    OCEAN_WAVES("Suara Deburan Ombak")
}

/**
 * Memetakan keluhan pengguna ke preset audio default (AGENTS.md Bagian 4).
 *
 * - RACING_THOUGHTS -> [AudioPreset.THETA_6HZ] (Penenang Otak & Gelombang Lambat)
 * - NOISY_ROOM -> [AudioPreset.BROWN_NOISE] (Peredam Polusi Frekuensi Rendah)
 * - SCREEN_FATIGUE -> [AudioPreset.RAIN_SOUND] (Relaksasi Sensorik & Penurun Stimulasi)
 * - IRREGULAR_SCHEDULE -> [AudioPreset.OCEAN_WAVES] (Pelemasan Otot & Ritme Sirkadian)
 * - null (Tombol "Lewati") -> [AudioPreset.THETA_6HZ] (default)
 */
fun getPresetForComplaint(complaint: Complaint?): AudioPreset {
    return when (complaint) {
        Complaint.RACING_THOUGHTS -> AudioPreset.THETA_6HZ
        Complaint.NOISY_ROOM -> AudioPreset.BROWN_NOISE
        Complaint.SCREEN_FATIGUE -> AudioPreset.RAIN_SOUND
        Complaint.IRREGULAR_SCHEDULE -> AudioPreset.OCEAN_WAVES
        null -> AudioPreset.THETA_6HZ
    }
}
