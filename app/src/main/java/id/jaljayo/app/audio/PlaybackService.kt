package id.jaljayo.app.audio

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import id.jaljayo.app.R
import id.jaljayo.core.AudioPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Service yang memutar audio relaksasi di background.
 *
 * Extends MediaSessionService sehingga:
 * - Otomatis menampilkan notifikasi foreground dengan kontrol play/pause
 * - Audio tetap jalan saat layar mati atau app di-swipe dari recents
 * - Kontrol lock screen dan media button otomatis ditangani oleh MediaSession
 *
 * Komunikasi dengan Activity lewat dua mekanisme:
 * 1. MediaController standard (play/pause) — sudah bawaan Media3
 * 2. Custom commands (timer start/cancel) — Activity kirim perintah,
 *    Service broadcast state lewat session extras (seperti WebSocket push)
 */
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var serviceScope: CoroutineScope
    private lateinit var player: ExoPlayer
    private var sleepTimer: SleepTimer? = null

    override fun onCreate() {
        super.onCreate()

        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        player.repeatMode = Player.REPEAT_MODE_ONE

        loadAudioPreset(AudioPreset.BROWN_NOISE)

        sleepTimer = SleepTimer(
            scope = serviceScope,
            onTick = { remainingMs -> broadcastTimerState(remainingMs, true) },
            onVolumeChange = { volume -> player.volume = volume },
            onFinished = {
                player.pause()
                player.volume = 1.0f
                broadcastTimerState(0L, false)
            }
        )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(SessionCallback())
            .build()
    }

    /**
     * Memuat file audio relaksasi sesuai preset yang dipilih.
     */
    private fun loadAudioPreset(preset: AudioPreset) {
        val resId = when (preset) {
            AudioPreset.THETA_6HZ -> R.raw.theta_6hz
            AudioPreset.BROWN_NOISE -> R.raw.brown_noise
            AudioPreset.RAIN_SOUND -> R.raw.hujan
            AudioPreset.OCEAN_WAVES -> R.raw.ombak
        }
        val uri = RawResourceDataSource.buildRawResourceUri(resId)
        val wasPlaying = player.isPlaying
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        if (wasPlaying) {
            player.play()
        }
    }

    /**
     * Broadcast state timer ke semua MediaController yang terhubung
     * lewat session extras. Activity menerima ini lewat
     * MediaController.Listener.onExtrasChanged().
     *
     * Analoginya: seperti server mengirim event lewat WebSocket ke
     * semua client yang terhubung.
     */
    private fun broadcastTimerState(remainingMs: Long, active: Boolean) {
        val extras = Bundle().apply {
            putLong(EXTRA_TIMER_REMAINING_MS, remainingMs)
            putBoolean(EXTRA_TIMER_ACTIVE, active)
        }
        mediaSession?.setSessionExtras(extras)
    }

    /**
     * Callback untuk menangani custom commands dari Activity.
     *
     * Di Media3, custom command harus:
     * 1. Didaftarkan di onConnect() → supaya controller diizinkan mengirimnya
     * 2. Ditangani di onCustomCommand() → logika sesungguhnya
     */
    private inner class SessionCallback : MediaSession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            // Ambil command bawaan, lalu tambahkan custom commands kita.
            // Pakai AcceptedResultBuilder (bukan accept()) karena builder
            // menggunakan named setter — tidak ada risiko salah urutan parameter.
            val defaultResult = super.onConnect(session, controller)
            val sessionCommands = defaultResult.availableSessionCommands.buildUpon()
                .add(SessionCommand(ACTION_START_TIMER, Bundle.EMPTY))
                .add(SessionCommand(ACTION_CANCEL_TIMER, Bundle.EMPTY))
                .add(SessionCommand(ACTION_SET_PRESET, Bundle.EMPTY))
                .build()

            // AcceptedResultBuilder default-nya sudah menerima semua player commands
            // (play, pause, seek, dll). Kita hanya perlu set session commands.
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                ACTION_START_TIMER -> {
                    val minutes = args.getInt(EXTRA_DURATION_MINUTES, SleepTimer.DEFAULT_DURATION)
                    sleepTimer?.start(minutes)
                }
                ACTION_CANCEL_TIMER -> {
                    sleepTimer?.cancel()
                    session.player.volume = 1.0f
                    broadcastTimerState(0L, false)
                }
                ACTION_SET_PRESET -> {
                    val presetName = args.getString(EXTRA_PRESET_NAME)
                    val preset = try {
                        AudioPreset.valueOf(presetName ?: AudioPreset.BROWN_NOISE.name)
                    } catch (_: Exception) {
                        AudioPreset.BROWN_NOISE
                    }
                    loadAudioPreset(preset)
                }
                else -> return super.onCustomCommand(session, controller, customCommand, args)
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        sleepTimer?.cancel()
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    companion object {
        // Custom command actions
        const val ACTION_START_TIMER = "id.jaljayo.action.START_TIMER"
        const val ACTION_CANCEL_TIMER = "id.jaljayo.action.CANCEL_TIMER"
        const val ACTION_SET_PRESET = "id.jaljayo.action.SET_PRESET"

        // Bundle keys
        const val EXTRA_DURATION_MINUTES = "duration_minutes"
        const val EXTRA_TIMER_REMAINING_MS = "timer_remaining_ms"
        const val EXTRA_TIMER_ACTIVE = "timer_active"
        const val EXTRA_PRESET_NAME = "preset_name"
    }
}
