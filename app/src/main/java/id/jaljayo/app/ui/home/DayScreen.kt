package id.jaljayo.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.jaljayo.app.data.DailyCard
import id.jaljayo.app.ui.theme.JalJayoSpace
import id.jaljayo.core.SleepSchedule
import kotlinx.coroutines.delay

/**
 * Layar Mode Siang (DayScreen) — Pilar Siang & Transisi JalJayo (06:00 - 19:59).
 *
 * Menampilkan:
 * - Sapaan daylight & ringkasan ritme sirkadian hari ini.
 * - Kartu edukasi kebiasaan harian dari ContentRepository (hari ke-N sejak instalasi).
 * - Status baca kartu harian.
 * - Tombol akses ke pengaturan dan pemutar relaksasi.
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
    onOpenNotificationSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = JalJayoSpace.large)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(JalJayoSpace.large))

        // === Header ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "JalJayo",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Satu langkah kecil\nuntuk malam tenang.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            OutlinedButton(
                onClick = onOpenSettingsClick,
                modifier = Modifier.height(JalJayoSpace.touchTarget),
                shape = CircleShape
            ) {
                Text(
                    text = "Pengaturan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(JalJayoSpace.medium))

        // Banner peringatan jika izin notifikasi nonaktif (Android 13+)
        if (!isNotificationPermissionGranted) {
            NotificationPermissionBanner(
                onOpenSettingsClick = onOpenNotificationSettings
            )
            Spacer(modifier = Modifier.height(JalJayoSpace.medium))
        }

        todayCard?.let { card ->
            Text(
                text = "Hari ${card.day.coerceAtMost(14)} dari 14",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(JalJayoSpace.medium))

        // === Kartu Edukasi Harian (dari JSON) ===
        if (todayCard != null) {
            DailyCardSection(
                card = todayCard,
                isCompleted = isCardCompleted,
                onMarkDone = onMarkCardDone
            )
        } else {
            // Fallback kalau JSON gagal terbaca
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(JalJayoSpace.medium)) {
                    Text(
                        text = "Konten kartu sedang dimuat...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(JalJayoSpace.medium))
        SleepScheduleCard(schedule = sleepSchedule)

        Spacer(modifier = Modifier.height(JalJayoSpace.large))

        // === Tombol Buka Pemutar Relaksasi ===
        OutlinedButton(
            onClick = onOpenPlayerClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(JalJayoSpace.touchTarget),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Buka pemutar relaksasi",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(JalJayoSpace.xLarge))
    }
}
@Composable
private fun SleepScheduleCard(schedule: SleepSchedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(JalJayoSpace.medium)) {
            Text(
                text = "Jadwal tidurmu",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(JalJayoSpace.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScheduleItem(title = "Bangun", time = schedule.wakeTime.toString())
                ScheduleItem(title = "Pengingat", time = schedule.anchorTime.toString())
                ScheduleItem(title = "Tidur", time = schedule.bedtime.toString())
            }

            Spacer(modifier = Modifier.height(JalJayoSpace.small))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(JalJayoSpace.small))
            Text(
                text = "Mode malam dimulai pukul 20.00",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailyCardSection(
    card: DailyCard,
    isCompleted: Boolean,
    onMarkDone: () -> Unit
) {
    var showVideoPlayer by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(JalJayoSpace.medium)) {
            // Label hari ke-N
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KARTU HARI INI",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isCompleted) {
                    Text(
                        text = "Sudah dibaca",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Belum dibaca",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = card.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(JalJayoSpace.small))

            // Body teks
            Text(
                text = card.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(14.dp))

            // === Kontainer Video Vertikal 9:16 (FR-02.1 & UCS-02) ===
            VerticalVideoThumbnailCard(
                card = card,
                onClick = { showVideoPlayer = true }
            )

            Spacer(modifier = Modifier.height(JalJayoSpace.medium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(JalJayoSpace.medium))

            // Kotak aksi konkret
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(modifier = Modifier.padding(JalJayoSpace.medium)) {
                    Column {
                        Text(
                            text = "Hari ini coba:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(JalJayoSpace.xSmall))
                        Text(
                            text = card.action,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Tampilkan aksi baca hanya sebelum kartu ditandai.
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(JalJayoSpace.medium))
                Button(
                    onClick = onMarkDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(JalJayoSpace.touchTarget),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Tandai sudah dibaca",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (showVideoPlayer) {
        VerticalVideoModalPlayer(
            card = card,
            onDismiss = { showVideoPlayer = false },
            onMarkDoneAndDismiss = {
                onMarkDone()
                showVideoPlayer = false
            }
        )
    }
}

/**
 * Thumbnail pratinjau kartu video vertikal edukasi tidur (FR-02.1 & UCS-02).
 */
@Composable
private fun VerticalVideoThumbnailCard(
    card: DailyCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Putar video edukasi: ${card.title}" },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(JalJayoSpace.small),
            horizontalArrangement = Arrangement.spacedBy(JalJayoSpace.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(104.dp)
                    .aspectRatio(9f / 16f)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.emoji,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(JalJayoSpace.xSmall)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "VIDEO • 0:35",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(JalJayoSpace.small))
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(JalJayoSpace.small))
                Text(
                    text = "Ketuk untuk menonton",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Pemutar video edukasi vertikal interaktif ramah gawai (UCS-02).
 */
@Composable
private fun VerticalVideoModalPlayer(
    card: DailyCard,
    onDismiss: () -> Unit,
    onMarkDoneAndDismiss: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    val totalSeconds = 35

    LaunchedEffect(isPlaying) {
        while (isPlaying && elapsedSeconds < totalSeconds) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090D16))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Player
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HARI KE-${card.day} • VIDEO EDUKASI",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text(text = "✕", fontSize = 20.sp, color = Color.White)
                    }
                }

                // Viewport Vertikal (9:16 Visual Card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                    Color(0xFF020617)
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = card.emoji, fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = card.action,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (elapsedSeconds >= totalSeconds) "Video selesai" else "Memutar video edukasi",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar & Timer
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                        Text(
                            text = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { elapsedSeconds.toFloat() / totalSeconds.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kontrol Tombol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (elapsedSeconds >= totalSeconds) {
                                elapsedSeconds = 0
                                isPlaying = true
                            } else {
                                isPlaying = !isPlaying
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (elapsedSeconds >= totalSeconds) "↺ Putar Ulang"
                            else if (isPlaying) "⏸ Jeda" else "▶ Lanjut",
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onMarkDoneAndDismiss,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Tandai sudah dibaca")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleItem(title: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun NotificationPermissionBanner(
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalJayoSpace.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Izin notifikasi nonaktif",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Aktifkan notifikasi agar pengingat jadwal tidur bisa muncul.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(JalJayoSpace.small))
            Button(
                onClick = onOpenSettingsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Aktifkan",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
