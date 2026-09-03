package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class QuickPrompt(
  val label: String,
  val prompt: String,
  val icon: ImageVector,
  val tintColor: androidx.compose.ui.graphics.Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickPromptChips(
  onSelectPrompt: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val prompts = listOf(
    QuickPrompt(
      label = "Quantum Algorithms",
      prompt = "Explain quantum computing principles, superposition, and quantum supremacy in modern cryptography.",
      icon = Icons.Default.Psychology,
      tintColor = NeonCyan
    ),
    QuickPrompt(
      label = "Security Protocol Audit",
      prompt = "Provide a zero-trust cyber security audit checklist for distributed edge APIs.",
      icon = Icons.Default.Shield,
      tintColor = MatrixGreen
    ),
    QuickPrompt(
      label = "Kotlin Concurrency",
      prompt = "Demonstrate an asynchronous Kotlin coroutines Flow state pipeline with backpressure.",
      icon = Icons.Default.Code,
      tintColor = NeonViolet
    ),
    QuickPrompt(
      label = "Sci-Fi Cyber Narrative",
      prompt = "Generate a futuristic cyberpunk storyline premise set in Neo-Kyoto 2142.",
      icon = Icons.Default.AutoAwesome,
      tintColor = NeonCyan
    )
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = "RAPID PROTOCOL INITIATION",
      fontSize = 10.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      color = TextSecondary,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      prompts.forEachIndexed { index, item ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurface)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .clickable { onSelectPrompt(item.prompt) }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("quick_prompt_chip_$index")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = item.icon,
              contentDescription = null,
              tint = item.tintColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = item.label,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = TextPrimary
            )
          }
        }
      }
    }
  }
}
