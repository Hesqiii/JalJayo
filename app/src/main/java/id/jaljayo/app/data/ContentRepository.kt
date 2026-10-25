package id.jaljayo.app.data

import android.content.Context
import org.json.JSONArray

/**
 * Model data untuk satu kartu edukasi harian.
 *
 * @param id           ID unik kartu (misal "card_01"), dipakai sebagai key di DataStore.
 * @param day          Hari ke-N (1–14) kartu ini muncul.
 * @param chapterId    Nomor bab modul CBT-I (1–4).
 * @param chapterTitle Nama bab modul CBT-I.
 * @param title        Judul singkat topik kebiasaan.
 * @param body         Teks penjelasan ≤ 80 kata (BR-02).
 * @param action       Satu aksi kebiasaan konkret yang harus dilakukan hari ini.
 * @param emoji        Emoji representasi topik untuk UI.
 * @param speaker      Nama narasumber/pakar sains tidur (Vishal Dasani).
 * @param videoResName Nama resource video pendamping di res/raw/.
 */
data class DailyCard(
    val id: String,
    val day: Int,
    val chapterId: Int = 1,
    val chapterTitle: String = "Regulasi Kafein & Nutrisi",
    val title: String,
    val body: String,
    val action: String,
    val emoji: String,
    val speaker: String = "Vishal Dasani (Certified Sleep Educator)",
    val videoResName: String? = null
)

/**
 * Informasi profil narasumber utama edukasi tidur JalJayo.
 */
data class SpeakerProfile(
    val name: String = "Vishal Dasani",
    val title: String = "Certified Sleep Educator & Sleep Coach Indonesia",
    val bio: String = "Pakar dan praktisi kesehatan tidur terkemuka di Indonesia. Menjadi narasumber utama panduan sains sirkadian, manajemen kafein, dekompresi overthinking, dan teknik relaksasi non-obat yang diadopsi ke dalam aplikasi JalJayo."
)

/**
 * Membaca dan menyajikan konten kartu edukasi dari assets/daily_cards.json.
 *
 * Konten 100% offline — dibaca dari APK bundle, tanpa jaringan (sesuai NFR-03).
 * Mendukung pengelompokan per bab CBT-I dan atribusi narasumber Vishal Dasani.
 */
class ContentRepository(private val context: Context) {

    /**
     * Memuat semua 14 kartu dari assets. Lazy load satu kali saja.
     */
    private val cards: List<DailyCard> by lazy { loadCardsFromAssets() }

    /**
     * Profil narasumber utama (Vishal Dasani).
     */
    val speakerProfile: SpeakerProfile = SpeakerProfile()

    /**
     * Menentukan kartu aktif hari ini berdasarkan hari ke-N sejak instalasi.
     *
     * - Hari ke-1 → kartu dengan day = 1
     * - Hari ke-15 dst → kembali ke kartu hari ke-1 (modulo, agar konten tidak habis)
     *
     * @param installTimestamp epoch millis saat pertama kali instalasi / onboarding selesai.
     * @return Kartu yang seharusnya tampil hari ini, atau null jika JSON kosong.
     */
    fun getTodayCard(installTimestamp: Long): DailyCard? {
        if (cards.isEmpty()) return null

        val msPerDay = 24 * 60 * 60 * 1000L
        val daysSinceInstall = ((System.currentTimeMillis() - installTimestamp) / msPerDay).toInt()
        val dayIndex = (daysSinceInstall % cards.size) + 1
        return cards.firstOrNull { it.day == dayIndex } ?: cards.first()
    }

    /**
     * Mengembalikan semua kartu dalam katalog luring.
     */
    fun getAllCards(): List<DailyCard> = cards

    /**
     * Mengembalikan daftar kartu yang dikelompokkan berdasarkan nomor bab CBT-I.
     * 1: Regulasi Kafein & Nutrisi
     * 2: Manajemen Cahaya & Gawai
     * 3: Dekompresi Stres & Overthinking
     * 4: Penataan Kamar & Stimulus Kasur
     */
    fun getCardsByChapter(chapterId: Int): List<DailyCard> {
        return cards.filter { it.chapterId == chapterId }
    }

    private fun loadCardsFromAssets(): List<DailyCard> {
        return try {
            val json = context.assets.open("daily_cards.json")
                .bufferedReader()
                .use { it.readText() }
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                DailyCard(
                    id = obj.getString("id"),
                    day = obj.getInt("day"),
                    chapterId = obj.optInt("chapterId", 1),
                    chapterTitle = obj.optString("chapterTitle", "Regulasi Kafein & Nutrisi"),
                    title = obj.getString("title"),
                    body = obj.getString("body"),
                    action = obj.getString("action"),
                    emoji = obj.getString("emoji"),
                    speaker = obj.optString("speaker", "Vishal Dasani (Certified Sleep Educator)"),
                    videoResName = if (!obj.isNull("videoResName") && obj.has("videoResName")) obj.getString("videoResName") else null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
