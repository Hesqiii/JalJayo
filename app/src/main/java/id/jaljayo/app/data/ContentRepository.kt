package id.jaljayo.app.data

import android.content.Context
import org.json.JSONArray

/**
 * Model data untuk satu kartu edukasi harian.
 *
 * @param id       ID unik kartu (misal "card_01"), dipakai sebagai key di DataStore.
 * @param day      Hari ke-N (1–14) kartu ini muncul.
 * @param title    Judul singkat topik kebiasaan.
 * @param body     Teks penjelasan ≤ 80 kata (BR-02).
 * @param action   Satu aksi kebiasaan konkret yang harus dilakukan hari ini.
 * @param emoji    Emoji representasi topik untuk UI.
 */
data class DailyCard(
    val id: String,
    val day: Int,
    val title: String,
    val body: String,
    val action: String,
    val emoji: String
)

/**
 * Membaca dan menyajikan konten kartu edukasi dari assets/daily_cards.json.
 *
 * Konten 100% offline — dibaca dari APK bundle, tanpa jaringan (sesuai NFR-03).
 * Parsing pakai org.json bawaan Android — tanpa library Gson/Moshi tambahan.
 */
class ContentRepository(private val context: Context) {

    /**
     * Memuat semua 14 kartu dari assets. Lazy load satu kali saja.
     */
    private val cards: List<DailyCard> by lazy { loadCardsFromAssets() }

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
        // daysSinceInstall = 0 pada hari yang sama dengan instalasi → kartu hari ke-1
        val dayIndex = (daysSinceInstall % cards.size) + 1
        return cards.firstOrNull { it.day == dayIndex } ?: cards.first()
    }

    /**
     * Mengembalikan semua kartu, misalnya untuk preview di menu debug.
     */
    fun getAllCards(): List<DailyCard> = cards

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
                    title = obj.getString("title"),
                    body = obj.getString("body"),
                    action = obj.getString("action"),
                    emoji = obj.getString("emoji")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
