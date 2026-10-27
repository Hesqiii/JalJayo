package id.jaljayo.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * Utility untuk menjadwalkan notifikasi pengingat tidur (Bedtime Anchor)
 * menggunakan AlarmManager native Android.
 *
 * Kenapa AlarmManager dan bukan WorkManager?
 * - WorkManager dirancang untuk background tasks yang fleksibel waktunya (bisa tertunda 10-15 menit oleh Doze mode).
 * - AlarmManager dengan `setExactAndAllowWhileIdle` atau `setAlarmClock` menjamin
 *   notifikasi ditembakkan tepat waktu (presisi menit/detik), bahkan saat ponsel dalam mode Doze (layar mati lama).
 *
 * Analogi Web: Mirip seperti penjadwal `cron` yang terikat langsung ke hardware OS/RTC (Real Time Clock).
 */
object BedtimeScheduler {

    private const val TAG = "BedtimeScheduler"
    private const val REQUEST_CODE_BEDTIME_ALARM = 2001

    /**
     * Memeriksa apakah aplikasi memiliki izin untuk menjadwalkan exact alarm (Android 12+).
     */
    fun canScheduleExact(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Menjadwalkan alarm pada waktu milidetik tertentu (Epoch millis).
     */
    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        title: String? = null,
        message: String? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BedtimeReceiver::class.java).apply {
            if (title != null) putExtra(BedtimeReceiver.EXTRA_TITLE, title)
            if (message != null) putExtra(BedtimeReceiver.EXTRA_MESSAGE, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BEDTIME_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Gunakan setAlarmClock sebagai metode utama:
            // setAlarmClock diperlakukan oleh Android sebagai alarm jam weker pengguna nyata.
            // Keunggulan:
            // 1. Kebal terhadap freezer/proxy alarm vendor (seperti Infinix Usf_Hiber / MIUI Battery Saver).
            // 2. Membangunkan ponsel dari deep sleep (Doze mode) secara instan dan tepat waktu.
            // 3. Tidak memerlukan izin khusus yang dibatasi di Android 14+ (SCHEDULE_EXACT_ALARM).
            val showIntent = Intent(context, id.jaljayo.app.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                0,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d(TAG, "Alarm berhasil dijadwalkan dengan setAlarmClock pada timestamp $triggerAtMillis")
        } catch (e: Exception) {
            Log.w(TAG, "setAlarmClock gagal, mencoba fallback setExactAndAllowWhileIdle", e)
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.d(TAG, "Fallback: Alarm dijadwalkan dengan setExactAndAllowWhileIdle")
            } catch (e2: SecurityException) {
                Log.e(TAG, "Gagal total menjadwalkan alarm karena SecurityException", e2)
            }
        }
    }

    /**
     * Menjadwalkan notifikasi pengujian cepat (contoh: 10 detik dari sekarang).
     * Sangat berguna untuk verifikasi prototipe di perangkat fisik tanpa harus menunggu tengah malam.
     */
    fun scheduleTestAlarm(context: Context, delaySeconds: Int = 10) {
        val triggerAtMillis = System.currentTimeMillis() + (delaySeconds * 1000L)
        scheduleAlarm(
            context = context,
            triggerAtMillis = triggerAtMillis,
            title = "Tes Pengingat Tidur 🌙",
            message = "Notifikasi berhasil muncul setelah $delaySeconds detik saat aplikasi ditutup!"
        )
    }

    /**
     * Menjadwalkan notifikasi Bedtime Anchor harian berdasarkan jam bangun pengguna.
     *
     * Formula: anchorTime = wakeTime - 7 jam 30 menit (bedtime) - 45 menit
     * Contoh: bangun 06:00 → tidur 22:30 → notifikasi 21:45
     *
     * Kalau waktu anchor hari ini sudah lewat, otomatis dijadwalkan untuk keesokan hari.
     */
    fun scheduleBedtimeAlarm(
        context: Context,
        wakeTime: java.time.LocalTime,
        currentStreak: Int = 1
    ) {
        val bedtime = wakeTime.minusHours(7).minusMinutes(30)
        val anchorTime = bedtime.minusMinutes(45)

        val now = java.util.Calendar.getInstance()
        val trigger = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, anchorTime.hour)
            set(java.util.Calendar.MINUTE, anchorTime.minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        // Kalau waktunya sudah lewat, jadwalkan besok
        if (trigger.timeInMillis <= now.timeInMillis) {
            trigger.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        val bedtimeStr = "%02d:%02d".format(bedtime.hour, bedtime.minute)
        val (title, message) = id.jaljayo.core.StreakTracker.formatGamifiedNotification(currentStreak, bedtimeStr)
        scheduleAlarm(
            context = context,
            triggerAtMillis = trigger.timeInMillis,
            title = title,
            message = message
        )
        Log.d(TAG, "Bedtime alarm dijadwalkan pukul ${anchorTime.hour}:${anchorTime.minute} dengan streak $currentStreak")
    }

    /**
     * Membatalkan jadwal alarm pengingat tidur jika pengguna mematikannya di pengaturan.
     */
    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BedtimeReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BEDTIME_ALARM,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarm pengingat tidur dibatalkan")
        }
    }
}
