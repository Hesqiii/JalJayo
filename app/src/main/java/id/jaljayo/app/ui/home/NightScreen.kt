package id.jaljayo.app.ui.home

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.jaljayo.app.ui.theme.JalJayoSpace
import id.jaljayo.core.AudioPreset

/** Quiet night playback screen. Playback and timer actions remain owned by the existing service. */
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = JalJayoSpace.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NightSky(
            modifier = Modifier
                .fillMaxWidth()
                .height(JalJayoSpace.nightSkyHeight)
        )

        Text(
            text = "Mode Malam Aktif",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(JalJayoSpace.xSmall))
        Text(
            text = "Konten video diistirahatkan untuk menjaga hormon tidurmu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (isDeviceVolumeMuted) {
            Spacer(Modifier.height(JalJayoSpace.medium))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        horizontal = JalJayoSpace.medium,
                        vertical = JalJayoSpace.small
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Volume ponsel Anda dalam keadaan hening, harap naikkan volume",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                TextButton(
                    onClick = onRaiseVolumeClick,
                    modifier = Modifier.heightIn(min = JalJayoSpace.touchTarget)
                ) {
                    Text("Naikkan volume")
                }
            }
        }

        Spacer(Modifier.height(JalJayoSpace.medium))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = JalJayoSpace.small)
        ) {
            PlayButton(isPlaying = isPlaying, onClick = onPlayPauseClick)
            Spacer(Modifier.height(JalJayoSpace.medium))
            Text(
                text = selectedPreset.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (isTimerActive && timerRemainingMs > 0) {
                val totalSeconds = (timerRemainingMs / 1000).toInt()
                val formattedTime = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
                Spacer(Modifier.height(JalJayoSpace.small))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(JalJayoSpace.large))
        Text(
            text = "Pilih suara",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(JalJayoSpace.small))
        AudioPreset.values().toList().chunked(2).forEach { rowPresets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(JalJayoSpace.small)
            ) {
                rowPresets.forEach { preset ->
                    NightChoice(
                        label = preset.label(),
                        selected = preset == selectedPreset,
                        modifier = Modifier.weight(1f),
                        onClick = { onPresetSelect(preset) }
                    )
                }
                if (rowPresets.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(JalJayoSpace.small))
        }

        Spacer(Modifier.height(JalJayoSpace.medium))
        Text(
            text = "Timer tidur",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(JalJayoSpace.small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(JalJayoSpace.small)
        ) {
            listOf(15, 30, 45).forEach { minutes ->
                NightChoice(
                    label = "$minutes menit",
                    selected = minutes == selectedDuration,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDurationSelect(minutes)
                        if (isPlaying) onStartTimerClick(minutes)
                    }
                )
            }
        }

        if (isTimerActive) {
            TextButton(
                onClick = onCancelTimerClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .heightIn(min = JalJayoSpace.touchTarget)
            ) {
                Text("Hentikan timer", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(JalJayoSpace.xLarge))
    }
}

@Composable
private fun NightChoice(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline
    val fillColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .heightIn(min = JalJayoSpace.audioChoiceHeight)
            .clip(CircleShape)
            .background(fillColor)
            .border(
                width = if (selected) JalJayoSpace.selectedOutlineWidth else JalJayoSpace.outlineWidth,
                color = borderColor,
                shape = CircleShape
            )
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = JalJayoSpace.small, vertical = JalJayoSpace.xSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val shape = CircleShape
    val fill = if (isPlaying) MaterialTheme.colorScheme.surface
    else MaterialTheme.colorScheme.secondary
    val edge = if (isPlaying) MaterialTheme.colorScheme.outline
    else MaterialTheme.colorScheme.secondary
    val iconColor = if (isPlaying) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSecondary
    val label = if (isPlaying) "Jeda" else "Putar"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(JalJayoSpace.playButton)
                .clip(shape)
                .background(fill)
                .border(
                    width = if (isPlaying) JalJayoSpace.outlineWidth else JalJayoSpace.selectedOutlineWidth,
                    color = edge,
                    shape = shape
                )
                .clickable(
                    role = Role.Button,
                    onClickLabel = if (isPlaying) "Jeda suara" else "Putar suara",
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(Modifier.size(JalJayoSpace.medium)) {
                    if (isPlaying) {
                        val barWidth = size.width * 0.24f
                        val barHeight = size.height * 0.72f
                        val gap = size.width * 0.16f
                        drawRoundRect(
                            color = iconColor,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                x = (size.width - (barWidth * 2 + gap)) / 2,
                                y = (size.height - barHeight) / 2
                            ),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2)
                        )
                        drawRoundRect(
                            color = iconColor,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                x = (size.width - (barWidth * 2 + gap)) / 2 + barWidth + gap,
                                y = (size.height - barHeight) / 2
                            ),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2)
                        )
                    } else {
                        val triangle = Path().apply {
                            moveTo(size.width * 0.34f, size.height * 0.2f)
                            lineTo(size.width * 0.78f, size.height * 0.5f)
                            lineTo(size.width * 0.34f, size.height * 0.8f)
                            close()
                        }
                        drawPath(triangle, color = iconColor)
                    }
                }
                Spacer(Modifier.height(JalJayoSpace.xSmall))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = iconColor
                )
            }
        }
    }
}

/** Static night-sky motif, kept decorative and out of the TalkBack reading order. */
@Composable
private fun NightSky(modifier: Modifier = Modifier) {
    val background = MaterialTheme.colorScheme.background
    val starlight = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.42f)
    val moonlight = MaterialTheme.colorScheme.secondary.copy(alpha = 0.78f)
    val stars = listOf(
        0.10f to 0.34f, 0.21f to 0.18f, 0.34f to 0.40f, 0.43f to 0.16f,
        0.58f to 0.28f, 0.72f to 0.12f, 0.85f to 0.35f, 0.93f to 0.20f,
        0.17f to 0.62f, 0.39f to 0.68f, 0.66f to 0.60f, 0.89f to 0.68f
    )

    Canvas(modifier.clearAndSetSemantics {}) {
        val dotRadius = JalJayoSpace.xSmall.toPx() / 2
        stars.forEachIndexed { index, (x, y) ->
            drawCircle(
                color = starlight.copy(alpha = if (index % 3 == 0) 0.78f else 0.5f),
                radius = if (index % 4 == 0) dotRadius else dotRadius * 0.62f,
                center = androidx.compose.ui.geometry.Offset(size.width * x, size.height * y)
            )
        }

        val moonRadius = JalJayoSpace.moonDiameter.toPx() / 2
        val moonCenter = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.45f)
        drawCircle(color = moonlight, radius = moonRadius, center = moonCenter)
        drawCircle(
            color = background,
            radius = moonRadius,
            center = moonCenter.copy(x = moonCenter.x + moonRadius * 0.48f, y = moonCenter.y - moonRadius * 0.35f)
        )

        val crossStars = listOf(0.29f to 0.31f, 0.77f to 0.49f, 0.55f to 0.77f)
        crossStars.forEach { (x, y) ->
            val center = androidx.compose.ui.geometry.Offset(size.width * x, size.height * y)
            val arm = JalJayoSpace.small.toPx()
            val stroke = JalJayoSpace.starStrokeWidth.toPx()
            drawLine(starlight, center.copy(y = center.y - arm), center.copy(y = center.y + arm), stroke, StrokeCap.Round)
            drawLine(starlight, center.copy(x = center.x - arm), center.copy(x = center.x + arm), stroke, StrokeCap.Round)
        }
    }
}

private fun AudioPreset.label(): String = when (this) {
    AudioPreset.THETA_6HZ -> "Theta 6Hz"
    AudioPreset.BROWN_NOISE -> "Brown Noise"
    AudioPreset.RAIN_SOUND -> "Hujan"
    AudioPreset.OCEAN_WAVES -> "Ombak"
}
