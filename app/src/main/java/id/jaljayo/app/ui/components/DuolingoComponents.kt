package id.jaljayo.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.jaljayo.app.ui.theme.DayPrimary
import id.jaljayo.app.ui.theme.DayPrimaryDark
import id.jaljayo.app.ui.theme.DaySuccess
import id.jaljayo.app.ui.theme.DaySuccessDark
import id.jaljayo.app.ui.theme.DaySurfaceBorder
import id.jaljayo.app.ui.theme.DaySurfaceShadow
import id.jaljayo.app.ui.theme.DayTextSecondary

/**
 * Komponen Tombol Taktil 3D Khas Duolingo (2026 Edition).
 * Memiliki bibir bawah 3D (shadow bevel) yang membal saat ditekan.
 */
@Composable
fun Tactile3DButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = DayPrimary,
    shadowColor: Color = DayPrimaryDark,
    textColor: Color = Color.White,
    height: Dp = 52.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowHeight = 4.dp
    val animatedOffsetY by animateDpAsState(
        targetValue = if (isPressed) shadowHeight else 0.dp,
        label = "duolingo_tactile_press"
    )

    Box(
        modifier = modifier
            .height(height + shadowHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Lapisan Bayangan Bawah Taktil 3D (Shadow Bevel)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = shadowHeight)
                .clip(shape)
                .background(shadowColor)
        )

        // Lapisan Permukaan Utama Tombol (Front Surface)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = animatedOffsetY)
                .clip(shape)
                .background(if (enabled) containerColor else Color(0xFFCBD5E1)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (enabled) textColor else Color(0xFF64748B),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Kartu Elevasi Taktil (Duolingo Tactile Card Container).
 */
@Composable
fun TactileCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = DaySurfaceBorder,
    shadowColor: Color = DaySurfaceShadow,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(shadowColor)
            .padding(bottom = 3.5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundColor)
                .border(1.5.dp, borderColor, shape)
        ) {
            content()
        }
    }
}

/**
 * Opsi Pilihan Kuis Taktil Bergaya Duolingo.
 */
@Composable
fun TactileSelectOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    badgeEmoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) DayPrimary else DaySurfaceBorder
    val bgColor = if (isSelected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
    val shadowColor = if (isSelected) DayPrimaryDark else DaySurfaceShadow

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(shadowColor)
            .padding(bottom = if (isSelected) 4.dp else 2.5.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(if (isSelected) 2.dp else 1.5.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFDBEAFE) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badgeEmoji, fontSize = 22.sp)
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DayPrimary else MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = DayTextSecondary,
                        lineHeight = 16.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) DayPrimary else Color.Transparent)
                        .border(
                            2.dp,
                            if (isSelected) DayPrimary else Color(0xFFCBD5E1),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

/**
 * Progress Bar Khas Duolingo.
 */
@Composable
fun DuolingoProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFFE2E8F0),
    progressColor: Color = DayPrimary
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0.05f, 1.0f))
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(progressColor)
        )
    }
}

/**
 * Chip Pilihan Taktil untuk Durasi & Jam Bangun.
 */
@Composable
fun TactileChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = DayPrimary,
    selectedBgColor: Color = Color(0xFFEFF6FF)
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) selectedBgColor else MaterialTheme.colorScheme.surface)
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = if (isSelected) selectedColor else DaySurfaceBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Badge Api Streak Duolingo (🔥 Streak Pill).
 */
@Composable
fun TactileStreakPill(
    streakCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEA580C))
            .padding(bottom = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFFF7ED))
                .border(1.5.dp, Color(0xFFFDBA74), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 16.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "$streakCount",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEA580C)
                )
            }
        }
    }
}

/**
 * Modal Dialog Perayaan & Status Streak Konsistensi (Duolingo Style).
 */
@Composable
fun StreakCelebrationDialog(
    streakCount: Int,
    longestStreak: Int,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(24.dp)
                    .clickable(enabled = false) {}
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEDD5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 42.sp)
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "$streakCount Hari Konsisten!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Setiap sesi relaksasi & kartu sains yang Anda selesaikan memperkuat ritme sirkadian biologis.",
                        fontSize = 12.5.sp,
                        color = DayTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    // 7-Day Tracker Row (Sen - Min)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                        days.forEachIndexed { index, day ->
                            val isCompleted = index < (streakCount % 7).coerceAtLeast(1)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompleted) Color(0xFFEA580C) else Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isCompleted) "✓" else "",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isCompleted) Color(0xFFEA580C) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Badge Rekor Terpanjang
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏆 Rekor Terbaik Anda: $longestStreak Hari Berturut-turut",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Tactile3DButton(
                        text = "Pertahankan Ritme!",
                        onClick = onDismiss,
                        containerColor = Color(0xFFEA580C),
                        shadowColor = Color(0xFFC2410C)
                    )
                }
            }
        }
    }
}

/**
 * Versi 1: Guided Interactive Walkthrough Pasca-Onboarding.
 */
@Composable
fun AppWalkthroughDialog(
    onDismiss: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    val titles = listOf(
        "Batas Kafein Sirkadian ☕",
        "Edukasi Sains & Video Kilat 📱",
        "Api Konsistensi Sirkadian 🔥",
        "Navigasi 3 Tab Bawah 🧭"
    )
    val bodies = listOf(
        "JalJayo menghitung batas kafein otomatis (8 jam sebelum tidur) agar adenosin dan fase Deep Sleep malam Anda terlindungi dari gangguan.",
        "Dapatkan satu intisari sains tidur dan video kilat 35 detik per hari dari pakar tidur Vishal Dasani untuk adaptasi kebiasaan bertahap.",
        "Setiap kali Anda menandai paham materi atau memutar audio tidur, rantai streak Anda bertambah. Jangan sampai apinya padam!",
        "Gunakan tab bawah: Siang untuk edukasi harian, Malam untuk pemutar audio tidur & sleep timer, serta Setelan untuk rekonfigurasi."
    )
    val emojis = listOf("☕", "📱", "🔥", "🧭")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PANDUAN PENGGUNAAN (${step + 1}/$totalSteps)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DayPrimary,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .clickable(onClick = onDismiss),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emojis[step], fontSize = 38.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = titles[step],
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = bodies[step],
                        fontSize = 13.sp,
                        color = DayTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    Tactile3DButton(
                        text = if (step < totalSteps - 1) "Lanjutkan ➔" else "Mulai Gunakan JalJayo 🚀",
                        onClick = {
                            if (step < totalSteps - 1) {
                                step++
                            } else {
                                onDismiss()
                            }
                        },
                        containerColor = DayPrimary,
                        shadowColor = DayPrimaryDark
                    )
                }
            }
        }
    }
}

/**
 * Versi 2: Pusat Informasi & Panduan Aplikasi On-Demand (Ikon "ℹ️").
 */
@Composable
fun AppHelpGuideDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(22.dp)
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ℹ️", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Panduan Pemakaian JalJayo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .clickable(onClick = onDismiss),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    HelpItem(
                        icon = "☀️",
                        title = "1. Beranda Mode Siang (06.00 - 19.59)",
                        desc = "Fokus pada konsumsi kafein yang aman dan 1 kartu edukasi sains per hari. Anda juga dapat membuka seluruh bab di Katalog CBT-I kapan saja."
                    )
                    Spacer(Modifier.height(10.dp))
                    HelpItem(
                        icon = "🌙",
                        title = "2. Ruang Relaksasi Malam (20.00 - 05.59)",
                        desc = "Menonaktifkan stimulasi visual (Night Lockout) dan menyajikan audio pengantar tidur (Theta 6Hz, Brown Noise, dsb.) disertai sleep timer otomatis."
                    )
                    Spacer(Modifier.height(10.dp))
                    HelpItem(
                        icon = "🔥",
                        title = "3. Rantai Streak Sirkadian",
                        desc = "Selesaikan kartu harian atau putar audio malam untuk memperpanjang streak konsistensi harian. Rantai tersimpan aman di perangkat Anda."
                    )
                    Spacer(Modifier.height(10.dp))
                    HelpItem(
                        icon = "👨‍⚕️",
                        title = "4. Narasumber: Vishal Dasani",
                        desc = "Seluruh materi sirkadian dan video kilat disarikan langsung dari edukasi klinis Certified Sleep Educator Vishal Dasani."
                    )

                    Spacer(Modifier.height(20.dp))

                    Tactile3DButton(
                        text = "Mengerti & Tutup",
                        onClick = onDismiss,
                        containerColor = DayPrimary,
                        shadowColor = DayPrimaryDark
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpItem(icon: String, title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, DaySurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(Modifier.height(2.dp))
                Text(desc, fontSize = 11.5.sp, color = DayTextSecondary, lineHeight = 16.sp)
            }
        }
    }
}
