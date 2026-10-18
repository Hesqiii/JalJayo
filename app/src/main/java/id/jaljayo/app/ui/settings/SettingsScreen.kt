package id.jaljayo.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import id.jaljayo.app.ui.theme.JalJayoSpace
import id.jaljayo.core.AudioPreset
import id.jaljayo.core.calculateSleepSchedule
import java.time.LocalTime

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
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = JalJayoSpace.settingsTopBarHeight)
                .padding(horizontal = JalJayoSpace.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription = "Kembali"
                }
            ) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(JalJayoSpace.xSmall))
            Text(
                text = "Pengaturan",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JalJayoSpace.large)
        ) {
            Spacer(Modifier.height(JalJayoSpace.small))
            Text(
                text = "Jam bangun",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(JalJayoSpace.xSmall))
            Text(
                text = "Pilih waktu kamu ingin memulai hari.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(JalJayoSpace.medium))
            Text(
                text = "%02d:%02d".format(wakeHour, wakeMinute),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(JalJayoSpace.medium))
            Text("Jam", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(JalJayoSpace.xSmall))
            listOf(4, 5, 6, 7, 8, 9).chunked(3).forEach { hourRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(JalJayoSpace.small)
                ) {
                    hourRow.forEach { hour ->
                        TimeChoice(
                            label = "%02d".format(hour),
                            selected = wakeHour == hour,
                            modifier = Modifier.weight(1f),
                            onClick = { wakeHour = hour }
                        )
                    }
                }
                Spacer(Modifier.height(JalJayoSpace.small))
            }

            Spacer(Modifier.height(JalJayoSpace.small))
            Text("Menit", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(JalJayoSpace.xSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(JalJayoSpace.small)
            ) {
                listOf(0, 15, 30, 45).forEach { minute ->
                    TimeChoice(
                        label = ":%02d".format(minute),
                        selected = wakeMinute == minute,
                        modifier = Modifier.weight(1f),
                        onClick = { wakeMinute = minute }
                    )
                }
            }

            Spacer(Modifier.height(JalJayoSpace.medium))
            SchedulePreview(
                reminderTime = schedule.anchorTime.toString(),
                bedtime = schedule.bedtime.toString(),
                wakeTime = schedule.wakeTime.toString()
            )

            Spacer(Modifier.height(JalJayoSpace.xLarge))
            Text(
                text = "Audio relaksasi default",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(JalJayoSpace.xSmall))
            Text(
                text = "Suara yang dipilih akan digunakan saat kamu membuka pemutar malam.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(JalJayoSpace.small))

            AudioPreset.entries.forEach { preset ->
                val selected = selectedPreset == preset
                val shape = MaterialTheme.shapes.medium
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = JalJayoSpace.small)
                        .border(
                            width = if (selected) JalJayoSpace.selectedOutlineWidth else JalJayoSpace.outlineWidth,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = shape
                        )
                        .selectable(
                            selected = selected,
                            role = Role.RadioButton,
                            onClick = { selectedPreset = preset }
                        ),
                    shape = shape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = JalJayoSpace.xSmall)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = JalJayoSpace.settingsChoiceHeight)
                            .padding(horizontal = JalJayoSpace.medium, vertical = JalJayoSpace.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(JalJayoSpace.xSmall))
                            Text(
                                text = presetDescription(preset),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (selected) {
                            Text(
                                text = "Terpilih",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = JalJayoSpace.small)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(JalJayoSpace.large))
            Text(
                text = "Riwayat sesi lokal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(JalJayoSpace.small))
            SessionHistory(
                totalSessionsCount = totalSessionsCount,
                lastSessionDuration = lastSessionDuration
            )

            Spacer(Modifier.height(JalJayoSpace.large))
            Button(
                onClick = { onSave(wakeHour, wakeMinute, selectedPreset) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = JalJayoSpace.settingsChoiceHeight),
                shape = CircleShape
            ) {
                Text("Simpan Pengaturan", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.height(JalJayoSpace.large))
        }
    }
}

@Composable
private fun TimeChoice(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = MaterialTheme.shapes.small
    Box(
        modifier = modifier
            .heightIn(min = JalJayoSpace.settingsChoiceHeight)
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = if (selected) JalJayoSpace.selectedOutlineWidth else JalJayoSpace.outlineWidth,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = shape
            )
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(JalJayoSpace.xSmall)
        )
    }
}

@Composable
private fun SchedulePreview(reminderTime: String, bedtime: String, wakeTime: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = JalJayoSpace.xSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalJayoSpace.medium),
            horizontalArrangement = Arrangement.spacedBy(JalJayoSpace.xSmall)
        ) {
            SchedulePreviewItem("Pengingat", reminderTime, Modifier.weight(1f))
            SchedulePreviewItem("Jam tidur", bedtime, Modifier.weight(1f))
            SchedulePreviewItem("Bangun", wakeTime, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SchedulePreviewItem(label: String, time: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SessionHistory(totalSessionsCount: Int, lastSessionDuration: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = JalJayoSpace.xSmall)
    ) {
        Column(modifier = Modifier.padding(JalJayoSpace.medium)) {
            Text(
                text = if (totalSessionsCount > 0) "$totalSessionsCount sesi tersimpan"
                else "Belum ada sesi",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(JalJayoSpace.xSmall))
            Text(
                text = if (totalSessionsCount > 0 && lastSessionDuration > 0)
                    "Sesi terakhir berlangsung $lastSessionDuration menit."
                else
                    "Sesi yang kamu putar akan muncul di sini.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun presetDescription(preset: AudioPreset): String = when (preset) {
    AudioPreset.THETA_6HZ -> "Untuk saat pikiran terasa ramai"
    AudioPreset.BROWN_NOISE -> "Untuk membantu meredam suara sekitar"
    AudioPreset.RAIN_SOUND -> "Suara hujan untuk menemani waktu istirahat"
    AudioPreset.OCEAN_WAVES -> "Suara ombak untuk menemani waktu istirahat"
}
