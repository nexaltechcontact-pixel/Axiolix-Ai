package com.example.ui.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RenameDialog(
  initialTitle: String,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var text by remember { mutableStateOf(initialTitle) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CyberDark,
    modifier = Modifier
      .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
      .testTag("rename_dialog"),
    title = {
      Text(
        text = "RENAME SESSION LOG",
        color = NeonCyan,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
    },
    text = {
      Column {
        Text(
          text = "Enter updated identifier for this neural session:",
          color = TextSecondary,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = text,
          onValueChange = { text = it },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = CyberSurface,
            unfocusedContainerColor = CyberSurface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rename_text_field")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(text) },
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonCyan,
          contentColor = CyberBlack
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("confirm_rename_button")
      ) {
        Text(
          text = "UPDATE",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_rename_button")
      ) {
        Text(
          text = "CANCEL",
          color = TextSecondary,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  )
}
