package id.jaljayo.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.jaljayo.app.ui.components.DuolingoProgressBar
import id.jaljayo.app.ui.theme.DayBackground
import id.jaljayo.app.ui.theme.DayPrimary
import id.jaljayo.app.ui.theme.DayTextSecondary
import kotlinx.coroutines.delay

/**
 * Intro Splash Screen Motion Graphic Minimalis:
 * Menampilkan Headline "JalJayo", Subheadline "Good Sleep, Habits",
 * serta animasi progress bar pengisian sirkadian sebelum menuju beranda.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0.0f) }
    val progress = remember { Animatable(0.05f) }

    LaunchedEffect(Unit) {
        // Animasi muncul teks tipografi secara halus dengan efek membal (spring)
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        alpha.animateTo(1.0f, animationSpec = tween(500))

        // Progress bar memuat secara dinamis
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )

        delay(200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DayBackground,
                        Color(0xFFEFF6FF),
                        Color(0xFFFEF3C7)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Headline Utama "JalJayo" dengan Motion Graphic Scale & Fade
            Text(
                text = "JalJayo",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = DayPrimary,
                letterSpacing = (-1.0).sp,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(Modifier.height(8.dp))

            // Subheadline "Good Sleep, Habits"
            Text(
                text = "Good Sleep, Habits",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = DayTextSecondary,
                letterSpacing = 0.5.sp,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(Modifier.height(48.dp))

            // Progress Bar Pengisian
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .alpha(alpha.value)
            ) {
                DuolingoProgressBar(
                    progress = progress.value,
                    trackColor = Color(0xFFE2E8F0),
                    progressColor = Color(0xFF22C55E)
                )
            }
        }
    }
}
