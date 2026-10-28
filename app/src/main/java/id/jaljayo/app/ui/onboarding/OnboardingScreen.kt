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
 * Langkah 1: Preferensi Jam Tidur Biasa & Durasi Tidur Fleksibel (Bedtime-First Approach)
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
    // Estimasi jam tidur awal berdasarkan jam bangun - durasi
    var bedtimeHour by remember {
        val totalMinutes = (wakeHour * 60 + wakeMinute) - (sleepDuration * 60).toInt()
        val normalizedMinutes = ((totalMinutes % 1440) + 1440) % 1440
        mutableIntStateOf(normalizedMinutes / 60)
    }
    var bedtimeMinute by remember {
        val totalMinutes = (wakeHour * 60 + wakeMinute) - (sleepDuration * 60).toInt()
        val normalizedMinutes = ((totalMinutes % 1440) + 1440) % 1440
        mutableIntStateOf(normalizedMinutes % 60)
    }

    // Fungsi sinkronisasi jam bangun otomatis saat jam tidur / durasi berubah
    fun updateCalculatedWakeTime(bHour: Int, bMinute: Int, dur: Float) {
        val totalMinutes = (bHour * 60 + bMinute) + (dur * 60).toInt()
        val normalizedMinutes = ((totalMinutes % 1440) + 1440) % 1440
        onTimeChange(normalizedMinutes / 60, normalizedMinutes % 60)
    }

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
            text = "Biasanya Anda tidur jam berapa di malam hari?",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 28.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Atur kebiasaan jam tidur Anda secara leluasa. Sistem akan menyelaraskan jadwal sirkadian dan jam bangun optimal secara otomatis.",
            fontSize = 13.sp,
            color = DayTextSecondary,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(14.dp))

        // Catatan Rekomendasi Jam Tidur dari Aplikasi (Sains Sirkadian)
        TactileCard(
            backgroundColor = Color(0xFFFEFCE8),
            borderColor = Color(0xFFFEF08A),
            shadowColor = Color(0xFFFDE047)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 20.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Rekomendasi Jam Tidur Sains: 22.00 – 23.30",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF854D0E)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Rentang biologis terbaik saat pelepasan melatonin optimal dan fase gelombang lambat (Deep Sleep NREM Tahap 3) mendominasi sebelum tengah malam (Walker, 2017).",
                        fontSize = 11.sp,
                        color = Color(0xFFA16207),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Pengaturan Jam Tidur Fleksibel (Interactive Stepper & Scrolling)
        Text(
            text = "Kebiasaan Jam Tidur Anda:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(10.dp))

        // Card Pengatur Jam & Menit Fleksibel
        TactileCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pengatur Jam
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("JAM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DayTextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        bedtimeHour = if (bedtimeHour == 0) 23 else bedtimeHour - 1
                                        updateCalculatedWakeTime(bedtimeHour, bedtimeMinute, sleepDuration)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "%02d".format(bedtimeHour),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DayPrimary
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        bedtimeHour = (bedtimeHour + 1) % 24
                                        updateCalculatedWakeTime(bedtimeHour, bedtimeMinute, sleepDuration)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                            }
                        }
                    }

                    Spacer(Modifier.width(16.dp))
                    Text(":", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = DayPrimary)
                    Spacer(Modifier.width(16.dp))

                    // Pengatur Menit
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MENIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DayTextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        bedtimeMinute = if (bedtimeMinute < 5) 55 else bedtimeMinute - 5
                                        updateCalculatedWakeTime(bedtimeHour, bedtimeMinute, sleepDuration)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "%02d".format(bedtimeMinute),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DayPrimary
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        bedtimeMinute = (bedtimeMinute + 5) % 60
                                        updateCalculatedWakeTime(bedtimeHour, bedtimeMinute, sleepDuration)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DayPrimary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Chip pilihan jam cepat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(Pair(21, 30), Pair(22, 0), Pair(22, 30), Pair(23, 0), Pair(23, 30), Pair(0, 0)).forEach { (h, m) ->
                        val isSelected = bedtimeHour == h && bedtimeMinute == m
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DayPrimary else Color(0xFFF1F5F9))
                                .clickable {
                                    bedtimeHour = h
                                    bedtimeMinute = m
                                    updateCalculatedWakeTime(h, m, sleepDuration)
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "%02d:%02d".format(h, m),
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Target Kebutuhan Durasi Tidur Harian
        Text(
            text = "Target Kebutuhan Durasi Tidur:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(6.5f, 7.0f, 7.5f, 8.0f, 8.5f).forEach { dur ->
                val isSelected = sleepDuration == dur
                val label = if (dur == 7.5f) "7.5 Jam\n(5 Siklus) ✓" else "$dur Jam"
                TactileChip(
                    text = label,
                    isSelected = isSelected,
                    onClick = {
                        onDurationChange(dur)
                        updateCalculatedWakeTime(bedtimeHour, bedtimeMinute, dur)
                    },
                    modifier = Modifier.weight(if (dur == 7.5f) 1.25f else 1f)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Hasil Perhitungan Waktu Bangun Pagi Alami
        TactileCard(
            backgroundColor = Color(0xFFF0FDF4),
            borderColor = Color(0xFFBBF7D0),
            shadowColor = Color(0xFF86EFAC)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Perkiraan Waktu Bangun Pagi Alami:",
                        fontSize = 11.5.sp,
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "%02d:%02d WIB".format(wakeHour, wakeMinute),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF166534)
                    )
                    Text(
                        text = "Otomatis dihitung dari jam tidur %02d:%02d + %.1f jam durasi".format(bedtimeHour, bedtimeMinute, sleepDuration),
                        fontSize = 10.5.sp,
                        color = Color(0xFF15803D)
                    )
                }

                // Kontrol penyesuaian halus jam bangun
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                val newMin = if (wakeMinute < 15) {
                                    val newH = if (wakeHour == 0) 23 else wakeHour - 1
                                    onTimeChange(newH, wakeMinute + 45)
                                } else {
                                    onTimeChange(wakeHour, wakeMinute - 15)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                val newMin = (wakeMinute + 15) % 60
                                val newH = if (wakeMinute + 15 >= 60) (wakeHour + 1) % 24 else wakeHour
                                onTimeChange(newH, newMin)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                    }
                }
            }
        }

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
 * Langkah 2: Konfirmasi Akhir & Ringkasan Solusi (Bersih, Minimalis, Tanpa Redundansi)
 */
@Composable
private fun StepConfirmDuolingo(
    complaint: Complaint?,
    wakeHour: Int,
    wakeMinute: Int,
    onConfirm: () -> Unit
) {
    val preset = getPresetForComplaint(complaint)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DaySuccess)
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Konfigurasi Tidur Siap!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "JalJayo telah mengalibrasi ritme biologis dan audio relaksasi malam Anda.",
            fontSize = 13.sp,
            color = DayTextSecondary,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(20.dp))

        // Ringkasan Bersih: Hanya memuat Preset dan Target Bangun
        TactileCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PRESET RELAKSASI MALAM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DayPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text("✓ Terkalibrasi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DaySuccess)
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎧", fontSize = 26.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = preset.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Target Bangun: %02d:%02d WIB".format(wakeHour, wakeMinute),
                            fontSize = 12.sp,
                            color = DayTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

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
                        color = Color(0xFFA16207),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Tombol Mulai JalJayo Utama
        Tactile3DButton(
            text = "Mulai JalJayo 🚀",
            onClick = onConfirm,
            containerColor = DaySuccess,
            shadowColor = DaySuccessDark
        )

        Spacer(Modifier.height(10.dp))

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