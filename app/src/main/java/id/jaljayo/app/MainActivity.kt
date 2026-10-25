package id.jaljayo.app

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import id.jaljayo.app.audio.PlaybackService
import id.jaljayo.app.audio.SleepTimer
import id.jaljayo.app.data.ContentRepository
import id.jaljayo.app.data.SettingsRepository
import id.jaljayo.app.notification.BedtimeReceiver
import id.jaljayo.app.notification.BedtimeScheduler
import id.jaljayo.app.ui.home.DayScreen
import id.jaljayo.app.ui.home.NightScreen
import id.jaljayo.app.ui.onboarding.OnboardingScreen
import id.jaljayo.app.ui.settings.SettingsScreen
import id.jaljayo.app.ui.splash.SplashScreen
import id.jaljayo.app.ui.theme.DayPrimary
import id.jaljayo.app.ui.theme.JalJayoTheme
import id.jaljayo.core.AppClock
import id.jaljayo.core.AudioPreset
import id.jaljayo.core.DEFAULT_SLEEP_SCHEDULE
import id.jaljayo.core.Mode
import id.jaljayo.core.SystemAppClock
import id.jaljayo.core.calculateSleepSchedule
import id.jaljayo.core.getCurrentMode
import java.time.LocalTime
import kotlinx.coroutines.launch

/**
 * Tab Navigasi Bawah Resmi JalJayo (Sinkron Wireframe_JalJayo_LoFi.html)
 */
enum class MainNavTab {
    DAY,
    NIGHT,
    SETTINGS
}

/**
 * Activity Utama JalJayo — Single Activity Architecture (Release v1.0.0).
 * Menyesuaikan waktu secara dinamis dengan jam perangkat Android asli.
 */
class MainActivity : ComponentActivity() {

    private lateinit var settingsRepo: SettingsRepository
    private lateinit var contentRepo: ContentRepository

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var isConnected by mutableStateOf(false)
    private var isPlaying by mutableStateOf(false)
    private var selectedPreset by mutableStateOf(AudioPreset.THETA_6HZ)

    // State Timer
    private var isTimerActive by mutableStateOf(false)
    private var timerRemainingMs by mutableLongStateOf(0L)
    private var selectedDuration by mutableIntStateOf(SleepTimer.DEFAULT_DURATION)

    // State Waktu Jam Asli Perangkat
    private val appClock: AppClock = SystemAppClock()
    private var currentMode by mutableStateOf<Mode>(appClock.getCurrentMode())
    private var selectedTab by mutableStateOf<MainNavTab>(
        if (appClock.getCurrentMode() == Mode.NIGHT) MainNavTab.NIGHT else MainNavTab.DAY
    )

    // State Volume & Baterai
    private var isDeviceVolumeMuted by mutableStateOf(false)
    private var isBatteryOptimizing by mutableStateOf(false)

    // State Izin Notifikasi (Android 13+)
    private var isNotificationPermissionGranted by mutableStateOf(true)

    // State Pengukuran Kinerja (NFR-01)
    private var playRequestTimestamp by mutableLongStateOf(0L)
    private var lastAudioLatencyMs by mutableLongStateOf(-1L)

    // Navigasi layar alur masuk (splash / onboarding / home)
    private var currentScreen by mutableStateOf("splash")

    // Receiver untuk mendengarkan perubahan volume hardware secara langsung
    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            checkDeviceVolume()
        }
    }

    // Launcher izin notifikasi (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isNotificationPermissionGranted = isGranted
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settingsRepo = SettingsRepository(this)
        contentRepo = ContentRepository(this)

        requestNotificationPermissionIfNeeded()
        BedtimeReceiver.createNotificationChannel(this)
        connectToPlaybackService()
        checkBatteryOptimization()
        checkDeviceVolume()

        try {
            val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
            registerReceiver(volumeReceiver, filter)
        } catch (_: Exception) {}

        setContent {
            val isOnboardingCompleted by settingsRepo.isOnboardingCompleted.collectAsState(initial = null)
            val savedWakeHour by settingsRepo.wakeUpHour.collectAsState(initial = 6)
            val savedWakeMinute by settingsRepo.wakeUpMinute.collectAsState(initial = 0)
            val savedPreset by settingsRepo.defaultPreset.collectAsState(initial = AudioPreset.THETA_6HZ)
            val installTimestamp by settingsRepo.installTimestamp.collectAsState(initial = System.currentTimeMillis())
            val completedCards by settingsRepo.completedCardIds.collectAsState(initial = emptySet<String>())
            val totalSessions by settingsRepo.totalSessionsCount.collectAsState(initial = 0)
            val lastSessionDuration by settingsRepo.lastSessionDuration.collectAsState(initial = 0)
            val currentStreak by settingsRepo.currentStreak.collectAsState(initial = 1)
            val longestStreak by settingsRepo.longestStreak.collectAsState(initial = 1)
            val isWalkthroughCompleted by settingsRepo.isWalkthroughCompleted.collectAsState(initial = true)

            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            val sleepSchedule = remember(savedWakeHour, savedWakeMinute) {
                calculateSleepSchedule(LocalTime.of(savedWakeHour, savedWakeMinute))
            }

            val todayCard = remember(installTimestamp) {
                contentRepo.getTodayCard(installTimestamp ?: System.currentTimeMillis())
            }

            JalJayoTheme(mode = if (selectedTab == MainNavTab.NIGHT) Mode.NIGHT else Mode.DAY) {
                SideEffect {
                    val window = this@MainActivity.window
                    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                    val isNightActive = selectedTab == MainNavTab.NIGHT
                    insetsController.isAppearanceLightStatusBars = !isNightActive
                    insetsController.isAppearanceLightNavigationBars = !isNightActive
                }

                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(
                            onFinished = {
                                currentScreen = if (isOnboardingCompleted == true) "home" else "onboarding"
                            }
                        )
                    }

                    "onboarding" -> {
                        OnboardingScreen(
                            onFinished = { preset, wakeHour, wakeMinute ->
                                coroutineScope.launch {
                                    settingsRepo.setOnboardingCompleted(true)
                                    settingsRepo.saveSleepSchedule(wakeHour, wakeMinute)
                                    settingsRepo.saveDefaultPreset(preset)
                                    BedtimeScheduler.scheduleBedtimeAlarm(
                                        this@MainActivity,
                                        LocalTime.of(wakeHour, wakeMinute)
                                    )
                                    selectedPreset = preset
                                    setAudioPreset(preset)
                                    // Sinkronkan tab awal dengan jam perangkat asli saat ini
                                    val nowMode = appClock.getCurrentMode()
                                    selectedTab = if (nowMode == Mode.NIGHT) MainNavTab.NIGHT else MainNavTab.DAY
                                    currentScreen = "home"
                                }
                            }
                        )
                    }

                    else -> {
                        // "home" — Menampilkan 3 Tab Navigasi Bawah Sesuai Wireframe Lo-Fi
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                JalJayoBottomNavBar(
                                    currentTab = selectedTab,
                                    onTabSelect = { tab -> selectedTab = tab }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    MainNavTab.DAY -> {
                                        DayScreen(
                                            sleepSchedule = sleepSchedule,
                                            todayCard = todayCard,
                                            isCardCompleted = todayCard?.id in completedCards,
                                            isNotificationPermissionGranted = isNotificationPermissionGranted,
                                            onOpenNotificationSettings = {
                                                openNotificationSettings()
                                            },
                                            onMarkCardDone = {
                                                todayCard?.let { card ->
                                                    coroutineScope.launch {
                                                        settingsRepo.toggleCardCompleted(card.id)
                                                    }
                                                }
                                            },
                                            onOpenPlayerClick = {
                                                selectedTab = MainNavTab.NIGHT
                                            },
                                            onOpenSettingsClick = {
                                                selectedTab = MainNavTab.SETTINGS
                                            },
                                            contentRepo = contentRepo,
                                            currentStreak = currentStreak,
                                            longestStreak = longestStreak
                                        )
                                    }

                                    MainNavTab.NIGHT -> {
                                        NightScreen(
                                            isPlaying = isPlaying,
                                            onPlayPauseClick = {
                                                togglePlayback {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            message = "Volume ponsel Anda dalam keadaan hening, harap naikkan volume.",
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            },
                                            selectedPreset = selectedPreset,
                                            onPresetSelect = { preset ->
                                                setAudioPreset(preset)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = "Memutar preset: ${preset.displayName}",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            },
                                            selectedDuration = selectedDuration,
                                            onDurationSelect = { duration -> selectedDuration = duration },
                                            isTimerActive = isTimerActive,
                                            timerRemainingMs = timerRemainingMs,
                                            onStartTimerClick = { minutes -> startTimer(minutes) },
                                            onCancelTimerClick = { cancelTimer() },
                                            isDeviceVolumeMuted = isDeviceVolumeMuted,
                                            onRaiseVolumeClick = {
                                                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol / 2, AudioManager.FLAG_SHOW_UI)
                                                checkDeviceVolume()
                                            }
                                        )
                                    }

                                    MainNavTab.SETTINGS -> {
                                        SettingsScreen(
                                            currentWakeHour = savedWakeHour,
                                            currentWakeMinute = savedWakeMinute,
                                            currentPreset = savedPreset,
                                            totalSessionsCount = totalSessions,
                                            lastSessionDuration = lastSessionDuration,
                                            onSave = { wakeHour, wakeMinute, preset ->
                                                coroutineScope.launch {
                                                    settingsRepo.saveSleepSchedule(wakeHour, wakeMinute)
                                                    settingsRepo.saveDefaultPreset(preset)
                                                    BedtimeScheduler.scheduleBedtimeAlarm(
                                                        this@MainActivity,
                                                        LocalTime.of(wakeHour, wakeMinute)
                                                    )
                                                    selectedPreset = preset
                                                    setAudioPreset(preset)
                                                    snackbarHostState.showSnackbar(
                                                        message = "Pengaturan sirkadian berhasil disimpan!",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            },
                                            onBack = {
                                                selectedTab = MainNavTab.DAY
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Versi 1: Panduan Interaktif Otomatis Pasca-Onboarding
                        if (currentScreen == "home" && !isWalkthroughCompleted) {
                            id.jaljayo.app.ui.components.AppWalkthroughDialog(
                                onDismiss = {
                                    coroutineScope.launch {
                                        settingsRepo.setWalkthroughCompleted(true)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
        checkBatteryOptimization()
        checkDeviceVolume()
        // Selaraskan pembacaan waktu dengan jam asli perangkat
        currentMode = appClock.getCurrentMode()
    }

    private fun checkNotificationPermission() {
        isNotificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun checkDeviceVolume() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val isMuted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.isStreamMute(AudioManager.STREAM_MUSIC) || (currentVolume == 0)
        } else {
            currentVolume == 0
        }
        isDeviceVolumeMuted = isMuted
    }

    private fun checkBatteryOptimization() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        isBatteryOptimizing = !powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    private fun openNotificationSettings() {
        try {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, BedtimeReceiver.CHANNEL_ID)
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val status = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            val granted = status == PackageManager.PERMISSION_GRANTED
            isNotificationPermissionGranted = granted
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isNotificationPermissionGranted = true
        }
    }

    private fun connectToPlaybackService() {
        val sessionToken = SessionToken(
            this,
            ComponentName(this, PlaybackService::class.java)
        )

        val controllerListener = object : MediaController.Listener {
            override fun onExtrasChanged(controller: MediaController, extras: Bundle) {
                timerRemainingMs = extras.getLong(PlaybackService.EXTRA_TIMER_REMAINING_MS, 0L)
                isTimerActive = extras.getBoolean(PlaybackService.EXTRA_TIMER_ACTIVE, false)
            }
        }

        controllerFuture = MediaController.Builder(this, sessionToken)
            .setListener(controllerListener)
            .buildAsync()

        controllerFuture!!.addListener({
            try {
                val controller = controllerFuture!!.get()
                isConnected = true
                isPlaying = controller.isPlaying

                controller.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                        if (playing && playRequestTimestamp > 0L) {
                            lastAudioLatencyMs = System.currentTimeMillis() - playRequestTimestamp
                            playRequestTimestamp = 0L
                        }
                    }
                })
            } catch (_: Exception) {}
        }, mainExecutor)
    }

    private fun togglePlayback(onMutedWarning: (() -> Unit)? = null) {
        checkDeviceVolume()
        if (isDeviceVolumeMuted) {
            onMutedWarning?.invoke()
        }
        val controller = controllerFuture
            ?.takeIf { it.isDone }
            ?.get()
            ?: return

        if (controller.isPlaying) {
            controller.pause()
        } else {
            playRequestTimestamp = System.currentTimeMillis()
            controller.play()
            if (!isTimerActive) {
                startTimer(selectedDuration)
            }
        }
    }

    private fun startTimer(minutes: Int) {
        val controller = controllerFuture
            ?.takeIf { it.isDone }
            ?.get()
            ?: return

        val command = SessionCommand(PlaybackService.ACTION_START_TIMER, Bundle.EMPTY)
        val args = Bundle().apply {
            putInt(PlaybackService.EXTRA_DURATION_MINUTES, minutes)
        }
        controller.sendCustomCommand(command, args)

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                settingsRepo.recordSleepSession(minutes, selectedPreset)
            } catch (_: Exception) {}
        }
    }

    private fun cancelTimer() {
        val controller = controllerFuture
            ?.takeIf { it.isDone }
            ?.get()
            ?: return

        val command = SessionCommand(PlaybackService.ACTION_CANCEL_TIMER, Bundle.EMPTY)
        controller.sendCustomCommand(command, Bundle.EMPTY)
    }

    private fun setAudioPreset(preset: AudioPreset) {
        selectedPreset = preset
        val controller = controllerFuture
            ?.takeIf { it.isDone }
            ?.get()
            ?: return

        val command = SessionCommand(PlaybackService.ACTION_SET_PRESET, Bundle.EMPTY)
        val args = Bundle().apply {
            putString(PlaybackService.EXTRA_PRESET_NAME, preset.name)
        }
        controller.sendCustomCommand(command, args)
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(volumeReceiver)
        } catch (_: Exception) {}
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onDestroy()
    }
}

/**
 * Bilah Navigasi Bawah 3 Tab Taktil (☀️ Siang, 🌙 Malam, ⚙️ Setelan)
 * Selaras dengan spesifikasi Wireframe_JalJayo_LoFi.html.
 */
@Composable
fun JalJayoBottomNavBar(
    currentTab: MainNavTab,
    onTabSelect: (MainNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = "☀️",
                label = "Siang",
                isSelected = currentTab == MainNavTab.DAY,
                onClick = { onTabSelect(MainNavTab.DAY) }
            )
            BottomNavItem(
                icon = "🌙",
                label = "Malam",
                isSelected = currentTab == MainNavTab.NIGHT,
                onClick = { onTabSelect(MainNavTab.NIGHT) }
            )
            BottomNavItem(
                icon = "⚙️",
                label = "Setelan",
                isSelected = currentTab == MainNavTab.SETTINGS,
                onClick = { onTabSelect(MainNavTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) DayPrimary else Color(0xFF94A3B8)
            )
        }
    }
}
