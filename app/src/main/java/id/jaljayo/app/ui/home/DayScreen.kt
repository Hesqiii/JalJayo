package id.jaljayo.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.jaljayo.app.data.DailyCard
import id.jaljayo.core.SleepSchedule
import kotlinx.coroutines.delay

/**
 * Layar Mode Siang (DayScreen) — Pilar Siang & Transisi JalJayo (06:00 - 19:59).
 *
 * Menampilkan:
 * - Sapaan daylight & ringkasan ritme sirkadian hari ini.
 * - Kartu edukasi kebiasaan harian dari ContentRepository (hari ke-N sejak instalasi).
 * - Indikator status "Sudah dibaca" dan tombol tandai selesai.
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
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // === Header ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "☀️ JalJayo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ritme Siang Hari",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            TextButton(onClick = onOpenSettingsClick) {
                Text(
                    text = "⚙️ Atur",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Bangun kebiasaan tidur teratur mulai dari aktivitas siang harimu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // === Kartu Jadwal Sirkadian ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "JADWAL SIRKADIAN HARI INI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScheduleItem(icon = "⏰", title = "Jam Bangun", time = sleepSchedule.wakeTime.toString())
                    ScheduleItem(icon = "🔔", title = "Pengingat Tidur", time = sleepSchedule.anchorTime.toString())
                    ScheduleItem(icon = "🛏️", title = "Jam Tidur", time = sleepSchedule.bedtime.toString())
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔒", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Night Lockout aktif otomatis pukul 20:00",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Konten kartu sedang dimuat...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === Tombol Buka Pemutar Relaksasi ===
        OutlinedButton(
            onClick = onOpenPlayerClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "🎧 Buka Pemutar Audio Relaksasi",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Label hari ke-N
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HARI KE-${card.day} • AKSI HARIAN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                if (isCompleted) {
                    Text(
                        text = "✅ Selesai",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "1 menit baca",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Emoji + Judul
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = card.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body teks
            Text(
                text = card.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // === Kontainer Video Vertikal 9:16 (FR-02.1 & UCS-02) ===
            VerticalVideoThumbnailCard(
                card = card,
                onClick = { showVideoPlayer = true }
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Kotak aksi konkret
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text(text = "💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "AKSI HARI INI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = card.action,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Tombol tandai selesai (hanya jika belum selesai)
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onMarkDone,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "✓ Tandai Sudah Dibaca",
                        style = MaterialTheme.typography.titleSmall
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
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Badge Atas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🎬 VIDEO VERTIKAL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "⏱️ 0:35",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Tombol Play Tengah
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 3.dp)
                )
            }

            // Judul Bawah
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = "Tonton Edukasi: ${card.title}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Ketuk untuk memutar video mikro sains tidur 35 detik",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
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
                                text = if (elapsedSeconds >= totalSeconds) "✅ Video Selesai" else "🎬 Memutar Video Sirkadian...",
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
                        Text(text = "✓ Tandai Selesai")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleItem(icon: String, title: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
