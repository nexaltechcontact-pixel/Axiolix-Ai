package com.example.ui.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberDark
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun IntroTransitionScreen(
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var progress by remember { mutableFloatStateOf(0f) }
  var statusText by remember { mutableStateOf("INITIALIZING QUANTUM CORE...") }

  // Infinite pulsing animation for cyber rings
  val infiniteTransition = rememberInfiniteTransition(label = "cyber_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val ringRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ring_rotation"
  )

  LaunchedEffect(Unit) {
    // Progressive boot sequence stages
    delay(400)
    progress = 0.28f
    statusText = "ALIGNING NEURAL VECTORS..."
    delay(500)
    progress = 0.65f
    statusText = "QUANTUM ENCRYPTION LOCKED [AES-256]"
    delay(500)
    progress = 0.92f
    statusText = "SYNAPSE MATRIX ONLINE..."
    delay(400)
    progress = 1.0f
    statusText = "AXIOLIX // READY FOR DISPATCH"
    delay(450)
    onComplete()
  }

  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(400, easing = FastOutSlowInEasing),
    label = "boot_progress"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .testTag("intro_transition_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Futuristic cyber grid and particle canvas in the background
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = size.width
      val canvasHeight = size.height
      val gridSpacing = 48.dp.toPx()

      // Horizontal tech lines
      var y = 0f
      while (y < canvasHeight) {
        drawLine(
          color = Color(0x1000F0FF),
          start = Offset(0f, y),
          end = Offset(canvasWidth, y),
          strokeWidth = 1f
        )
        y += gridSpacing
      }

      // Vertical tech lines
      var x = 0f
      while (x < canvasWidth) {
        drawLine(
          color = Color(0x1000F0FF),
          start = Offset(x, 0f),
          end = Offset(x, canvasHeight),
          strokeWidth = 1f
        )
        x += gridSpacing
      }

      // Pulsing center holographic ring
      val center = Offset(canvasWidth / 2f, canvasHeight / 2f - 40.dp.toPx())
      drawCircle(
        color = NeonCyan.copy(alpha = 0.18f),
        radius = 110.dp.toPx() * pulseScale,
        center = center,
        style = Stroke(width = 2.dp.toPx())
      )

      drawCircle(
        color = NeonViolet.copy(alpha = 0.22f),
        radius = 135.dp.toPx() * (2.1f - pulseScale),
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
      )
    }

    // Main Center Branding Content
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)
    ) {
      // Futuristic Logo Box with glowing border
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(130.dp)
          .scale(pulseScale)
          .shadow(24.dp, shape = CircleShape, spotColor = NeonCyan)
          .background(CyberDark, CircleShape)
          .border(
            width = 2.dp,
            brush = Brush.sweepGradient(
              listOf(NeonCyan, NeonViolet, NeonCyan)
            ),
            shape = CircleShape
          )
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_axiolix_logo),
          contentDescription = "Axiolix AI Logo",
          modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // App Title with Cybernetic Styling
      Text(
        text = "AXIOLIX",
        fontSize = 38.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 8.sp,
        color = NeonCyan,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.testTag("app_logo_title")
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Subtitle
      Text(
        text = "COGNITIVE AI MATRIX",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 4.sp,
        color = NeonViolet,
        fontFamily = FontFamily.Monospace
      )

      Spacer(modifier = Modifier.height(48.dp))

      // Futuristic Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(Color(0xFF151924))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(animatedProgress)
            .height(6.dp)
            .background(
              Brush.horizontalGradient(
                listOf(NeonViolet, NeonCyan, MatrixGreen)
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Status Indicator
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (animatedProgress >= 1f) MatrixGreen else NeonCyan)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = statusText,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )
      }
    }

    // Skip Button in top right
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 48.dp, end = 24.dp),
      contentAlignment = Alignment.TopEnd
    ) {
      Text(
        text = "SKIP [>>]",
        color = TextMuted,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .clickable { onComplete() }
          .padding(8.dp)
          .testTag("skip_intro_button")
      )
    }

    // Bottom Version/Security stamp
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 32.dp),
      contentAlignment = Alignment.BottomCenter
    ) {
      Text(
        text = "SECURE PROTOCOL // QUANTUM NODE v3.5 // ALL SYSTEMS STABLE",
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        color = TextMuted,
        letterSpacing = 1.sp
      )
    }
  }
}
