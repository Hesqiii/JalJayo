package id.jaljayo.app.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

/**
 * Timer tidur yang berjalan di dalam service (bukan di Activity).
 *
 * Cara kerja:
 * 1. User memilih durasi (15/30/45 menit)
 * 2. Timer countdown di background
 * 3. Saat sisa 2 menit: fade-out eksponensial dari volume saat itu ke 0
 * 4. Setelah fade selesai: panggil onFinished (untuk stop player)
 *
 * Kenapa eksponensial?
 * Linear fade (turun sama rata) terasa "tiba-tiba" di awal.
 * Eksponensial membuat volume turun pelan di awal, makin cepat di akhir —
 * seperti CSS ease-in. Telinga manusia mempersepsi volume secara logaritmik,
 * jadi kurva ini terasa lebih alami.
 *
 * @param scope CoroutineScope dari service (bukan dari Activity)
 * @param onTick callback setiap detik — dipakai untuk broadcast sisa waktu ke UI
 * @param onVolumeChange callback untuk mengubah volume player (0.0f–1.0f)
 * @param onFinished callback saat timer selesai (untuk stop/release player)
 */
class SleepTimer(
    private val scope: CoroutineScope,
    private val onTick: (remainingMs: Long) -> Unit,
    private val onVolumeChange: (Float) -> Unit,
    private val onFinished: () -> Unit
) {
    private var timerJob: Job? = null

    /** Sisa waktu dalam milidetik. 0 = timer tidak aktif. */
    var remainingMs: Long = 0L
        private set

    val isRunning: Boolean
        get() = timerJob?.isActive == true

    /**
     * Mulai timer dengan durasi tertentu dalam menit.
     * Kalau sudah ada timer berjalan, timer lama dibatalkan.
     */
    fun start(durationMinutes: Int) {
        cancel()

        val totalMs = durationMinutes * 60 * 1000L
        remainingMs = totalMs
        onTick(remainingMs)

        timerJob = scope.launch {
            // Fase 1: countdown biasa (tanpa fade)
            // = total durasi - 2 menit fade
            val fadeMs = FADE_DURATION_MS
            val countdownMs = (totalMs - fadeMs).coerceAtLeast(0L)

            if (countdownMs > 0) {
                val steps = (countdownMs / TICK_INTERVAL_MS).toInt()
                repeat(steps) {
                    delay(TICK_INTERVAL_MS)
                    remainingMs -= TICK_INTERVAL_MS
                    onTick(remainingMs)
                }
                val leftover = countdownMs - (steps * TICK_INTERVAL_MS)
                if (leftover > 0) {
                    delay(leftover)
                    remainingMs -= leftover
                    onTick(remainingMs)
                }
            }

            // Fase 2: fade-out eksponensial selama 2 menit
            val actualFadeMs = remainingMs.coerceAtMost(fadeMs)
            if (actualFadeMs > 0) {
                val fadeSteps = (actualFadeMs / FADE_TICK_MS).toInt()
                var tickAccumulator = 0L
                repeat(fadeSteps) { step ->
                    delay(FADE_TICK_MS)
                    remainingMs -= FADE_TICK_MS
                    tickAccumulator += FADE_TICK_MS

                    // progress: 0.0 (awal fade) → 1.0 (akhir fade)
                    val progress = (step + 1).toFloat() / fadeSteps

                    // Kurva eksponensial: volume = (1 - progress)^3
                    // Pangkat 3 → turun pelan di awal, makin cepat di akhir
                    val volume = (1f - progress).pow(FADE_EXPONENT)
                    onVolumeChange(volume.coerceIn(0f, 1f))

                    // Broadcast sisa waktu setiap ~1 detik (bukan setiap 100ms
                    // supaya tidak membanjiri UI)
                    if (tickAccumulator >= TICK_INTERVAL_MS) {
                        onTick(remainingMs)
                        tickAccumulator = 0L
                    }
                }
            }

            // Selesai — volume pasti 0, minta service stop
            remainingMs = 0
            onVolumeChange(0f)
            onTick(0L)
            onFinished()
        }
    }

    /** Batalkan timer. Volume tidak direset (caller yang tanggung jawab). */
    fun cancel() {
        timerJob?.cancel()
        timerJob = null
        remainingMs = 0
    }

    companion object {
        /** Interval update countdown (1 detik). */
        private const val TICK_INTERVAL_MS = 1000L

        /** Durasi fase fade-out: 2 menit (sesuai AGENTS.md Section 4). */
        private const val FADE_DURATION_MS = 2 * 60 * 1000L

        /** Interval update volume saat fade (100ms → smooth). */
        private const val FADE_TICK_MS = 100L

        /**
         * Eksponen untuk kurva fade.
         * 3.0 = cubic ease-in. Turun pelan → makin cepat → diam.
         */
        private const val FADE_EXPONENT = 3.0f

        /** Pilihan durasi yang tersedia (menit). Default = 30. */
        val DURATION_OPTIONS = listOf(15, 30, 45)
        const val DEFAULT_DURATION = 30
    }
}
