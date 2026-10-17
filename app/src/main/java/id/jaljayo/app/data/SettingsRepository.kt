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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jaljayo_settings")

/**
 * Repository lokal untuk mengelola pengaturan dan progres JalJayo via Jetpack DataStore.
 * Sesuai prinsip NFR-03 & Bagian 6 AGENTS.md:
 * - 100% data tersimpan di perangkat pengguna (offline-first).
 * - Nol pengumpulan data keluar (tanpa jaringan / tanpa internet).
 *
 * Analogi Web: Seperti Redux / Zustand yang terhubung ke localStorage,
 * tapi asinkron dan menggunakan Kotlin Coroutines Flow.
 */
class SettingsRepository(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_WAKE_UP_HOUR = intPreferencesKey("wake_up_hour")
        val KEY_WAKE_UP_MINUTE = intPreferencesKey("wake_up_minute")
        val KEY_DEFAULT_PRESET = stringPreferencesKey("default_preset")
        val KEY_INSTALL_TIMESTAMP = longPreferencesKey("install_timestamp")
        val KEY_COMPLETED_CARDS = stringSetPreferencesKey("completed_cards")
        val KEY_LAST_SESSION_TIMESTAMP = longPreferencesKey("last_session_timestamp")
        val KEY_LAST_SESSION_DURATION = intPreferencesKey("last_session_duration")
        val KEY_LAST_SESSION_PRESET = stringPreferencesKey("last_session_preset")
        val KEY_TOTAL_SESSIONS_COUNT = intPreferencesKey("total_sessions_count")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
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

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
            if (!prefs.contains(KEY_INSTALL_TIMESTAMP)) {
                prefs[KEY_INSTALL_TIMESTAMP] = System.currentTimeMillis()
            }
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
    }
}
