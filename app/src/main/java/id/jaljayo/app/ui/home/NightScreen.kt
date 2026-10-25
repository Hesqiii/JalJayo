package id.jaljayo.app.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.jaljayo.app.ui.components.Tactile3DButton
import id.jaljayo.app.ui.components.TactileCard
import id.jaljayo.app.ui.components.TactileChip
import id.jaljayo.app.ui.theme.NightAccent
import id.jaljayo.app.ui.theme.NightBackground
import id.jaljayo.app.ui.theme.NightPrimary
import id.jaljayo.app.ui.theme.NightPrimaryDark
import id.jaljayo.app.ui.theme.NightSurface
import id.jaljayo.app.ui.theme.NightSurfaceBorder
import id.jaljayo.app.ui.theme.NightTextSecondary
import id.jaljayo.core.AudioPreset

/**
 * Layar Pemutar Relaksasi Malam (NightScreen) — Zero-Visual Mode (20:00 - 05:59).
 *
 * Menerapkan kaidah fotobiologi sirkadian (Chang et al., 2015):
 * - Layar pekat OLED Black (#030712 / #000000) tanpa stimulasi visual menyilaukan.
 * - Tombol play melingkar besar (112dp) yang mudah disentuh di kamar gelap.
 * - Pemilihan frekuensi gelombang tidur & timer otomatis dengan kurva peredupan kubik.
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

    // Animasi denyut halus pada piringan pemutar saat audio aktif
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NightBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // === Night Lockout Info Banner ===
        TactileCard(
            backgroundColor = NightSurface,
            borderColor = NightSurfaceBorder,
            shadowColor = Color(0xFF02040A)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔒", fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Mode Malam Aktif (Night Lockout)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1F5F9)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Akses video diistirahatkan untuk menjaga hormon tidurmu.",
                        fontSize = 11.sp,
                        color = NightTextSecondary
                    )
                }
            }
        }

        // Peringatan jika Volume HP 0% (UCS-01.Exc-1)
        if (isDeviceVolumeMuted) {
            Spacer(Modifier.height(12.dp))
            TactileCard(
                backgroundColor = Color(0xFF451A03),
                borderColor = Color(0xFFB45309),
                shadowColor = Color(0xFF78350F)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Volume Ponsel Hening (0%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFEF3C7)
                        )
                        Text(
                            text = "Naikkan volume agar audio relaksasi terdengar.",
                            fontSize = 11.sp,
                            color = Color(0xFFFDE68A)
                        )
                    }
                    TextButton(onClick = onRaiseVolumeClick) {
                        Text("Naikkan", fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24), fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // === Piringan Pemutar Audio Besar (112dp Tactile Disc) ===
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            // Cincin gelombang luar
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .size(165.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(1.5.dp, NightPrimary.copy(alpha = 0.35f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(NightPrimary.copy(alpha = 0.12f))
                )
            }

            // Tombol Inti Piringan Play/Pause Taktil 112dp
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(3.dp, if (isPlaying) NightPrimary else Color(0xFF334155), CircleShape)
                    .clickable(onClick = onPlayPauseClick),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isPlaying) "⏸" else "▶",
                        fontSize = 38.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = selectedPreset.displayName,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFF8FAFC)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (isPlaying) "Audio sedang berputar di latar belakang" else "Ketuk untuk memulai relaksasi",
            fontSize = 12.sp,
            color = NightTextSecondary
        )

        // Indikator Hitung Mundur Timer Sesi
        if (isTimerActive && timerRemainingMs > 0) {
            val totalSeconds = (timerRemainingMs / 1000).toInt()
            val formattedTime = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, NightPrimary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏳ ", fontSize = 12.sp)
                    Text(
                        text = "$formattedTime tersisa (Fade-out di 02:00)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF93C5FD)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // === Pemilihan Preset Suara ===
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PILIH FREKUENSI SUARA",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NightPrimary,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(
                    preset = AudioPreset.THETA_6HZ,
                    isSelected = selectedPreset == AudioPreset.THETA_6HZ,
                    onClick = { onPresetSelect(AudioPreset.THETA_6HZ) },
                    modifier = Modifier.weight(1f)
                )
                PresetChip(
                    preset = AudioPreset.BROWN_NOISE,
                    isSelected = selectedPreset == AudioPreset.BROWN_NOISE,
                    onClick = { onPresetSelect(AudioPreset.BROWN_NOISE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(
                    preset = AudioPreset.RAIN_SOUND,
                    isSelected = selectedPreset == AudioPreset.RAIN_SOUND,
                    onClick = { onPresetSelect(AudioPreset.RAIN_SOUND) },
                    modifier = Modifier.weight(1f)
                )
                PresetChip(
                    preset = AudioPreset.OCEAN_WAVES,
                    isSelected = selectedPreset == AudioPreset.OCEAN_WAVES,
                    onClick = { onPresetSelect(AudioPreset.OCEAN_WAVES) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // === Pemilihan Durasi Timer ===
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "TIMER OTOMATIS (FADE-OUT KUBIK)",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NightPrimary,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimerChip(
                    minutes = 15,
                    isSelected = selectedDuration == 15,
                    onClick = {
                        onDurationSelect(15)
                        if (isPlaying) onStartTimerClick(15)
                    },
                    modifier = Modifier.weight(1f)
                )
                TimerChip(
                    minutes = 30,
                    isSelected = selectedDuration == 30,
                    onClick = {
                        onDurationSelect(30)
                        if (isPlaying) onStartTimerClick(30)
                    },
                    modifier = Modifier.weight(1f)
                )
                TimerChip(
                    minutes = 45,
                    isSelected = selectedDuration == 45,
                    onClick = {
                        onDurationSelect(45)
                        if (isPlaying) onStartTimerClick(45)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // === Tombol Taktil Putar & Kunci Layar ===
        Tactile3DButton(
            text = if (isPlaying) "⏸ Jeda Audio Relaksasi" else "▶ Putar & Kunci Layar",
            onClick = onPlayPauseClick,
            containerColor = NightPrimary,
            shadowColor = NightPrimaryDark
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Layar aman dipadamkan • Audio tetap berputar di latar belakang",
            fontSize = 11.sp,
            color = NightTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun PresetChip(
    preset: AudioPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF1E3A8A) else NightSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NightPrimary else NightSurfaceBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = preset.displayName,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else NightTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimerChip(
    minutes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF1E3A8A) else NightSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NightPrimary else NightSurfaceBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$minutes Menit",
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else NightTextSecondary
        )
    }
}
