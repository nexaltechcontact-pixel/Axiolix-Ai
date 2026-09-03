package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGlow
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationDashboardView(
  conversations: List<ConversationEntity>,
  currentConversationId: Long?,
  currentUser: UserAccountEntity?,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onSelectConversation: (Long) -> Unit,
  onCreateNewSession: () -> Unit,
  onRenameConversation: (Long, String) -> Unit,
  onTogglePin: (Long) -> Unit,
  onDeleteConversation: (Long) -> Unit,
  onClearAll: () -> Unit,
  onOpenAuthDialog: () -> Unit,
  onCloseDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  var sessionToRename by remember { mutableStateOf<ConversationEntity?>(null) }
  var sessionToDelete by remember { mutableStateOf<ConversationEntity?>(null) }
  var showClearAllConfirmation by remember { mutableStateOf(false) }

  // Filter conversations
  val filteredConversations = remember(conversations, searchQuery) {
    if (searchQuery.isBlank()) {
      conversations
    } else {
      conversations.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }
  }

  val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBlack)
      .testTag("conversation_dashboard_view")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDark)
          .border(1.dp, CyberBorder)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onCloseDashboard,
            modifier = Modifier.testTag("dashboard_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Return to Terminal",
              tint = NeonCyan
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = "SESSION DASHBOARD",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 2.sp,
              color = NeonCyan
            )
            Text(
              text = "ARCHIVED NEURAL CONVERSATIONS",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = TextSecondary
            )
          }
        }

        // Operative badge button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceVariant)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .clickable { onOpenAuthDialog() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("dashboard_auth_badge")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (currentUser != null) MatrixGreen else CyberPink)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = currentUser?.username?.uppercase() ?: "GUEST",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TextPrimary
            )
          }
        }
      }

      // Telemetry Metric Tiles
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricTile(
          label = "STORED LOGS",
          value = "${conversations.size}",
          accentColor = NeonCyan,
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          label = "ACTIVE SESSION",
          value = "#${currentConversationId ?: 1}",
          accentColor = NeonViolet,
          modifier = Modifier.weight(1f)
        )
        MetricTile(
          label = "PROTOCOL",
          value = "AES-256",
          accentColor = MatrixGreen,
          modifier = Modifier.weight(1f)
        )
      }

      // Search & New Session Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = {
            Text(
              text = "Search logs...",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = TextMuted
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChange("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
              }
            }
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = CyberSurface,
            unfocusedContainerColor = CyberSurface
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("dashboard_search_input")
        )

        Button(
          onClick = onCreateNewSession,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = NeonCyan,
            contentColor = CyberBlack
          ),
          modifier = Modifier
            .height(52.dp)
            .testTag("dashboard_new_session_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "NEW",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Section Header & Purge Action
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ALL SESSIONS (${filteredConversations.size})",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp,
          color = TextSecondary
        )

        if (conversations.isNotEmpty()) {
          TextButton(
            onClick = { showClearAllConfirmation = true },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.testTag("purge_all_sessions_button")
          ) {
            Text(
              text = "PURGE ALL",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = CyberPink
            )
          }
        }
      }

      // Conversation list
      if (filteredConversations.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.ChatBubbleOutline,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = if (searchQuery.isNotEmpty()) "NO MATCHING SESSIONS" else "NO ARCHIVED LOGS FOUND",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (searchQuery.isNotEmpty()) "Try adjusting your search query" else "Launch a new session to begin interacting with Axiolix",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = TextMuted
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 24.dp)
        ) {
          items(filteredConversations, key = { it.id }) { conv ->
            val isCurrent = conv.id == currentConversationId
            ConversationItemCard(
              conversation = conv,
              isCurrent = isCurrent,
              formattedDate = dateFormat.format(Date(conv.updatedAt)),
              onSelect = { onSelectConversation(conv.id) },
              onRename = { sessionToRename = conv },
              onTogglePin = { onTogglePin(conv.id) },
              onDelete = { sessionToDelete = conv }
            )
          }
        }
      }
    }
  }

  // Rename Dialog
  sessionToRename?.let { conv ->
    RenameDialog(
      initialTitle = conv.title,
      onDismiss = { sessionToRename = null },
      onConfirm = { newTitle ->
        onRenameConversation(conv.id, newTitle)
        sessionToRename = null
      }
    )
  }

  // Delete Confirmation Dialog
  sessionToDelete?.let { conv ->
    AlertDialog(
      onDismissRequest = { sessionToDelete = null },
      containerColor = CyberDark,
      modifier = Modifier.border(1.dp, CyberPink, RoundedCornerShape(16.dp)),
      title = {
        Text(
          text = "DELETE SESSION LOG?",
          color = CyberPink,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      },
      text = {
        Text(
          text = "Session \"${conv.title}\" and all associated neural message records will be permanently purged.",
          color = TextSecondary,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteConversation(conv.id)
            sessionToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberPink, contentColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_delete_button")
        ) {
          Text("CONFIRM PURGE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { sessionToDelete = null }) {
          Text("CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace)
        }
      }
    )
  }

  // Purge All Confirmation Dialog
  if (showClearAllConfirmation) {
    AlertDialog(
      onDismissRequest = { showClearAllConfirmation = false },
      containerColor = CyberDark,
      modifier = Modifier.border(1.dp, CyberPink, RoundedCornerShape(16.dp)),
      title = {
        Text(
          text = "PURGE COMPLETE ARCHIVE?",
          color = CyberPink,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      },
      text = {
        Text(
          text = "This will erase all previous chat sessions across the neural register. This action cannot be reversed.",
          color = TextSecondary,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onClearAll()
            showClearAllConfirmation = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberPink, contentColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_purge_all_button")
        ) {
          Text("PURGE ALL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearAllConfirmation = false }) {
          Text("CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace)
        }
      }
    )
  }
}

@Composable
private fun ConversationItemCard(
  conversation: ConversationEntity,
  isCurrent: Boolean,
  formattedDate: String,
  onSelect: () -> Unit,
  onRename: () -> Unit,
  onTogglePin: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(
        width = if (isCurrent) 1.5.dp else 1.dp,
        color = if (isCurrent) NeonCyan else if (conversation.isPinned) NeonViolet else CyberBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable { onSelect() }
      .testTag("conversation_card_${conversation.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCurrent) CyberSurfaceElevated else CyberSurface
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Info column
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (conversation.isPinned) {
            Icon(
              imageVector = Icons.Default.PushPin,
              contentDescription = "Pinned",
              tint = NeonViolet,
              modifier = Modifier
                .size(14.dp)
                .padding(end = 4.dp)
            )
          }

          if (isCurrent) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(NeonCyan.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "ACTIVE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = NeonCyan
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
          }

          Text(
            text = formattedDate,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = conversation.title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.Monospace,
          color = if (isCurrent) NeonCyan else TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Action Buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Pin Button
        IconButton(
          onClick = onTogglePin,
          modifier = Modifier
            .size(36.dp)
            .testTag("pin_conversation_${conversation.id}")
        ) {
          Icon(
            imageVector = if (conversation.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
            contentDescription = if (conversation.isPinned) "Unpin" else "Pin",
            tint = if (conversation.isPinned) NeonViolet else TextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }

        // Rename Button
        IconButton(
          onClick = onRename,
          modifier = Modifier
            .size(36.dp)
            .testTag("rename_conversation_${conversation.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Rename",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }

        // Delete Button
        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(36.dp)
            .testTag("delete_conversation_${conversation.id}")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = CyberPink,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun MetricTile(
  label: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(CyberSurface)
      .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = 8.dp)
  ) {
    Column {
      Text(
        text = label,
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        color = TextMuted,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = accentColor
      )
    }
  }
}
