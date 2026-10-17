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
 * BroadcastReceiver yang mendengarkan event saat ponsel selesai di-restart (`BOOT_COMPLETED`).
 *
 * Di sistem Android, semua alarm di AlarmManager otomatis terhapus saat ponsel mati atau restart.
 * Oleh karena itu, kita wajib mendaftarkan receiver ini agar jadwal pengingat tidur
 * dapat didaftarkan ulang secara otomatis tanpa mengharuskan pengguna membuka aplikasi terlebih dahulu.
 *
 * Analogi Web: Mirip seperti systemd unit service dengan status `WantedBy=default.target`
 * yang otomatis bangkit kembali saat server reboot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Ponsel selesai restart (BOOT_COMPLETED). Menjadwalkan ulang alarm pengingat...")

            val pendingResult = goAsync()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
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
                        Log.d(TAG, "Alarm pengingat tidur berhasil dipulihkan untuk jam bangun %02d:%02d".format(wakeHour, wakeMinute))
                    } else {
                        Log.d(TAG, "Pengguna belum menyelesaikan onboarding, alarm tidak dijadwalkan.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal memulihkan jadwal alarm setelah boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
