package id.jaljayo.core

import java.time.LocalTime

/**
 * Abstraksi jam untuk mendapatkan waktu saat ini.
 *
 * Kenapa butuh interface ini? (Aturan AGENTS.md Bagian 7):
 * Jangan pernah memanggil `LocalTime.now()` langsung di dalam logika penentu mode!
 * Jika kita memanggil `LocalTime.now()` langsung, kita tidak bisa menguji logika
 * mode malam pada pukul 19:59 vs 20:00 tanpa mengubah jam sistem komputer/ponsel.
 *
 * Dengan interface ini:
 * - Di aplikasi nyata: kita menggunakan [SystemAppClock] (waktu nyata)
 * - Di Unit Test & Menu Demo: kita menggunakan [FakeAppClock] (bebas disetel jamnya)
 *
 * Analogi Web: Mirip seperti menggunakan library mock timer di Jest (`jest.useFakeTimers()`).
 */
interface AppClock {
    fun nowTime(): LocalTime
    fun nowEpochMillis(): Long
}

/**
 * Implementasi jam standar menggunakan waktu sistem perangkat nyata.
 */
class SystemAppClock : AppClock {
    override fun nowTime(): LocalTime = LocalTime.now()
    override fun nowEpochMillis(): Long = System.currentTimeMillis()
}

/**
 * Implementasi jam tiruan (mock) yang waktunya dapat dimanipulasi untuk testing & demo.
 */
class FakeAppClock(
    var currentTime: LocalTime = LocalTime.of(12, 0),
    var currentEpochMillis: Long = System.currentTimeMillis()
) : AppClock {
    override fun nowTime(): LocalTime = currentTime
    override fun nowEpochMillis(): Long = currentEpochMillis
}
