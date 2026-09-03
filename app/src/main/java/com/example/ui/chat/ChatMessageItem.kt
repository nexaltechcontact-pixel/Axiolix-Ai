package com.example.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.ChatMessageEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageItem(
  message: ChatMessageEntity,
  userCallsign: String,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isBot = message.sender == "AXIOLIX"
  val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
  val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp)
      .testTag(if (isBot) "bot_message_item" else "user_message_item"),
    horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
  ) {
    if (isBot) {
      // Axiolix Cyber Avatar
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(CyberDark)
          .border(1.dp, NeonCyan, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_axiolix_logo),
          contentDescription = "Axiolix AI",
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
    }

    // Message Bubble
    Column(
      modifier = Modifier.fillMaxWidth(0.86f),
      horizontalAlignment = if (isBot) Alignment.Start else Alignment.End
    ) {
      // Sender Header & Timestamp
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 3.dp, start = 4.dp, end = 4.dp)
      ) {
        if (isBot) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(MatrixGreen)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "AXIOLIX // NEURAL CORE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = NeonCyan
          )
        } else {
          Text(
            text = userCallsign.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = NeonViolet
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(NeonViolet)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = formattedTime,
          fontSize = 9.sp,
          fontFamily = FontFamily.Monospace,
          color = TextMuted
        )
      }

      // Bubble Body
      Card(
        shape = RoundedCornerShape(
          topStart = if (isBot) 2.dp else 14.dp,
          topEnd = if (isBot) 14.dp else 2.dp,
          bottomStart = 14.dp,
          bottomEnd = 14.dp
        ),
        colors = CardDefaults.cardColors(
          containerColor = if (isBot) CyberSurface else CyberSurfaceElevated
        ),
        modifier = Modifier
          .border(
            width = 1.dp,
            brush = if (isBot) {
              Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.6f), CyberBorder))
            } else {
              Brush.linearGradient(listOf(NeonViolet.copy(alpha = 0.6f), CyberBorder))
            },
            shape = RoundedCornerShape(
              topStart = if (isBot) 2.dp else 14.dp,
              topEnd = if (isBot) 14.dp else 2.dp,
              bottomStart = 14.dp,
              bottomEnd = 14.dp
            )
          )
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = message.content,
            fontSize = 14.sp,
            fontFamily = FontFamily.Default,
            color = TextPrimary,
            lineHeight = 21.sp
          )

          // Footer action row (e.g. copy)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 6.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Axiolix Message", message.content)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Log copied to clipboard", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy message",
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }

    if (!isBot) {
      Spacer(modifier = Modifier.width(10.dp))
      // User Cyber Avatar
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(CyberDark)
          .border(1.dp, NeonViolet, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Operative",
          tint = NeonViolet,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
