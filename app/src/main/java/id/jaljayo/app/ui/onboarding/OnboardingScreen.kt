package id.jaljayo.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import id.jaljayo.core.Complaint
import id.jaljayo.core.getPresetForComplaint

/**
 * Smart Diagnostic Micro-Onboarding (BR-00: maks. 3 ketukan, < 60 detik).
 *
 * Alur:
 *   Langkah 0 → Pertanyaan keluhan utama (RACING_THOUGHTS / NOISY_ROOM)
 *   Langkah 1 → Pilih jam bangun pagi
 *   Langkah 2 → Layar konfirmasi + teks wajib PRD
 *
 * @param onFinished Dipanggil saat pengguna selesai / lewati, dengan preset & jam bangun terpilih.
 */
@Composable
fun OnboardingScreen(
    onFinished: (preset: AudioPreset, wakeHour: Int, wakeMinute: Int) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedComplaint by remember { mutableStateOf<Complaint?>(null) }
    var selectedWakeHour by remember { mutableIntStateOf(6) }
    var selectedWakeMinute by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Tombol Lewati di pojok kanan atas (selalu tersedia, hanya di langkah 0 & 1)
        if (step < 2) {
            TextButton(
                onClick = {
                    onFinished(AudioPreset.THETA_6HZ, 6, 0)
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "Lewati",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Indikator langkah (dot)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == step) 10.dp else 7.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= step)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        // Konten tiap langkah dengan animasi slide
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            modifier = Modifier.align(Alignment.Center),
            label = "onboarding_step"
        ) { currentStep ->
            when (currentStep) {
                0 -> StepComplaint(
                    selected = selectedComplaint,
                    onSelect = { complaint ->
                        selectedComplaint = complaint
                        step = 1
                    }
                )
                1 -> StepWakeTime(
                    wakeHour = selectedWakeHour,
                    wakeMinute = selectedWakeMinute,
                    onHourChange = { selectedWakeHour = it },
                    onMinuteChange = { selectedWakeMinute = it },
                    onNext = { step = 2 }
                )
                2 -> StepConfirm(
                    complaint = selectedComplaint,
                    wakeHour = selectedWakeHour,
                    wakeMinute = selectedWakeMinute,
                    onConfirm = {
                        onFinished(
                            getPresetForComplaint(selectedComplaint),
                            selectedWakeHour,
                            selectedWakeMinute
                        )
                    }
                )
            }
        }
    }
}

// ===================== Langkah 0: Pilih Keluhan =====================

@Composable
private fun StepComplaint(
    selected: Complaint?,
    onSelect: (Complaint) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🌙",
            fontSize = 48.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Halo! Apa yang paling\nsering mengganggu tidurmu?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pilih satu yang paling kamu rasakan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        ComplaintCard(
            emoji = "🧠",
            label = "Otak muter terus / overthinking",
            sublabel = "Pikiran tidak bisa berhenti meski tubuh sudah lelah",
            isSelected = selected == Complaint.RACING_THOUGHTS,
            onClick = { onSelect(Complaint.RACING_THOUGHTS) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ComplaintCard(
            emoji = "🔊",
            label = "Kamar bising / tidak tenang",
            sublabel = "Suara dari luar, kosan ramai, atau lingkungan berisik",
            isSelected = selected == Complaint.NOISY_ROOM,
            onClick = { onSelect(Complaint.NOISY_ROOM) }
        )
    }
}

@Composable
private fun ComplaintCard(
    emoji: String,
    label: String,
    sublabel: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ===================== Langkah 1: Pilih Jam Bangun =====================

private val wakeHourOptions = listOf(4, 5, 6, 7, 8, 9)
private val wakeMinuteOptions = listOf(0, 15, 30, 45)

@Composable
private fun StepWakeTime(
    wakeHour: Int,
    wakeMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "⏰", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Jam berapa kamu biasanya\nbangun pagi?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "JalJayo akan otomatis menghitung jam tidur ideal\n(5 siklus × 90 menit = 7,5 jam)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))

        // Pilih Jam
        Text(
            text = "JAM",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            wakeHourOptions.forEach { hour ->
                val selected = wakeHour == hour
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { onHourChange(hour) }
                ) {
                    Text(
                        text = "%02d".format(hour),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pilih Menit
        Text(
            text = "MENIT",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            wakeMinuteOptions.forEach { minute ->
                val selected = wakeMinute == minute
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { onMinuteChange(minute) }
                ) {
                    Text(
                        text = ":%02d".format(minute),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Lanjut →",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

// ===================== Langkah 2: Konfirmasi =====================

@Composable
private fun StepConfirm(
    complaint: Complaint?,
    wakeHour: Int,
    wakeMinute: Int,
    onConfirm: () -> Unit
) {
    val preset = getPresetForComplaint(complaint)
    // Hitung jam tidur: bangun - 7 jam 30 menit
    val totalWakeMinutes = wakeHour * 60 + wakeMinute
    val totalBedMinutes = (totalWakeMinutes - 450 + 1440) % 1440 // -7.5 jam, modulo 24 jam
    val bedHour = totalBedMinutes / 60
    val bedMinute = totalBedMinutes % 60
    val anchorMinutes = (totalBedMinutes - 45 + 1440) % 1440
    val anchorHour = anchorMinutes / 60
    val anchorMin = anchorMinutes % 60

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "✨", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Profil Tidurmu Sudah Siap!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ConfirmRow(label = "Audio relaksasi", value = preset.displayName)
                Spacer(modifier = Modifier.height(12.dp))
                ConfirmRow(label = "Jam bangun", value = "%02d:%02d".format(wakeHour, wakeMinute))
                Spacer(modifier = Modifier.height(12.dp))
                ConfirmRow(label = "Jam tidur ideal", value = "%02d:%02d".format(bedHour, bedMinute))
                Spacer(modifier = Modifier.height(12.dp))
                ConfirmRow(label = "Pengingat tidur", value = "%02d:%02d".format(anchorHour, anchorMin))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teks wajib PRD (AGENTS.md Bagian 4)
        Text(
            text = "Pengaturan ini dapat Anda ubah kapan saja di menu Pengaturan.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Mulai JalJayo 🚀",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun ConfirmRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
