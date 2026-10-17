package id.jaljayo.app.ui.settings

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.jaljayo.core.AudioPreset
import id.jaljayo.core.SleepSchedule
import id.jaljayo.core.calculateSleepSchedule
import java.time.LocalTime

/**
 * Layar Pengaturan — mengubah jam bangun & preset audio default.
 *
 * @param currentWakeHour    Jam bangun tersimpan saat ini.
 * @param currentWakeMinute  Menit bangun tersimpan saat ini.
 * @param currentPreset      Preset audio tersimpan saat ini.
 * @param onSave             Dipanggil saat pengguna menyimpan perubahan.
 * @param onBack             Dipanggil saat tombol Kembali ditekan.
 */
@Composable
fun SettingsScreen(
    currentWakeHour: Int,
    currentWakeMinute: Int,
    currentPreset: AudioPreset,
    totalSessionsCount: Int = 0,
    lastSessionDuration: Int = 0,
    onSave: (wakeHour: Int, wakeMinute: Int, preset: AudioPreset) -> Unit,
    onBack: () -> Unit
) {
    var wakeHour by remember { mutableIntStateOf(currentWakeHour) }
    var wakeMinute by remember { mutableIntStateOf(currentWakeMinute) }
    var selectedPreset by remember { mutableStateOf(currentPreset) }

    val schedule = remember(wakeHour, wakeMinute) {
        calculateSleepSchedule(LocalTime.of(wakeHour, wakeMinute))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text(text = "←", fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Pengaturan",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            // === Seksi Jam Bangun ===
            SectionHeader(title = "JAM BANGUN PAGI")
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "%02d:%02d".format(wakeHour, wakeMinute),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "JAM",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(4, 5, 6, 7, 8, 9).forEach { hour ->
                    val selected = wakeHour == hour
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { wakeHour = hour }
                    ) {
                        Text(
                            text = "%02d".format(hour),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MENIT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(0, 15, 30, 45).forEach { minute ->
                    val selected = wakeMinute == minute
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { wakeMinute = minute }
                    ) {
                        Text(
                            text = ":%02d".format(minute),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preview jadwal sirkadian hasil perubahan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SchedulePreviewItem(label = "Pengingat", time = schedule.anchorTime.toString())
                    SchedulePreviewItem(label = "Jam Tidur", time = schedule.bedtime.toString())
                    SchedulePreviewItem(label = "Jam Bangun", time = schedule.wakeTime.toString())
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(20.dp))

            // === Seksi Preset Audio ===
            SectionHeader(title = "AUDIO RELAKSASI DEFAULT")
            Spacer(modifier = Modifier.height(12.dp))

            AudioPreset.entries.forEach { preset ->
                val selected = selectedPreset == preset
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable { selectedPreset = preset },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (selected) 3.dp else 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = presetEmoji(preset),
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.displayName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = presetDesc(preset),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (selected) {
                            Text(
                                text = "✓",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // === Seksi Riwayat Sesi Relaksasi (UCS-01) ===
            SectionHeader(title = "RIWAYAT SESI TIDUR LOKAL")
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📊", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (totalSessionsCount > 0)
                                "$totalSessionsCount Sesi Relaksasi Selesai"
                            else
                                "Belum Ada Sesi Tersimpan",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (totalSessionsCount > 0 && lastSessionDuration > 0)
                                "Sesi terakhir berdurasi $lastSessionDuration menit (100% data offline)"
                            else
                                "Riwayat sesi akan dicatat otomatis setelah kamu memutar audio malam",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onSave(wakeHour, wakeMinute, selectedPreset) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Simpan Pengaturan",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SchedulePreviewItem(label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun presetEmoji(preset: AudioPreset) = when (preset) {
    AudioPreset.THETA_6HZ -> "🧠"
    AudioPreset.BROWN_NOISE -> "🔊"
    AudioPreset.RAIN_SOUND -> "🌧️"
    AudioPreset.OCEAN_WAVES -> "🌊"
}

private fun presetDesc(preset: AudioPreset) = when (preset) {
    AudioPreset.THETA_6HZ -> "Ideal untuk overthinking & racing thoughts"
    AudioPreset.BROWN_NOISE -> "Ideal untuk lingkungan bising & kosan ramai"
    AudioPreset.RAIN_SOUND -> "Suara hujan yang menenangkan"
    AudioPreset.OCEAN_WAVES -> "Suara ombak untuk relaksasi dalam"
}
