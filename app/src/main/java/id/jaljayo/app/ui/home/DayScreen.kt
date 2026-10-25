package id.jaljayo.app.ui.home

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.jaljayo.app.R
import id.jaljayo.app.data.ContentRepository
import id.jaljayo.app.data.DailyCard
import id.jaljayo.app.ui.components.Tactile3DButton
import id.jaljayo.app.ui.components.TactileCard
import id.jaljayo.app.ui.theme.DayBackground
import id.jaljayo.app.ui.theme.DayPrimary
import id.jaljayo.app.ui.theme.DayPrimaryDark
import id.jaljayo.app.ui.theme.DaySuccess
import id.jaljayo.app.ui.theme.DaySuccessDark
import id.jaljayo.app.ui.theme.DaySurfaceBorder
import id.jaljayo.app.ui.theme.DayTextSecondary
import id.jaljayo.core.SleepSchedule
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

/**
 * Layar Mode Siang (DayScreen) — Pilar Siang & Transisi JalJayo (06:00 - 19:59).
 * Desain Taktil Bergaya Duolingo 2026 & Wireframe Sync:
 * - Sapaan ramah personal & penanda mode siang aktif.
 * - Banner batas kafein dinamis (dihitung otomatis dari jam tidur).
 * - Daily Spotlight Micro-Card (Teks intisari sains & pemutar video asli Vishal Dasani).
 * - Modul penjelajahan Katalog Bab CBT-I luring interaktif.
 * - Tombol taktil 3D pemutar audio relaksasi malam.
 */
@Composable
fun DayScreen(
    sleepSchedule: SleepSchedule,
    todayCard: DailyCard?,
    isCardCompleted: Boolean,
    onMarkCardDone: () -> Unit,
    onOpenPlayerClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    isNotificationPermissionGranted: Boolean = true,
    onOpenNotificationSettings: () -> Unit = {},
    contentRepo: ContentRepository? = null,
    currentStreak: Int = 1,
    longestStreak: Int = 1
) {
    val context = LocalContext.current
    val repository = remember(context, contentRepo) { contentRepo ?: ContentRepository(context) }
    val scrollState = rememberScrollState()
    var showCatalogDialog by remember { mutableStateOf(false) }
    var activeModalCard by remember { mutableStateOf<DailyCard?>(null) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val timeFmt = DateTimeFormatter.ofPattern("HH.mm")
    val curfewStr = sleepSchedule.caffeineCurfew.format(timeFmt)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DayBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // === Top Header Bergaya Duolingo ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Halo, Sahabat Tidur! ☀️",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Mode Siang Aktif (Akses s.d 20.00)",
                    fontSize = 11.5.sp,
                    color = DayTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Streak Pill Duolingo (🔥 Counter)
                id.jaljayo.app.ui.components.TactileStreakPill(
                    streakCount = currentStreak,
                    onClick = { showStreakDialog = true }
                )

                Spacer(Modifier.width(8.dp))

                // Tombol Panduan Info (Versi 2)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF))
                        .border(1.5.dp, Color(0xFFBFDBFE), CircleShape)
                        .clickable { showHelpDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("ℹ️", fontSize = 16.sp)
                }

                Spacer(Modifier.width(8.dp))

                // Tombol Ikon Pengaturan Taktil
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .border(1.5.dp, DaySurfaceBorder, CircleShape)
                        .clickable(onClick = onOpenSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚙️", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner peringatan jika izin notifikasi nonaktif (Android 13+)
        if (!isNotificationPermissionGranted) {
            NotificationPermissionBanner(
                onOpenSettingsClick = onOpenNotificationSettings
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val now = java.time.LocalTime.now()
        val isCurfewPassed = now.isAfter(sleepSchedule.caffeineCurfew)

        // === Banner Batas Kafein Dinamis (Curfew Banner - Drake et al., 2013) ===
        TactileCard(
            backgroundColor = if (isCurfewPassed) Color(0xFFFEF2F2) else Color(0xFFFFFBEB),
            borderColor = if (isCurfewPassed) Color(0xFFFECACA) else Color(0xFFFDE68A),
            shadowColor = if (isCurfewPassed) Color(0xFFFCA5A5) else Color(0xFFFCD34D)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isCurfewPassed) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isCurfewPassed) "☕⚠️" else "☕", fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isCurfewPassed) "Batas Kafein Telah Lewat: $curfewStr" else "Batas Kafein Terakhir: $curfewStr",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurfewPassed) Color(0xFF991B1B) else Color(0xFFB45309)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (isCurfewPassed) {
                            "Waktu paruh kafein 5–7 jam di tubuh. Hindari kopi/kafein dan beralihlah ke air mineral agar fase Deep Sleep malam ini (${sleepSchedule.bedtime.format(timeFmt)}) terjaga."
                        } else {
                            "Waktu paruh kafein 5–7 jam di tubuh. Hentikan konsumsi kopi 8 jam sebelum jam tidur ideal (${sleepSchedule.bedtime.format(timeFmt)})."
                        },
                        fontSize = 11.5.sp,
                        color = if (isCurfewPassed) Color(0xFF7F1D1D) else Color(0xFF92400E),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === Kartu Edukasi Harian (Daily Spotlight Micro-Card) ===
        if (todayCard != null) {
            DailyCardSection(
                card = todayCard,
                isCompleted = isCardCompleted,
                onMarkDone = onMarkCardDone,
                onWatchVideo = { activeModalCard = todayCard }
            )
        } else {
            TactileCard {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Memuat materi edukasi sains tidur...",
                        fontSize = 13.sp,
                        color = DayTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === Tombol Akses Katalog Bab Edukasi CBT-I ===
        TactileCard(
            backgroundColor = Color(0xFFEFF6FF),
            borderColor = Color(0xFFBFDBFE),
            shadowColor = Color(0xFF93C5FD)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCatalogDialog = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📚", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Katalog Bab Edukasi CBT-I",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DayPrimary
                        )
                        Text(
                            text = "Eksplorasi Kafein, Gawai, Stres, & Kamar",
                            fontSize = 11.5.sp,
                            color = Color(0xFF3B82F6)
                        )
                    }
                }
                Text("➔", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === Kartu Jadwal Sirkadian Terkalibrasi ===
        SleepScheduleCard(schedule = sleepSchedule)

        Spacer(modifier = Modifier.height(20.dp))

        // === Tombol Taktil Menuju Ruang Relaksasi Malam ===
        Tactile3DButton(
            text = "🌙 Masuk Ruang Relaksasi Malam",
            onClick = onOpenPlayerClick,
            containerColor = DayPrimary,
            shadowColor = DayPrimaryDark
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal Video Player Nyata jika user ingin menonton
    activeModalCard?.let { card ->
        VerticalVideoModalPlayer(
            card = card,
            onDismiss = { activeModalCard = null },
            onMarkDoneAndDismiss = {
                onMarkCardDone()
                activeModalCard = null
            }
        )
    }

    // Dialog Katalog Bab CBT-I Lengkap & Interaktif
    if (showCatalogDialog) {
        CbtiCatalogDialog(
            contentRepo = repository,
            onDismiss = { showCatalogDialog = false },
            onSelectCardToWatch = { card ->
                activeModalCard = card
            }
        )
    }

    // Modal Dialog Status Streak Sirkadian (Duolingo Style)
    if (showStreakDialog) {
        id.jaljayo.app.ui.components.StreakCelebrationDialog(
            streakCount = currentStreak,
            longestStreak = longestStreak,
            onDismiss = { showStreakDialog = false }
        )
    }

    // Modal Dialog Panduan Pemakaian On-Demand (Versi 2)
    if (showHelpDialog) {
        id.jaljayo.app.ui.components.AppHelpGuideDialog(
            onDismiss = { showHelpDialog = false }
        )
    }
}

/**
 * Komponen Kartu Edukasi Harian (Spotlight Micro-Card).
 */
@Composable
private fun DailyCardSection(
    card: DailyCard,
    isCompleted: Boolean,
    onMarkDone: () -> Unit,
    onWatchVideo: () -> Unit
) {
    TactileCard {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Topik & Badge Hari
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "HARI KE-${card.day} • ${card.chapterTitle.uppercase()}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DayPrimary
                    )
                }

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✓ Diterapkan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DaySuccess
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Judul & Emoji
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(card.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = card.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // Body Intisari Sains
            Text(
                text = card.body,
                fontSize = 12.5.sp,
                color = Color(0xFF334155),
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            // Banner Aksi Konkret
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, DaySurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "🎯 Rencana Aksi Hari Ini:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DayPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = card.action,
                        fontSize = 12.sp,
                        color = Color(0xFF1E293B),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Atribusi Narasumber Vishal Dasani dengan Foto Profil Asli
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.photo_profile_narasumber),
                        contentDescription = "Foto Narasumber Vishal Dasani",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, DayPrimary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Narasumber: Vishal Dasani",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DayPrimary
                        )
                        Text(
                            text = "Certified Sleep Educator & Sleep Coach Indonesia",
                            fontSize = 10.sp,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Tombol Tonton Video & Tandai Paham
            if (card.videoResName != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Tactile3DButton(
                        text = "▶ Tonton Video Kilat",
                        onClick = onWatchVideo,
                        containerColor = DayPrimary,
                        shadowColor = DayPrimaryDark,
                        modifier = Modifier.weight(1.1f)
                    )

                    Tactile3DButton(
                        text = if (isCompleted) "✓ Paham" else "Tandai Paham",
                        onClick = onMarkDone,
                        containerColor = if (isCompleted) DaySuccess else Color(0xFF64748B),
                        shadowColor = if (isCompleted) DaySuccessDark else Color(0xFF475569),
                        modifier = Modifier.weight(0.9f)
                    )
                }
            } else {
                // Hari tanpa video kilat: tombol penuh (Wireframe Screen 2 Sync)
                Tactile3DButton(
                    text = if (isCompleted) "✓ Selesai & Diterapkan" else "✓ Tandai Paham & Terapkan",
                    onClick = onMarkDone,
                    containerColor = if (isCompleted) DaySuccess else DayPrimary,
                    shadowColor = if (isCompleted) DaySuccessDark else DayPrimaryDark,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Pemutar Video Edukasi Nyata Berformat Vertikal (Hardware-Accelerated VideoView).
 * Memutar berkas dari res/raw (H.264/AAC) dengan kontrol putar/jeda dan durasi dinamis.
 */
@Composable
private fun VerticalVideoModalPlayer(
    card: DailyCard,
    onDismiss: () -> Unit,
    onMarkDoneAndDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var currentSec by remember { mutableIntStateOf(0) }
    var totalSec by remember { mutableIntStateOf(35) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    val rawResId = remember(card.videoResName) {
        when (card.videoResName) {
            "video_kafein" -> R.raw.video_kafein
            "video_stres" -> R.raw.video_stres
            "video_kamar" -> R.raw.video_kamar
            "video_layar" -> R.raw.video_layar
            else -> R.raw.video_kafein
        }
    }

    // Loop ticker posisi durasi video
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    currentSec = (vv.currentPosition / 1000).coerceAtLeast(0)
                    val dur = vv.duration / 1000
                    if (dur > 0) totalSec = dur
                }
            }
            delay(500L)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Kanvas Video Asli
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        videoViewRef?.let { vv ->
                            if (vv.isPlaying) {
                                vv.pause()
                                isPlaying = false
                            } else {
                                vv.start()
                                isPlaying = true
                            }
                        }
                    },
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val uri = Uri.parse("android.resource://${ctx.packageName}/$rawResId")
                        setVideoURI(uri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            totalSec = (mp.duration / 1000).coerceAtLeast(1)
                            start()
                            isPlaying = true
                        }
                        setOnErrorListener { _, _, _ ->
                            true
                        }
                        videoViewRef = this
                    }
                }
            )

            // Lapisan Kontrol Atas & Bawah
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Foto Profil Narasumber Vishal Dasani & Tombol Tutup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.photo_profile_narasumber),
                            contentDescription = "Vishal Dasani",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, DayPrimary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Vishal Dasani",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Certified Sleep Educator",
                                color = Color(0xFF93C5FD),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Indikator Pause di tengah
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000))
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶", color = Color.White, fontSize = 28.sp)
                    }
                } else {
                    Spacer(Modifier.height(1.dp))
                }

                // Bagian Bawah: Judul Kartu, Durasi, Progress Bar, & Tombol Selesai
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xDD0F172A))
                        .padding(16.dp)
                ) {
                    Text(
                        text = card.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = card.action,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "%02d:%02d".format(currentSec / 60, currentSec % 60),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "%02d:%02d".format(totalSec / 60, totalSec % 60),
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (currentSec.toFloat() / totalSec.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                        color = DayPrimary,
                        trackColor = Color(0x33FFFFFF)
                    )

                    Spacer(Modifier.height(12.dp))

                    Tactile3DButton(
                        text = "✓ Selesai Nonton & Terapkan",
                        onClick = onMarkDoneAndDismiss,
                        containerColor = DaySuccess,
                        shadowColor = DaySuccessDark
                    )
                }
            }
        }
    }
}

/**
 * Dialog Katalog Bab Edukasi CBT-I Luring Lengkap & Interaktif.
 * Pengguna dapat membuka setiap bab untuk melihat daftar kartu, membaca isi materi,
 * dan menonton video secara bebas kapan saja.
 */
@Composable
private fun CbtiCatalogDialog(
    contentRepo: ContentRepository,
    onDismiss: () -> Unit,
    onSelectCardToWatch: (DailyCard) -> Unit
) {
    var selectedChapterId by remember { mutableStateOf<Int?>(null) }
    var selectedDetailCard by remember { mutableStateOf<DailyCard?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(20.dp)
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Header Dialog
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedChapterId != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9))
                                        .clickable {
                                            selectedChapterId = null
                                            selectedDetailCard = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("←", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = if (selectedChapterId == null) "Katalog Bab Edukasi CBT-I 📚" else "Daftar Materi Bab $selectedChapterId",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .clickable(onClick = onDismiss),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Catatan Transparansi Kurikulum Edukasi Harian vs Akses Bebas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0FDF4))
                            .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "💡 Panduan Kurikulum CBT-I:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Beranda memfokuskan 1 materi per hari untuk pembiasaan bertahap tanpa distraksi. Namun, Anda memiliki kebebasan penuh menjelajahi seluruh bab dan video di katalog ini kapan saja.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF166534),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    if (selectedChapterId == null) {
                        // TAMPILAN DAFTAR BAB (4 BAB CBT-I)
                        ChapterClickableItem(
                            emoji = "☕",
                            title = "Bab 1: Regulasi Kafein & Nutrisi",
                            subtitle = "3 Materi • Batas konsumsi kafein, adenosin, hidrasi",
                            status = "Buka Bab ➔",
                            onClick = { selectedChapterId = 1 }
                        )
                        Spacer(Modifier.height(8.dp))
                        ChapterClickableItem(
                            emoji = "📱",
                            title = "Bab 2: Manajemen Cahaya & Gawai",
                            subtitle = "4 Materi • Supresi melatonin, aturan gawai malam",
                            status = "Buka Bab ➔",
                            onClick = { selectedChapterId = 2 }
                        )
                        Spacer(Modifier.height(8.dp))
                        ChapterClickableItem(
                            emoji = "🧠",
                            title = "Bab 3: Dekompresi Pikiran & Cemas",
                            subtitle = "3 Materi • Aturan 15 menit kasur, pernapasan 4-7-8",
                            status = "Buka Bab ➔",
                            onClick = { selectedChapterId = 3 }
                        )
                        Spacer(Modifier.height(8.dp))
                        ChapterClickableItem(
                            emoji = "🛏️",
                            title = "Bab 4: Penataan Kamar & Suhu",
                            subtitle = "4 Materi • Suhu ideal 18–20°C, kasur hanya tidur",
                            status = "Buka Bab ➔",
                            onClick = { selectedChapterId = 4 }
                        )
                    } else {
                        // TAMPILAN KARTU-KARTU DALAM BAB TERPILIH
                        val chapterCards = contentRepo.getCardsByChapter(selectedChapterId!!)
                        chapterCards.forEach { card ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selectedDetailCard?.id == card.id) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(1.dp, DaySurfaceBorder, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedDetailCard = if (selectedDetailCard?.id == card.id) null else card
                                    }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(card.emoji, fontSize = 20.sp)
                                            Spacer(Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Hari ke-${card.day}: ${card.title}",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = if (card.videoResName != null) {
                                                        if (selectedDetailCard?.id == card.id) "tutup detail" else "baca & tonton"
                                                    } else {
                                                        if (selectedDetailCard?.id == card.id) "tutup detail" else "baca materi sains"
                                                    },
                                                    fontSize = 10.sp,
                                                    color = DayPrimary
                                                )
                                            }
                                        }
                                        Text(if (selectedDetailCard?.id == card.id) "▲" else "▼", fontSize = 12.sp, color = DayTextSecondary)
                                    }

                                    // Detail materi jika diklik
                                    if (selectedDetailCard?.id == card.id) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = card.body,
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF334155),
                                            lineHeight = 16.sp
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = "Aksi: ${card.action}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B)
                                        )
                                        if (card.videoResName != null) {
                                            Spacer(Modifier.height(10.dp))
                                            Tactile3DButton(
                                                text = "▶ Tonton Video Kilat Materi Ini",
                                                onClick = {
                                                    onSelectCardToWatch(card)
                                                },
                                                containerColor = DayPrimary,
                                                shadowColor = DayPrimaryDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Profil Narasumber Pakar Vishal Dasani dengan Foto Profil Asli
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEFF6FF))
                            .border(1.5.dp, Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.photo_profile_narasumber),
                                contentDescription = "Foto Narasumber Vishal Dasani",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, DayPrimary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Narasumber: Vishal Dasani",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DayPrimary
                                )
                                Text(
                                    text = "Certified Sleep Educator & Sleep Coach",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E3A8A)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Materi video mikro 35 detik & kurikulum CBT-I disarikan langsung dari edukasi klinis dan praktis Vishal Dasani.",
                                    fontSize = 10.sp,
                                    color = DayTextSecondary,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Tactile3DButton(
                        text = "Tutup Katalog",
                        onClick = onDismiss,
                        containerColor = DayPrimary,
                        shadowColor = DayPrimaryDark
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterClickableItem(
    emoji: String,
    title: String,
    subtitle: String,
    status: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, DaySurfaceBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(subtitle, fontSize = 11.sp, color = DayTextSecondary)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(status, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
            }
        }
    }
}

@Composable
private fun SleepScheduleCard(schedule: SleepSchedule) {
    val timeFmt = DateTimeFormatter.ofPattern("HH.mm")
    TactileCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "JADWAL SIRKADIAN ANDA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DayPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Bangun Pagi", fontSize = 11.sp, color = DayTextSecondary)
                    Text(schedule.wakeTime.format(timeFmt), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Batas Kafein", fontSize = 11.sp, color = DayTextSecondary)
                    Text(schedule.caffeineCurfew.format(timeFmt), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
                Column {
                    Text("Pengingat Anchor", fontSize = 11.sp, color = DayTextSecondary)
                    Text(schedule.anchorTime.format(timeFmt), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                }
                Column {
                    Text("Tidur Ideal", fontSize = 11.sp, color = DayTextSecondary)
                    Text(schedule.bedtime.format(timeFmt), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NotificationPermissionBanner(
    onOpenSettingsClick: () -> Unit
) {
    TactileCard(
        backgroundColor = Color(0xFFFEF2F2),
        borderColor = Color(0xFFFECACA),
        shadowColor = Color(0xFFFCA5A5)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️", fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Izin Notifikasi Nonaktif",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                )
                Text(
                    text = "Aktifkan agar pengingat Bedtime Anchor berdering.",
                    fontSize = 11.sp,
                    color = Color(0xFFB91C1C)
                )
            }
            TextButton(onClick = onOpenSettingsClick) {
                Text("Aktifkan", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), fontSize = 12.sp)
            }
        }
    }
}
