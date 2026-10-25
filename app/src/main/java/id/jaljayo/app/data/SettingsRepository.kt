package id.jaljayo.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import id.jaljayo.core.AudioPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jaljayo_settings")

/**
 * Repository lokal untuk mengelola pengaturan, streak sirkadian, dan progres JalJayo via Jetpack DataStore.
 * Sesuai prinsip NFR-03 & Bagian 6 AGENTS.md:
 * - 100% data tersimpan di perangkat pengguna (offline-first).
 * - Nol pengumpulan data keluar (tanpa jaringan / tanpa internet).
 */
class SettingsRepository(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_WALKTHROUGH_COMPLETED = booleanPreferencesKey("walkthrough_completed")
        val KEY_WAKE_UP_HOUR = intPreferencesKey("wake_up_hour")
        val KEY_WAKE_UP_MINUTE = intPreferencesKey("wake_up_minute")
        val KEY_DEFAULT_PRESET = stringPreferencesKey("default_preset")
        val KEY_INSTALL_TIMESTAMP = longPreferencesKey("install_timestamp")
        val KEY_COMPLETED_CARDS = stringSetPreferencesKey("completed_cards")
        val KEY_LAST_SESSION_TIMESTAMP = longPreferencesKey("last_session_timestamp")
        val KEY_LAST_SESSION_DURATION = intPreferencesKey("last_session_duration")
        val KEY_LAST_SESSION_PRESET = stringPreferencesKey("last_session_preset")
        val KEY_TOTAL_SESSIONS_COUNT = intPreferencesKey("total_sessions_count")
        val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
        val KEY_LONGEST_STREAK = intPreferencesKey("longest_streak")
        val KEY_LAST_STREAK_EPOCH_DAY = longPreferencesKey("last_streak_epoch_day")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isWalkthroughCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_WALKTHROUGH_COMPLETED] ?: false
    }

    val wakeUpHour: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_WAKE_UP_HOUR] ?: 6
    }

    val wakeUpMinute: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_WAKE_UP_MINUTE] ?: 0
    }

    val defaultPreset: Flow<AudioPreset> = dataStore.data.map { prefs ->
        val name = prefs[KEY_DEFAULT_PRESET] ?: AudioPreset.BROWN_NOISE.name
        try {
            AudioPreset.valueOf(name)
        } catch (_: Exception) {
            AudioPreset.BROWN_NOISE
        }
    }

    val installTimestamp: Flow<Long> = dataStore.data.map { prefs ->
        prefs[KEY_INSTALL_TIMESTAMP] ?: System.currentTimeMillis()
    }

    val completedCardIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[KEY_COMPLETED_CARDS] ?: emptySet()
    }

    val lastSessionTimestamp: Flow<Long> = dataStore.data.map { prefs ->
        prefs[KEY_LAST_SESSION_TIMESTAMP] ?: 0L
    }

    val lastSessionDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_LAST_SESSION_DURATION] ?: 0
    }

    val lastSessionPreset: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_LAST_SESSION_PRESET] ?: ""
    }

    val totalSessionsCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_TOTAL_SESSIONS_COUNT] ?: 0
    }

    val currentStreak: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_CURRENT_STREAK] ?: 1
    }

    val longestStreak: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_LONGEST_STREAK] ?: 1
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
            if (!prefs.contains(KEY_INSTALL_TIMESTAMP)) {
                prefs[KEY_INSTALL_TIMESTAMP] = System.currentTimeMillis()
            }
            if (!prefs.contains(KEY_CURRENT_STREAK)) {
                prefs[KEY_CURRENT_STREAK] = 1
                prefs[KEY_LONGEST_STREAK] = 1
                prefs[KEY_LAST_STREAK_EPOCH_DAY] = LocalDate.now().toEpochDay()
            }
        }
    }

    suspend fun setWalkthroughCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_WALKTHROUGH_COMPLETED] = completed
        }
    }

    suspend fun saveSleepSchedule(hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_WAKE_UP_HOUR] = hour
            prefs[KEY_WAKE_UP_MINUTE] = minute
        }
    }

    suspend fun saveDefaultPreset(preset: AudioPreset) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_PRESET] = preset.name
        }
    }

    suspend fun toggleCardCompleted(cardId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_COMPLETED_CARDS]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(cardId)) {
                current.remove(cardId)
            } else {
                current.add(cardId)
            }
            prefs[KEY_COMPLETED_CARDS] = current
        }
        recordDailyActionForStreak()
    }

    /**
     * Memperbarui streak harian berdasarkan aksi konkret (menandai kartu atau memutar audio).
     * Menerapkan prinsip Duolingo streak engine + Loss Aversion (Kahneman & Tversky, 1979).
     */
    suspend fun recordDailyActionForStreak() {
        val todayEpochDay = LocalDate.now().toEpochDay()
        dataStore.edit { prefs ->
            val lastDay = prefs[KEY_LAST_STREAK_EPOCH_DAY] ?: 0L
            val current = prefs[KEY_CURRENT_STREAK] ?: 1
            val longest = prefs[KEY_LONGEST_STREAK] ?: 1

            when {
                lastDay == todayEpochDay -> {
                    // Sudah dihitung hari ini, pertahankan streak
                }
                lastDay == todayEpochDay - 1 -> {
                    // Konsisten berturut-turut! Tambah streak +1
                    val newStreak = current + 1
                    prefs[KEY_CURRENT_STREAK] = newStreak
                    prefs[KEY_LAST_STREAK_EPOCH_DAY] = todayEpochDay
                    if (newStreak > longest) {
                        prefs[KEY_LONGEST_STREAK] = newStreak
                    }
                }
                lastDay == 0L -> {
                    // Pertama kali memulai kebiasaan
                    prefs[KEY_CURRENT_STREAK] = 1
                    prefs[KEY_LONGEST_STREAK] = maxOf(longest, 1)
                    prefs[KEY_LAST_STREAK_EPOCH_DAY] = todayEpochDay
                }
                else -> {
                    // Terlewat lebih dari 1 hari -> reset ke 1
                    prefs[KEY_CURRENT_STREAK] = 1
                    prefs[KEY_LAST_STREAK_EPOCH_DAY] = todayEpochDay
                }
            }
        }
    }

    /**
     * Mencatat riwayat sesi relaksasi malam secara lokal (UCS-01 Postconditions).
     * Disimpan secara silent tanpa memunculkan prompt atau membangunkan pengguna.
     */
    suspend fun recordSleepSession(durationMinutes: Int, preset: AudioPreset) {
        dataStore.edit { prefs ->
            val currentCount = prefs[KEY_TOTAL_SESSIONS_COUNT] ?: 0
            prefs[KEY_TOTAL_SESSIONS_COUNT] = currentCount + 1
            prefs[KEY_LAST_SESSION_TIMESTAMP] = System.currentTimeMillis()
            prefs[KEY_LAST_SESSION_DURATION] = durationMinutes
            prefs[KEY_LAST_SESSION_PRESET] = preset.name
        }
        recordDailyActionForStreak()
    }
}
