package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.UserAccountEntity
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthDialog(
  currentUser: UserAccountEntity?,
  errorMessage: String?,
  onDismiss: () -> Unit,
  onLogin: (identifier: String, accessKey: String, (Boolean, String) -> Unit) -> Unit,
  onRegister: (username: String, email: String, accessKey: String, (Boolean, String) -> Unit) -> Unit,
  modifier: Modifier = Modifier
) {
  var isRegisterMode by remember { mutableStateOf(false) }

  var usernameInput by remember { mutableStateOf(currentUser?.username ?: "") }
  var emailInput by remember { mutableStateOf(currentUser?.email ?: "") }
  var passwordInput by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  var localError by remember { mutableStateOf<String?>(null) }
  var isSubmitting by remember { mutableStateOf(false) }

  val focusManager = LocalFocusManager.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .border(
          width = 1.5.dp,
          brush = Brush.linearGradient(listOf(NeonCyan, NeonViolet)),
          shape = RoundedCornerShape(16.dp)
        )
        .testTag("auth_dialog_card"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CyberDark)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        // Top Header with Close and Security Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF003840)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Security Clearance",
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "NEURAL ACCESS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace,
                color = NeonCyan
              )
              Text(
                text = "AES-256 SECURE TERMINAL",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MatrixGreen
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_auth_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss",
              tint = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Toggle Buttons: LOGIN vs REGISTER
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceVariant)
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (!isRegisterMode) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
              .border(
                width = if (!isRegisterMode) 1.dp else 0.dp,
                color = if (!isRegisterMode) NeonCyan else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
              )
              .clickable {
                isRegisterMode = false
                localError = null
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "AUTHENTICATE",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = if (!isRegisterMode) NeonCyan else TextMuted
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isRegisterMode) NeonViolet.copy(alpha = 0.2f) else Color.Transparent)
              .border(
                width = if (isRegisterMode) 1.dp else 0.dp,
                color = if (isRegisterMode) NeonViolet else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
              )
              .clickable {
                isRegisterMode = true
                localError = null
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "REGISTER ID",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = if (isRegisterMode) NeonViolet else TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Username / Callsign Field
        OutlinedTextField(
          value = usernameInput,
          onValueChange = {
            usernameInput = it
            localError = null
          },
          label = {
            Text(
              text = if (isRegisterMode) "Operative Callsign" else "Callsign or Email",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan)
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = TextSecondary,
            focusedContainerColor = CyberSurface,
            unfocusedContainerColor = CyberSurface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("username_input_field")
        )

        // Email Field (Shown during Registration)
        AnimatedVisibility(visible = isRegisterMode) {
          Column {
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
              value = emailInput,
              onValueChange = {
                emailInput = it
                localError = null
              },
              label = {
                Text(
                  text = "Secure Net Address (Email)",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp
                )
              },
              leadingIcon = {
                Icon(Icons.Default.Security, contentDescription = null, tint = NeonViolet)
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
              ),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonViolet,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = NeonViolet,
                unfocusedLabelColor = TextSecondary,
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("email_input_field")
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Access Key (Password) Field
        OutlinedTextField(
          value = passwordInput,
          onValueChange = {
            passwordInput = it
            localError = null
          },
          label = {
            Text(
              text = "Quantum Access Key",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan)
          },
          trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(
                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Access Key Visibility",
                tint = TextSecondary
              )
            }
          },
          singleLine = true,
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
          ),
          keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = TextSecondary,
            focusedContainerColor = CyberSurface,
            unfocusedContainerColor = CyberSurface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("password_input_field")
        )

        // Error message display
        val activeError = localError ?: errorMessage
        if (!activeError.isNullOrEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "⚠ $activeError",
            color = CyberPink,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
          onClick = {
            focusManager.clearFocus()
            isSubmitting = true
            localError = null

            if (isRegisterMode) {
              if (usernameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank()) {
                localError = "All terminal parameters required."
                isSubmitting = false
                return@Button
              }
              onRegister(usernameInput, emailInput, passwordInput) { success, msg ->
                isSubmitting = false
                if (!success) {
                  localError = msg
                }
              }
            } else {
              if (usernameInput.isBlank() || passwordInput.isBlank()) {
                localError = "Identifier and access key required."
                isSubmitting = false
                return@Button
              }
              onLogin(usernameInput, passwordInput) { success, msg ->
                isSubmitting = false
                if (!success) {
                  localError = msg
                }
              }
            }
          },
          enabled = !isSubmitting,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isRegisterMode) NeonViolet else NeonCyan,
            contentColor = CyberBlack
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("submit_auth_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isSubmitting) "SYNCHRONIZING..." else if (isRegisterMode) "REGISTER OPERATIVE" else "AUTHENTICATE ACCESS",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Demo / Guest Access Shortcut
        TextButton(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("guest_access_button")
        ) {
          Text(
            text = "CONTINUE IN GUEST TERMINAL MODE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }
      }
    }
  }
}
