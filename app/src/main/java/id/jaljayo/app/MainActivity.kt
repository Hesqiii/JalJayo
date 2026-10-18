package id.jaljayo.app

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import id.jaljayo.app.ui.theme.JalJayoTheme
import id.jaljayo.core.AppClock
import id.jaljayo.core.AudioPreset
import id.jaljayo.core.DEFAULT_SLEEP_SCHEDULE
import id.jaljayo.core.Mode
import id.jaljayo.core.SystemAppClock
import id.jaljayo.core.calculateSleepSchedule
import id.jaljayo.core.getCurrentMode
import java.time.LocalTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity Utama JalJayo — Single Activity Architecture.
 *
 * Mengelola:
 * 1. Mode Siang / Malam (Night Lockout pukul 20:00 - 05:59).
 * 2. Peralihan otomatis mode via LaunchedEffect loop.
 * 3. Koneksi MediaController ke PlaybackService untuk pemutar audio & sleep timer.
 * 4. Pengecekan volume hening perangkat.
 * 5. Panel Debug / Demo (simulasi waktu & pengujian alarm untuk demo tugas kuliah).
 */
class MainActivity : ComponentActivity() {

    private var controllerFuture: ListenableFuture<MediaController>? = null

    // Repositories
    private lateinit var settingsRepo: SettingsRepository
    private lateinit var contentRepo: ContentRepository

    // State Media Player
    private var isPlaying by mutableStateOf(false)
    private var isConnected by mutableStateOf(false)
    private var selectedPreset by mutableStateOf(AudioPreset.BROWN_NOISE)

    // State Timer
    private var isTimerActive by mutableStateOf(false)
    private var timerRemainingMs by mutableLongStateOf(0L)
    private var selectedDuration by mutableIntStateOf(SleepTimer.DEFAULT_DURATION)

    // State Mode Siang / Malam
    private val appClock: AppClock = SystemAppClock()
    private var currentMode by mutableStateOf<Mode>(appClock.getCurrentMode())
    private var debugModeOverride by mutableStateOf<Mode?>(null)

    // State Volume & Baterai
    private var isDeviceVolumeMuted by mutableStateOf(false)
    private var isBatteryOptimizing by mutableStateOf(false)
    private var testAlarmStatus by mutableStateOf<String?>(null)

    // State Izin Notifikasi (Android 13+)
    private var isNotificationPermissionGranted by mutableStateOf(true)

    // State Pengukuran Kinerja (NFR-01)
    private var playRequestTimestamp by mutableLongStateOf(0L)
    private var lastAudioLatencyMs by mutableLongStateOf(-1L)

    private fun getMemoryUsageMB(): Long {
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    }

    // Navigasi layar (onboarding / home / settings)
    private var currentScreen by mutableStateOf("loading") // "loading", "onboarding", "home", "settings"

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
            // Kumpulkan state dari DataStore
            val isOnboardingDone by settingsRepo.isOnboardingCompleted.collectAsState(initial = null)
            val savedWakeHour by settingsRepo.wakeUpHour.collectAsState(initial = 6)
            val savedWakeMinute by settingsRepo.wakeUpMinute.collectAsState(initial = 0)
            val savedPreset by settingsRepo.defaultPreset.collectAsState(initial = AudioPreset.THETA_6HZ)
            val installTimestamp by settingsRepo.installTimestamp.collectAsState(initial = System.currentTimeMillis())
            val completedCards by settingsRepo.completedCardIds.collectAsState(initial = emptySet())
            val totalSessions by settingsRepo.totalSessionsCount.collectAsState(initial = 0)
            val lastSessionDuration by settingsRepo.lastSessionDuration.collectAsState(initial = 0)

            // Tentukan layar setelah DataStore selesai load
            LaunchedEffect(isOnboardingDone) {
                if (isOnboardingDone != null) {
                    currentScreen = if (isOnboardingDone == true) "home" else "onboarding"
                }
            }

            // Sinkronisasi preset audio yang tersimpan ke pemutar saat pertama kali tersedia
            LaunchedEffect(savedPreset, isOnboardingDone) {
                if (isOnboardingDone == true && savedPreset != selectedPreset) {
                    selectedPreset = savedPreset
                    setAudioPreset(savedPreset)
                }
            }

            val sleepSchedule = remember(savedWakeHour, savedWakeMinute) {
                calculateSleepSchedule(LocalTime.of(savedWakeHour, savedWakeMinute))
            }
            val todayCard = remember(installTimestamp) {
                contentRepo.getTodayCard(installTimestamp)
            }

            val effectiveMode = debugModeOverride ?: currentMode
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            var showDebugSheet by remember { mutableStateOf(false) }

            // Loop pengecekan waktu berkala untuk peralihan mode otomatis pukul 20:00 & 06:00
            LaunchedEffect(Unit) {
                while (true) {
                    delay(30_000L)
                    val actualMode = appClock.getCurrentMode()
                    if (actualMode != currentMode) {
                        currentMode = actualMode
                        if (actualMode == Mode.NIGHT && debugModeOverride == null) {
                            snackbarHostState.showSnackbar(
                                message = "Mode Malam Aktif. Konten video diistirahatkan untuk menjaga hormon tidurmu.",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
            }

            JalJayoTheme(mode = effectiveMode) {
                SideEffect {
                    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                    val useDarkSystemIcons = effectiveMode == Mode.DAY
                    insetsController.isAppearanceLightStatusBars = useDarkSystemIcons
                    insetsController.isAppearanceLightNavigationBars = useDarkSystemIcons
                }

                when (currentScreen) {
                    "loading" -> {
                        // Layar kosong saat DataStore dimuat (biasanya < 200ms)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
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
                                    currentScreen = "home"
                                }
                            }
                        )
                    }

                    "settings" -> {
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
                                    currentScreen = "home"
                                    snackbarHostState.showSnackbar(
                                        message = "Pengaturan disimpan!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onBack = { currentScreen = "home" }
                        )
                    }

                    else -> {
                        // "home" — layar utama siang/malam
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                                    DemoControlBar(
                                        effectiveMode = effectiveMode,
                                        isOverridden = debugModeOverride != null,
                                        onOpenDebugClick = { showDebugSheet = true }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (effectiveMode) {
                                    Mode.NIGHT -> {
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
                                    Mode.DAY -> {
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
                                                debugModeOverride = Mode.NIGHT
                                            },
                                            onOpenSettingsClick = {
                                                currentScreen = "settings"
                                            }
                                        )
                                    }
                                }
                            }

                            if (showDebugSheet) {
                                val sheetState = rememberModalBottomSheetState()
                                ModalBottomSheet(
                                    onDismissRequest = { showDebugSheet = false },
                                    sheetState = sheetState
                                ) {
                                    DebugDemoPanel(
                                        currentMode = currentMode,
                                        debugModeOverride = debugModeOverride,
                                        onOverrideMode = { mode -> debugModeOverride = mode },
                                        isBatteryOptimizing = isBatteryOptimizing,
                                        onDisableBatteryOptimization = { requestDisableBatteryOptimization() },
                                        testAlarmStatus = testAlarmStatus,
                                        onTriggerTestAlarm = {
                                            BedtimeScheduler.scheduleTestAlarm(this@MainActivity, delaySeconds = 10)
                                            testAlarmStatus = "Alarm 10 detik aktif! Kunci layarmu sekarang."
                                        },
                                        onCancelTestAlarm = {
                                            BedtimeScheduler.cancelAlarm(this@MainActivity)
                                            testAlarmStatus = null
                                        },
                                        onOpenNotificationSettings = {
                                            openNotificationSettings()
                                        },
                                        audioLatencyMs = lastAudioLatencyMs,
                                        memoryUsageMB = getMemoryUsageMB(),
                                        onDismiss = { showDebugSheet = false }
                                    )
                                }
                            }
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

    private fun requestDisableBatteryOptimization() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            } catch (_: Exception) {}
        }
    }

    private fun openNotificationSettings() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Membuka langsung halaman pengaturan channel Pengingat Waktu Tidur JalJayo
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, BedtimeReceiver.CHANNEL_ID)
                }
                startActivity(intent)
            } else {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
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

        // Catat riwayat sesi relaksasi secara lokal tanpa mengganggu pengguna (PRD UCS-01)
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
 * Bar demo ringkas di bagian bawah layar.
 */
@Composable
private fun DemoControlBar(
    effectiveMode: Mode,
    isOverridden: Boolean,
    onOpenDebugClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isOverridden) {
                Text(
                    text = if (effectiveMode == Mode.NIGHT) "Simulasi malam aktif" else "Simulasi siang aktif",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }

        TextButton(onClick = onOpenDebugClick) {
            Text(
                text = "Demo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Panel Demonstrasi Dosen & Pengujian (Fase 2 / Debug Menu).
 */
@Composable
private fun DebugDemoPanel(
    currentMode: Mode,
    debugModeOverride: Mode?,
    onOverrideMode: (Mode?) -> Unit,
    isBatteryOptimizing: Boolean,
    onDisableBatteryOptimization: () -> Unit,
    testAlarmStatus: String?,
    onTriggerTestAlarm: () -> Unit,
    onCancelTestAlarm: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    audioLatencyMs: Long,
    memoryUsageMB: Long,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "🛠️ Panel Demo & Pengujian",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Gunakan kontrol ini untuk mendemokan fitur di depan dosen tanpa menunggu jam asli.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SIMULASI WAKTU / MODE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = debugModeOverride == null,
                onClick = { onOverrideMode(null) },
                label = { Text("Jam HP Asli") }
            )
            FilterChip(
                selected = debugModeOverride == Mode.DAY,
                onClick = { onOverrideMode(Mode.DAY) },
                label = { Text("Paksa Siang") }
            )
            FilterChip(
                selected = debugModeOverride == Mode.NIGHT,
                onClick = { onOverrideMode(Mode.NIGHT) },
                label = { Text("Paksa Malam") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Uji Alarm Notifikasi
        Text(
            text = "PENGUJIAN BEDTIME ANCHOR (NOTIFIKASI 10 DETIK)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onTriggerTestAlarm) {
                Text("Picu Alarm (10 dtk)")
            }
            if (testAlarmStatus != null) {
                OutlinedButton(onClick = onCancelTestAlarm) {
                    Text("Batalkan")
                }
            }
        }

        testAlarmStatus?.let { status ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optimasi Baterai
        if (isBatteryOptimizing) {
            Button(
                onClick = onDisableBatteryOptimization,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Matikan Optimasi Baterai")
            }
        } else {
            Text(
                text = "✅ Optimasi Baterai Latar Belakang Sudah Dinonaktifkan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onOpenNotificationSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⚙️ Buka Pengaturan Notifikasi & Layar Kunci")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "💡 Di HP Infinix (XOS) / Xiaomi / Oppo: Ketuk tombol di atas lalu aktifkan opsi 'Layar Kunci' (Lock screen) agar kartu notifikasi muncul saat layar HP mati.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Benchmark Kinerja & Memori NFR-01
        Text(
            text = "BENCHMARK KINERJA & MEMORI (NFR-01)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Latensi Mulai Audio:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (audioLatencyMs >= 0)
                            "$audioLatencyMs ms (Target <200ms ${if (audioLatencyMs < 200) "✅" else "⚠️"})"
                        else
                            "Tekan Play untuk Mengukur",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (audioLatencyMs in 0..199)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Penggunaan Heap RAM:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$memoryUsageMB MB (Target <40MB ${if (memoryUsageMB < 40) "✅" else "ℹ️"})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
