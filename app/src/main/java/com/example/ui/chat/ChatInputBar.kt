package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChatInputBar(
  isGenerating: Boolean,
  onSendMessage: (String) -> Unit,
  onPickImage: () -> Unit,
  modifier: Modifier = Modifier
) {
  var text by remember { mutableStateOf("") }

  fun sendCurrent() {
    val clean = text.trim()
    if (clean.isNotEmpty() && !isGenerating) {
      onSendMessage(clean)
      text = ""
    }
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(CyberDark)
      .border(1.dp, CyberBorder)
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    OutlinedTextField(
      value = text,
      onValueChange = { text = it },
      placeholder = {
        Text(
          text = if (isGenerating) "Axiolix is synthesizing response..." else "Transmit prompt to Axiolix...",
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          color = TextMuted
        )
      },
      enabled = !isGenerating,
      maxLines = 4,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
      keyboardActions = KeyboardActions(onSend = { sendCurrent() }),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan,
        unfocusedBorderColor = CyberBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = CyberSurface,
        unfocusedContainerColor = CyberSurface
      ),
      modifier = Modifier
        .weight(1f)
        .testTag("chat_input_text_field")
    )

    // Send Button
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(
          if (text.isNotBlank() && !isGenerating) NeonCyan else CyberSurface
        )
        .border(
          width = 1.dp,
          color = if (text.isNotBlank() && !isGenerating) NeonCyan else CyberBorder,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isGenerating) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = NeonCyan,
          strokeWidth = 2.dp
        )
      } else {
        IconButton(
          onClick = { sendCurrent() },
          enabled = text.isNotBlank() && !isGenerating,
          modifier = Modifier
            .size(46.dp)
            .testTag("send_message_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Transmit Message",
            tint = if (text.isNotBlank()) Color.Black else TextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
