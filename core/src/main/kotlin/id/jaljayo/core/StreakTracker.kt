package id.jaljayo.core

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Logika domain untuk sistem Streak Gamification (Duolingo-style)
 * Berdasarkan prinsip Fogg Behavior Model & Loss Aversion (Kahneman & Tversky, 1979).
 */
object StreakTracker {

    /**
     * Memeriksa apakah streak dapat bertambah atau dipertahankan berdasarkan tanggal akses terakhir.
     * Mengimplementasikan buffer toleransi 24 jam untuk memitigasi "What-the-hell effect".
     */
    fun evaluateStreak(
        currentStreak: Int,
        lastCompletedDate: LocalDate?,
        today: LocalDate
    ): StreakEvaluationResult {
        if (lastCompletedDate == null) {
            return StreakEvaluationResult(
                newStreak = 1,
                isCelebrationEarned = true,
                status = StreakStatus.STARTED
            )
        }

        val daysDifference = ChronoUnit.DAYS.between(lastCompletedDate, today)

        return when {
            daysDifference <= 0L -> {
                // Selesai pada hari yang sama (tetap aktif, tidak double-count)
                StreakEvaluationResult(
                    newStreak = currentStreak.coerceAtLeast(1),
                    isCelebrationEarned = false,
                    status = StreakStatus.ALREADY_COMPLETED_TODAY
                )
            }
            daysDifference == 1L -> {
                // Selesai kemarin, hari ini konsisten berlanjut
                StreakEvaluationResult(
                    newStreak = currentStreak + 1,
                    isCelebrationEarned = true,
                    status = StreakStatus.INCREMENTED
                )
            }
            daysDifference == 2L -> {
                // Grace period / buffer 24 jam (mitigasi keputusasaan hilangnya streak)
                StreakEvaluationResult(
                    newStreak = currentStreak + 1,
                    isCelebrationEarned = true,
                    status = StreakStatus.SAVED_BY_BUFFER
                )
            }
            else -> {
                // Terlewat lebih dari 2 hari -> reset ke 1
                StreakEvaluationResult(
                    newStreak = 1,
                    isCelebrationEarned = true,
                    status = StreakStatus.RESET
                )
            }
        }
    }

    /**
     * Membentuk salinan teks notifikasi dinamis berbasis Loss Aversion.
     */
    fun formatGamifiedNotification(streak: Int, bedtimeStr: String): Pair<String, String> {
        val safeStreak = if (streak <= 0) 1 else streak
        val title = "Pertahankan Konsistensimu 🔥 (Streak: $safeStreak Hari)"
        val message = "45 menit lagi jendela tidur idealmu tiba ($bedtimeStr). Mulai relaksasi dan lindungi konsistensi tidurmu!"
        return Pair(title, message)
    }
}

enum class StreakStatus {
    STARTED,
    INCREMENTED,
    ALREADY_COMPLETED_TODAY,
    SAVED_BY_BUFFER,
    RESET
}

data class StreakEvaluationResult(
    val newStreak: Int,
    val isCelebrationEarned: Boolean,
    val status: StreakStatus
)
