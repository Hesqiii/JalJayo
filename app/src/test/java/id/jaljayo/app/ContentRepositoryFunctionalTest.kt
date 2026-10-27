package id.jaljayo.app

import id.jaljayo.app.data.SpeakerProfile
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Functional Test untuk validasi integritas aset konten edukasi CBT-I (daily_cards.json)
 * dan atribusi kepakaran sains narasumber (Vishal Dasani).
 */
class ContentRepositoryFunctionalTest {

    @Test
    fun testDailyCardsJsonStructureAndIntegrity() {
        val jsonFile = File("src/main/assets/daily_cards.json")
        assertTrue("Berkas daily_cards.json harus tersedia di assets!", jsonFile.exists())

        val jsonContent = jsonFile.readText(Charsets.UTF_8)
        val jsonArray = JSONArray(jsonContent)

        // Verifikasi jumlah kartu mencakup program 14 hari penuh
        assertEquals(14, jsonArray.length())

        val daysCovered = mutableSetOf<Int>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val id = obj.getString("id")
            val day = obj.getInt("day")
            val title = obj.getString("title")
            val body = obj.getString("body")
            val action = obj.getString("action")
            val emoji = obj.getString("emoji")

            assertTrue("ID kartu tidak boleh kosong", id.isNotBlank())
            assertTrue("Hari harus dalam rentang 1-14", day in 1..14)
            assertTrue("Judul kartu hari ke-$day tidak boleh kosong", title.isNotBlank())
            assertTrue("Body kartu hari ke-$day tidak boleh kosong", body.isNotBlank())
            assertTrue("Action kartu hari ke-$day tidak boleh kosong", action.isNotBlank())
            assertTrue("Emoji kartu hari ke-$day tidak boleh kosong", emoji.isNotBlank())

            daysCovered.add(day)

            // Verifikasi mapping video kilat hanya pada bab yang relevan (Hari 1, 2, 3, 5)
            if (day in listOf(1, 2, 3, 5)) {
                val videoResName = obj.optString("videoResName", "")
                assertTrue(
                    "Hari ke-$day harus memiliki video kilat pendamping",
                    videoResName.isNotBlank()
                )
                val rawFile = File("src/main/res/raw/$videoResName.mov")
                assertTrue(
                    "Berkas fisik video $videoResName.mov harus ada di res/raw",
                    rawFile.exists()
                )
            }
        }

        // Pastikan seluruh 14 hari berurutan lengkap
        assertEquals(14, daysCovered.size)
    }

    @Test
    fun testSpeakerProfileAttribution() {
        val speaker = SpeakerProfile()
        assertEquals("Vishal Dasani", speaker.name)
        assertTrue(speaker.title.contains("Sleep Educator"))
        assertTrue(speaker.bio.contains("CBT-I") || speaker.bio.contains("sirkadian") || speaker.bio.contains("JalJayo"))
    }
}
