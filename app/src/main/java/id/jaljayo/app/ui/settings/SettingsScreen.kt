package id.jaljayo.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.jaljayo.app.ui.components.Tactile3DButton
import id.jaljayo.app.ui.components.TactileCard
import id.jaljayo.app.ui.theme.DayBackground
import id.jaljayo.app.ui.theme.DayPrimary
import id.jaljayo.app.ui.theme.DayPrimaryDark
import id.jaljayo.app.ui.theme.DaySuccess
import id.jaljayo.app.ui.theme.DaySurfaceBorder
import id.jaljayo.app.ui.theme.DayTextSecondary
import id.jaljayo.core.AudioPreset
import id.jaljayo.core.calculateSleepSchedule
import java.time.LocalTime

/**
 * Layar Pengaturan & Preferensi Mandiri (SettingsScreen).
 * Tampilan Taktil Bergaya Duolingo 2026 & 100% DataStore Offline.
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
            .background(DayBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
    ) {
        // === Top Bar ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text("←", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Pengaturan & Preferensi",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // === Bagian 1: Jadwal Sirkadian ===
            Text(
                text = "PROFIL & JADWAL SIRKADIAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DayPrimary,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            TactileCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Target Jam Bangun Pagi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Jam aktif memulai hari untuk menghitung jadwal tidur 5 siklus.",
                        fontSize = 12.sp,
                        color = DayTextSecondary
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "%02d:%02d".format(wakeHour, wakeMinute),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DayPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    // Pilihan Jam
                    Text("Pilih Jam:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DayTextSecondary)
                    Spacer(Modifier.height(6.dp))
                    listOf(4, 5, 6, 7, 8, 9).chunked(3).forEach { hourRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            hourRow.forEach { hour ->
                                TimeChoiceDuolingo(
                                    label = "%02d:00".format(hour),
                                    selected = wakeHour == hour,
                                    modifier = Modifier.weight(1f),
                                    onClick = { wakeHour = hour }
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }

                    Spacer(Modifier.height(10.dp))

                    // Pilihan Menit
                    Text("Pilih Menit:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DayTextSecondary)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 15, 30, 45).forEach { minute ->
                            TimeChoiceDuolingo(
                                label = ":%02d".format(minute),
                                selected = wakeMinute == minute,
                                modifier = Modifier.weight(1f),
                                onClick = { wakeMinute = minute }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = DaySurfaceBorder)
                    Spacer(Modifier.height(12.dp))

                    // Pratinjau Jadwal Sirkadian Terhitung
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Pengingat Anchor", fontSize = 11.sp, color = DayTextSecondary)
                            Text(schedule.anchorTime.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                        }
                        Column {
                            Text("Tidur Ideal", fontSize = 11.sp, color = DayTextSecondary)
                            Text(schedule.bedtime.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Bangun Pagi", fontSize = 11.sp, color = DayTextSecondary)
                            Text("%02d:%02d".format(wakeHour, wakeMinute), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // === Bagian 2: Preset Audio Default ===
            Text(
                text = "PREFERENSI AUDIO DEFAULT",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DayPrimary,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            TactileCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    AudioPreset.entries.forEach { preset ->
                        val selected = selectedPreset == preset
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) DayPrimary else DaySurfaceBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPreset = preset }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) DayPrimary else MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = presetDescription(preset),
                                        fontSize = 11.sp,
                                        color = DayTextSecondary
                                    )
                                }
                                if (selected) {
                                    Text("✓", color = DayPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // === Bagian 3: Riwayat Sesi & Privasi Lokal ===
            TactileCard(
                backgroundColor = Color(0xFFF0FDF4),
                borderColor = Color(0xFFBBF7D0),
                shadowColor = Color(0xFF86EFAC)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔒", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "100% Offline & Privasi Terjamin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (totalSessionsCount > 0)
                            "$totalSessionsCount sesi relaksasi diselesaikan secara lokal di perangkat ini."
                        else
                            "Semua setelan dan sesi tidur tersimpan lokal di HP tanpa koneksi internet.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF166534)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // === Bagian 4: Panduan Pemakaian Aplikasi (Logo "ℹ️" - Versi 2) ===
            var showHelpGuide by remember { mutableStateOf(false) }

            TactileCard(
                backgroundColor = Color(0xFFEFF6FF),
                borderColor = Color(0xFFBFDBFE),
                shadowColor = Color(0xFF93C5FD)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHelpGuide = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ℹ️", fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Panduan Pemakaian Aplikasi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DayPrimary
                            )
                            Text(
                                text = "Pelajari fitur sirkadian, video, & streak",
                                fontSize = 11.sp,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }
                    Text("➔", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                }
            }

            if (showHelpGuide) {
                id.jaljayo.app.ui.components.AppHelpGuideDialog(
                    onDismiss = { showHelpGuide = false }
                )
            }

            Spacer(Modifier.height(24.dp))

            // === Tombol Simpan Taktil Duolingo ===
            Tactile3DButton(
                text = "Simpan Pengaturan 💾",
                onClick = { onSave(wakeHour, wakeMinute, selectedPreset) },
                containerColor = DayPrimary,
                shadowColor = DayPrimaryDark
            )

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun TimeChoiceDuolingo(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) DayPrimary else Color(0xFFF8FAFC))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) DayPrimary else DaySurfaceBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun presetDescription(preset: AudioPreset): String = when (preset) {
    AudioPreset.THETA_6HZ -> "Penenang gelombang otak untuk meredakan overthinking"
    AudioPreset.BROWN_NOISE -> "Derau frekuensi rendah untuk sound masking kamar bising"
    AudioPreset.RAIN_SOUND -> "Suara hujan alami untuk menemani waktu relaksasi"
    AudioPreset.OCEAN_WAVES -> "Deburan ombak berirama teratur untuk meredakan cemas"
}
