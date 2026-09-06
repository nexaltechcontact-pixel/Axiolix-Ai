package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.auth.AuthDialog
import com.example.ui.chat.ChatScreen
import com.example.ui.dashboard.ConversationDashboardView
import com.example.ui.intro.IntroTransitionScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AxiolixViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: AxiolixViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AxiolixApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun AxiolixApp(viewModel: AxiolixViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val conversations by viewModel.conversations.collectAsStateWithLifecycle()
  val currentMessages by viewModel.currentMessages.collectAsStateWithLifecycle()

  val activeConversationTitle = conversations
    .find { it.id == uiState.currentConversationId }?.title
    ?: "Neural Stream"

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberBlack)
  ) {
    AnimatedContent(
      targetState = uiState.introCompleted,
      transitionSpec = {
        fadeIn() togetherWith fadeOut()
      },
      label = "intro_app_transition"
    ) { introDone ->
      if (!introDone) {
        IntroTransitionScreen(
          onComplete = { viewModel.completeIntro() }
        )
      } else {
        AnimatedContent(
          targetState = uiState.showDashboard,
          transitionSpec = {
            fadeIn() togetherWith fadeOut()
          },
          label = "dashboard_chat_transition"
        ) { inDashboard ->
          if (inDashboard) {
            ConversationDashboardView(
              conversations = conversations,
              currentConversationId = uiState.currentConversationId,
              currentUser = uiState.currentUser,
              searchQuery = uiState.searchQuery,
              onSearchQueryChange = { viewModel.setSearchQuery(it) },
              onSelectConversation = { viewModel.selectConversation(it) },
              onCreateNewSession = { viewModel.createNewSession() },
              onRenameConversation = { id, title -> viewModel.renameConversation(id, title) },
              onTogglePin = { viewModel.togglePin(it) },
              onDeleteConversation = { viewModel.deleteConversation(it) },
              onClearAll = { viewModel.clearAllHistory() },
              onOpenAuthDialog = { viewModel.showAuthDialog() },
              onCloseDashboard = { viewModel.closeDashboard() }
            )
          } else {
            ChatScreen(
              messages = currentMessages,
              isGenerating = uiState.isGenerating,
              currentUser = uiState.currentUser,
              conversationTitle = activeConversationTitle,
              onSendMessage = { viewModel.sendMessage(it) },
              ChatScreen(
              messages = currentMessages,
              isGenerating = uiState.isGenerating,
              currentUser = uiState.currentUser,
              conversationTitle = activeConversationTitle,
              onSendMessage = { viewModel.sendMessage(it) },
              onOpenDashboard = { viewModel.openDashboard() },
              onOpenAuthDialog = { viewModel.showAuthDialog() },
              onCreateNewSession = { viewModel.createNewSession() }
            )
          }
        }
      }
    }
              onOpenDashboard = { viewModel.openDashboard() },
              onOpenAuthDialog = { viewModel.showAuthDialog() },
              onCreateNewSession = { viewModel.createNewSession() }
            )
          }
        }
      }
    }

    // Register / Login Pop Up Modal
    if (uiState.showAuthDialog) {
      AuthDialog(
        currentUser = uiState.currentUser,
        errorMessage = uiState.errorMessage,
        onDismiss = { viewModel.dismissAuthDialog() },
        onLogin = { id, key, callback -> viewModel.login(id, key, callback) },
        onRegister = { user, email, key, callback -> viewModel.register(user, email, key, callback) }
      )
    }
  }
}
