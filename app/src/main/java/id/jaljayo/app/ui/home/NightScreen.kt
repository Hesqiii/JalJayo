package id.jaljayo.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.jaljayo.core.AudioPreset

/**
 * Layar Mode Malam (NightScreen) — Inti dari pilar Malam JalJayo (BR-01 & BR-02).
 *
 * Prinsip Desain:
 * - Layar gelap (OLED black/midnight) tanpa elemen visual yang berkedip atau menyala terang.
 * - One-Tap Wind-Down: Tombol Play melingkar besar (<= 2 ketukan dari buka aplikasi).
 * - Kontrol Timer 15, 30, 45 menit dengan indikator fade-out 2 menit terakhir.
 * - Peringatan volume jika perangkat dalam keadaan bisu (0%).
 */
@Composable
fun NightScreen(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    selectedPreset: AudioPreset,
    onPresetSelect: (AudioPreset) -> Unit,
    selectedDuration: Int,
    onDurationSelect: (Int) -> Unit,
    isTimerActive: Boolean,
    timerRemainingMs: Long,
    onStartTimerClick: (Int) -> Unit,
    onCancelTimerClick: () -> Unit,
    isDeviceVolumeMuted: Boolean,
    onRaiseVolumeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // === 1. Header Mode Malam ===
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 28.dp)
        ) {
            Text(
                text = "🌙 JalJayo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Mode Malam Aktif",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Konten video diistirahatkan untuk menjaga hormon tidurmu.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // === 2. Peringatan Volume Hening (Jika Volume 0%) ===
        AnimatedVisibility(
            visible = isDeviceVolumeMuted,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { onRaiseVolumeClick() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D1F0E)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔇", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Volume ponsel Anda dalam keadaan hening, harap naikkan volume.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFDE68A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ketuk untuk otomatis mengatur volume ke 50%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFDE68A).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // === 3. Tombol Utama: One-Tap Wind-Down Player ===
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            // Tombol bulat besar ramah sentuhan di kegelapan
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = 2.dp,
                        color = if (isPlaying) MaterialTheme.colorScheme.primary else Color(0xFF2A364F),
                        shape = CircleShape
                    )
                    .clickable { onPlayPauseClick() }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isPlaying) "⏸" else "▶",
                        fontSize = 38.sp,
                        color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPlaying) "Jeda" else "Putar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info preset yang sedang dipilih
            Text(
                text = selectedPreset.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Status timer jika sedang aktif
            if (isTimerActive && timerRemainingMs > 0) {
                val totalSeconds = (timerRemainingMs / 1000).toInt()
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                val formattedTime = "%02d:%02d".format(minutes, seconds)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sisa Waktu: $formattedTime",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )

                if (timerRemainingMs <= 120_000L) {
                    Text(
                        text = "Memudar perlahan (fade-out)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // === 4. Pilihan Preset Audio & Durasi Timer ===
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            // Pemilih Preset Audio
            Text(
                text = "PILIH SUARA RELAKSASI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AudioPreset.values().forEach { preset ->
                    val isSelected = preset == selectedPreset
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onPresetSelect(preset) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = when (preset) {
                                AudioPreset.THETA_6HZ -> "Theta 6Hz"
                                AudioPreset.BROWN_NOISE -> "Brown Noise"
                                AudioPreset.RAIN_SOUND -> "Hujan"
                                AudioPreset.OCEAN_WAVES -> "Ombak"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pemilih Timer Tidur
            Text(
                text = "TIMER TIDUR (FADE-OUT 2 MENIT)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(15, 30, 45).forEach { minutes ->
                    val isSelected = minutes == selectedDuration
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onDurationSelect(minutes)
                                if (isPlaying) {
                                    onStartTimerClick(minutes)
                                }
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "$minutes Menit",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tombol aksi timer
            if (isTimerActive) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onCancelTimerClick) {
                    Text(
                        text = "Hentikan Timer",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
