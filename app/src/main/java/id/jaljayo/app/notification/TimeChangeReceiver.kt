package id.jaljayo.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import id.jaljayo.app.data.SettingsRepository
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Menjadwalkan ulang alarm Bedtime Anchor saat jam HP atau zona waktu berubah.
 *
 * Kenapa perlu?
 * AlarmManager menyimpan waktu trigger sebagai epoch millis (absolut).
 * Kalau user menggeser jam HP maju/mundur atau berpindah zona waktu,
 * epoch millis yang sudah dijadwalkan bisa jadi salah relatif terhadap
 * jam lokal yang baru. Receiver ini menghitung ulang dari awal.
 *
 * Analogi Web: Mirip event listener 'visibilitychange' yang mengecek
 * ulang state saat tab kembali aktif — tapi untuk jam sistem.
 */
class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_TIME_CHANGED && action != Intent.ACTION_TIMEZONE_CHANGED) {
            return
        }

        Log.d(TAG, "Jam/zona waktu berubah ($action). Menjadwalkan ulang alarm pengingat...")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settingsRepo = SettingsRepository(context)
                val isOnboardingDone = settingsRepo.isOnboardingCompleted.first()
                if (isOnboardingDone) {
                    val wakeHour = settingsRepo.wakeUpHour.first()
                    val wakeMinute = settingsRepo.wakeUpMinute.first()
                    BedtimeScheduler.scheduleBedtimeAlarm(
                        context = context,
                        wakeTime = LocalTime.of(wakeHour, wakeMinute)
                    )
                    Log.d(TAG, "Alarm dipulihkan untuk jam bangun %02d:%02d".format(wakeHour, wakeMinute))
                } else {
                    Log.d(TAG, "Onboarding belum selesai, alarm tidak dijadwalkan.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal menjadwalkan ulang alarm setelah perubahan waktu", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "TimeChangeReceiver"
    }
}
