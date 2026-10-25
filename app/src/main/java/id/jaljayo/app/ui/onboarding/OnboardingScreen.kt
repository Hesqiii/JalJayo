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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.jaljayo.app.ui.components.DuolingoProgressBar
import id.jaljayo.app.ui.components.Tactile3DButton
import id.jaljayo.app.ui.components.TactileCard
import id.jaljayo.app.ui.components.TactileChip
import id.jaljayo.app.ui.components.TactileSelectOption
import id.jaljayo.app.ui.theme.DayBackground
import id.jaljayo.app.ui.theme.DayPrimary
import id.jaljayo.app.ui.theme.DayPrimaryDark
import id.jaljayo.app.ui.theme.DaySuccess
import id.jaljayo.app.ui.theme.DaySuccessDark
import id.jaljayo.app.ui.theme.DayTextSecondary
import id.jaljayo.core.AudioPreset
import id.jaljayo.core.Complaint
import id.jaljayo.core.calculateSleepSchedule
import id.jaljayo.core.getPresetForComplaint
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Smart Diagnostic Micro-Onboarding (Gaya Duolingo 2026 & Wireframe Sync).
 *
 * Mengutamakan Bahasa Baku, Jelas, & Zero-Friction:
 * 1. Progress Bar tebal di bagian atas.
 * 2. Pilihan kendala tidur yang granular dan bebas dari teks teknis "Auto-set".
 * 3. Kalkulasi sirkadian 5 siklus tidur (Walker, 2017) dan batas kafein (Drake et al., 2013).
 */
@Composable
fun OnboardingScreen(
    onFinished: (preset: AudioPreset, wakeHour: Int, wakeMinute: Int) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedComplaint by remember { mutableStateOf<Complaint?>(Complaint.RACING_THOUGHTS) }
    var selectedSleepDuration by remember { mutableFloatStateOf(7.5f) }
    var selectedWakeHour by remember { mutableIntStateOf(6) }
    var selectedWakeMinute by remember { mutableIntStateOf(0) }

    val totalSteps = 3
    val progress = (step + 1).toFloat() / totalSteps.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DayBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // === Top Bar: Tombol Kembali, Progress Bar Duolingo, & Tombol Lewati ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { step -= 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                    }
                    Spacer(Modifier.width(10.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    DuolingoProgressBar(
                        progress = progress,
                        progressColor = DayPrimary
                    )
                }

                if (step < 2) {
                    Spacer(Modifier.width(10.dp))
                    TextButton(
                        onClick = { onFinished(AudioPreset.THETA_6HZ, 6, 0) }
                    ) {
                        Text(
                            text = "Lewati",
                            fontWeight = FontWeight.Bold,
                            color = DayTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // === Konten Langkah Onboarding ===
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it } + fadeOut())
                    },
                    label = "duolingo_onboarding_step"
                ) { currentStep ->
                    when (currentStep) {
                        0 -> StepComplaintDuolingo(
                            selected = selectedComplaint,
                            onSelect = { selectedComplaint = it },
                            onNext = { step = 1 }
                        )
                        1 -> StepScheduleDuolingo(
                            sleepDuration = selectedSleepDuration,
                            onDurationChange = { selectedSleepDuration = it },
                            wakeHour = selectedWakeHour,
                            wakeMinute = selectedWakeMinute,
                            onTimeChange = { h, m ->
                                selectedWakeHour = h
                                selectedWakeMinute = m
                            },
                            onNext = { step = 2 }
                        )
                        2 -> StepConfirmDuolingo(
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
    }
}

/**
 * Langkah 0: Pilihan Diagnostik Masalah Tidur (Bahasa Baku & Pilihan Lengkap)
 */
@Composable
private fun StepComplaintDuolingo(
    selected: Complaint?,
    onSelect: (Complaint) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "KONFIGURASI AWAL SISTEM",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DayPrimary,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Apa kendala utama yang mengganggu kualitas istirahat Anda?",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 28.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Pilih salah satu kondisi yang paling sering Anda alami untuk kalibrasi audio dan ritme istirahat.",
            fontSize = 13.sp,
            color = DayTextSecondary
        )

        Spacer(Modifier.height(20.dp))

        // Opsi 1: Ruminasi & Overthinking
        TactileSelectOption(
            title = Complaint.RACING_THOUGHTS.title,
            subtitle = Complaint.RACING_THOUGHTS.description,
            isSelected = selected == Complaint.RACING_THOUGHTS,
            badgeEmoji = Complaint.RACING_THOUGHTS.iconEmoji,
            onClick = { onSelect(Complaint.RACING_THOUGHTS) }
        )

        Spacer(Modifier.height(12.dp))

        // Opsi 2: Polusi Suara Lingkungan
        TactileSelectOption(
            title = Complaint.NOISY_ROOM.title,
            subtitle = Complaint.NOISY_ROOM.description,
            isSelected = selected == Complaint.NOISY_ROOM,
            badgeEmoji = Complaint.NOISY_ROOM.iconEmoji,
            onClick = { onSelect(Complaint.NOISY_ROOM) }
        )

        Spacer(Modifier.height(12.dp))

        // Opsi 3: Kelelahan Stimulasi Layar Gawai
        TactileSelectOption(
            title = Complaint.SCREEN_FATIGUE.title,
            subtitle = Complaint.SCREEN_FATIGUE.description,
            isSelected = selected == Complaint.SCREEN_FATIGUE,
            badgeEmoji = Complaint.SCREEN_FATIGUE.iconEmoji,
            onClick = { onSelect(Complaint.SCREEN_FATIGUE) }
        )

        Spacer(Modifier.height(12.dp))

        // Opsi 4: Ketegangan Tubuh & Ritme Tidak Teratur
        TactileSelectOption(
            title = Complaint.IRREGULAR_SCHEDULE.title,
            subtitle = Complaint.IRREGULAR_SCHEDULE.description,
            isSelected = selected == Complaint.IRREGULAR_SCHEDULE,
            badgeEmoji = Complaint.IRREGULAR_SCHEDULE.iconEmoji,
            onClick = { onSelect(Complaint.IRREGULAR_SCHEDULE) }
        )

        Spacer(Modifier.height(24.dp))

        Tactile3DButton(
            text = "Lanjutkan ➔",
            onClick = onNext,
            containerColor = DayPrimary,
            shadowColor = DayPrimaryDark
        )

        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Langkah 1: Kebutuhan Durasi & Target Jam Bangun Pagi (Pilar Sirkadian)
 */
@Composable
private fun StepScheduleDuolingo(
    sleepDuration: Float,
    onDurationChange: (Float) -> Unit,
    wakeHour: Int,
    wakeMinute: Int,
    onTimeChange: (Int, Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "RITME SIRKADIAN BIOLOGIS",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DayPrimary,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Tentukan Target Waktu Bangun Pagi Anda",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Sistem menghitung jadwal tidur ideal berbasis 5 siklus tidur lengkap (Walker, 2017).",
            fontSize = 13.sp,
            color = DayTextSecondary
        )

        Spacer(Modifier.height(20.dp))

        // Pilihan Durasi Tidur
        Text(
            text = "Target Kebutuhan Tidur Harian:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TactileChip(
                text = "7.0 Jam",
                isSelected = sleepDuration == 7.0f,
                onClick = { onDurationChange(7.0f) },
                modifier = Modifier.weight(1f)
            )
            TactileChip(
                text = "7.5 Jam\n(5 Siklus) ✓",
                isSelected = sleepDuration == 7.5f,
                onClick = { onDurationChange(7.5f) },
                modifier = Modifier.weight(1.2f)
            )
            TactileChip(
                text = "8.0 Jam",
                isSelected = sleepDuration == 8.0f,
                onClick = { onDurationChange(8.0f) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Pilihan Jam Bangun
        Text(
            text = "Pilihan Jam Bangun Pagi:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TactileChip(
                text = "05:00",
                isSelected = wakeHour == 5 && wakeMinute == 0,
                onClick = { onTimeChange(5, 0) },
                modifier = Modifier.weight(1f)
            )
            TactileChip(
                text = "06:00 ✓",
                isSelected = wakeHour == 6 && wakeMinute == 0,
                onClick = { onTimeChange(6, 0) },
                modifier = Modifier.weight(1f)
            )
            TactileChip(
                text = "07:00",
                isSelected = wakeHour == 7 && wakeMinute == 0,
                onClick = { onTimeChange(7, 0) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Kalkulasi Pratinjau Cepat
        val previewSchedule = calculateSleepSchedule(LocalTime.of(wakeHour, wakeMinute))
        val timeFmt = DateTimeFormatter.ofPattern("HH.mm")
        TactileCard(
            backgroundColor = Color(0xFFF0FDF4),
            borderColor = Color(0xFFBBF7D0),
            shadowColor = Color(0xFF86EFAC)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🔔 Kalkulasi Sirkadian & Kafein Terhitung:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF15803D)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Jam Tidur Ideal: ${previewSchedule.bedtime.format(timeFmt)} • Pengingat Anchor: ${previewSchedule.anchorTime.format(timeFmt)}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF166534)
                )
                Text(
                    text = "Batas Kafein Terakhir: ${previewSchedule.caffeineCurfew.format(timeFmt)} (8 jam sebelum tidur)",
                    fontSize = 11.5.sp,
                    color = Color(0xFF15803D)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Tactile3DButton(
            text = "Hitung & Tinjau Jadwal ➔",
            onClick = onNext,
            containerColor = DayPrimary,
            shadowColor = DayPrimaryDark
        )

        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Langkah 2: Konfirmasi Akhir & Ringkasan Solusi
 */
@Composable
private fun StepConfirmDuolingo(
    complaint: Complaint?,
    wakeHour: Int,
    wakeMinute: Int,
    onConfirm: () -> Unit
) {
    val schedule = calculateSleepSchedule(LocalTime.of(wakeHour, wakeMinute))
    val preset = getPresetForComplaint(complaint)
    val timeFmt = DateTimeFormatter.ofPattern("HH.mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DaySuccess)
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Konfigurasi Tidur Siap!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Seluruh parameter telah disesuaikan secara ilmiah untuk kebutuhan biologis Anda.",
            fontSize = 13.sp,
            color = DayTextSecondary
        )

        Spacer(Modifier.height(20.dp))

        // Ringkasan Kartu
        TactileCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "JADWAL SIRKADIAN & BATAS KAFEIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DayPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text("✓ Terkalibrasi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DaySuccess)
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Target Bangun", fontSize = 11.sp, color = DayTextSecondary)
                        Text("%02d:%02d".format(wakeHour, wakeMinute), fontSize = 15.sp, fontWeight = FontWeight.Bold)
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

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎧 Preset Relaksasi Malam: ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(preset.displayName, fontSize = 12.sp, color = DayPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Catatan Transparansi Rekonfigurasi
        TactileCard(
            backgroundColor = Color(0xFFFEFCE8),
            borderColor = Color(0xFFFEF08A),
            shadowColor = Color(0xFFFDE047)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ℹ️", fontSize = 20.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Pengaturan Fleksibel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF854D0E)
                    )
                    Text(
                        text = "Konfigurasi jadwal dan pilihan audio dapat disesuaikan kembali sewaktu-waktu di menu Pengaturan.",
                        fontSize = 11.sp,
                        color = Color(0xFFA16207)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Tombol Sukses Besar
        Tactile3DButton(
            text = "Mulai JalJayo 🚀",
            onClick = onConfirm,
            containerColor = DaySuccess,
            shadowColor = DaySuccessDark
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Zero-Login • 100% Data Tersimpan Aman di Perangkat Anda",
            fontSize = 11.sp,
            color = DayTextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))
    }
}
