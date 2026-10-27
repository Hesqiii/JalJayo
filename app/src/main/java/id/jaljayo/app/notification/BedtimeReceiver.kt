package id.jaljayo.app.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import id.jaljayo.app.MainActivity
import id.jaljayo.app.R

/**
 * BroadcastReceiver yang dipicu oleh AlarmManager saat tiba waktu pengingat tidur
 * (45 menit sebelum jam tidur) atau saat pengujian alarm.
 */
class BedtimeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "BedtimeReceiver menerima trigger alarm!")

        // 1. Nyalakan layar HP jika dalam keadaan mati/terkunci
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "JalJayo:BedtimeReminderWake"
            )
            wakeLock.acquire(5000L) // Hidupkan layar selama 5 detik agar pengguna langsung melihat notifikasi
            Log.i(TAG, "WakeLock berhasil menyalakan layar ponsel")
        } catch (e: Exception) {
            Log.w(TAG, "Gagal mengaktifkan wake lock untuk layar", e)
        }

        // 2. Cek izin notifikasi (wajib di Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                Log.w(TAG, "Tidak dapat menampilkan notifikasi: Izin POST_NOTIFICATIONS belum diberikan")
                return
            }
        }

        // 3. Buat Notification Channel dengan prioritas tinggi dan suara
        createNotificationChannel(context)

        // 4. Intent untuk membuka MainActivity saat notifikasi diklik
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_OPEN_APP,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 5. Bangun notifikasi pengingat tidur untuk kartu lock screen
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Saatnya Bersiap Tidur 🌙"
        val message = intent.getStringExtra(EXTRA_MESSAGE)
            ?: "45 menit lagi jam tidurmu. Matikan layar dan mulai relaksasi bersama JalJayo."

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrationPattern = longArrayOf(0, 500, 200, 500)

        // Intent untuk aksi langsung "Mulai Relaksasi" dari lockscreen/notifikasi
        val playIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_START_RELAXATION, true)
        }
        val playPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_PLAY_AUDIO,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_bedtime)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_stat_bedtime,
                "▶ Mulai Relaksasi",
                playPendingIntent
            )
            .build()

        // 6. Tampilkan notifikasi
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            Log.i(TAG, "Notifikasi pengingat tidur berhasil ditampilkan!")
        } catch (e: SecurityException) {
            Log.e(TAG, "Gagal memposting notifikasi karena kendala keamanan", e)
        }
    }

    companion object {
        const val TAG = "BedtimeReceiver"
        // ID channel v5 & ID notifikasi 2001 (tidak tabrakan dengan Media3 yang menggunakan 1001)
        const val CHANNEL_ID = "jaljayo_bedtime_reminder_v5"
        const val NOTIFICATION_ID = 2001
        private const val REQUEST_CODE_OPEN_APP = 1002

        const val EXTRA_TITLE = "extra_notification_title"
        const val EXTRA_MESSAGE = "extra_notification_message"
        const val EXTRA_START_RELAXATION = "extra_start_relaxation"
        private const val REQUEST_CODE_PLAY_AUDIO = 1003

        /**
         * Mendaftarkan Notification Channel secara dini (dapat dipanggil dari MainActivity).
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Bersihkan channel usang agar tidak membingungkan sistem Android & XOS
                listOf(
                    "jaljayo_bedtime_anchor",
                    "jaljayo_bedtime_anchor_v2",
                    "jaljayo_bedtime_anchor_v3",
                    "jaljayo_bedtime_anchor_v4"
                ).forEach { oldId ->
                    try {
                        notificationManager.deleteNotificationChannel(oldId)
                    } catch (_: Exception) {}
                }

                val name = "Pengingat Waktu Tidur"
                val descriptionText = "Notifikasi transisi tidur 45 menit sebelum jam tidur"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()

                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                    enableLights(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setSound(soundUri, audioAttributes)
                    setShowBadge(true)
                }

                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
