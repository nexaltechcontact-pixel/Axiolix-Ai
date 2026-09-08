package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AxiolixUiState(
  val introCompleted: Boolean = false,
  val currentUser: UserAccountEntity? = null,
  val showAuthDialog: Boolean = false,
  val showDashboard: Boolean = false,
  val currentConversationId: Long? = null,
  val isGenerating: Boolean = false,
  val searchQuery: String = "",
  val statusMessage: String? = null,
  val errorMessage: String? = null
)

class AxiolixViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val chatRepository = ChatRepository(database.chatDao())
  private val authRepository = AuthRepository(database.userDao())

  private val _uiState = MutableStateFlow(AxiolixUiState())
  val uiState: StateFlow<AxiolixUiState> = _uiState.asStateFlow()

  val conversations: StateFlow<List<ConversationEntity>> = chatRepository.allConversations
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val currentMessages: StateFlow<List<ChatMessageEntity>> = _uiState
    .flatMapLatest { state ->
      val convId = state.currentConversationId
      if (convId != null) {
        chatRepository.getMessagesForConversation(convId)
      } else {
        flowOf(emptyList())
      }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      // Check or init default session
      val activeUser = authRepository.getActiveSession()
        ?: authRepository.createDefaultOperativeIfEmpty()
      _uiState.value = _uiState.value.copy(currentUser = activeUser)

      // Initialize initial conversation if none exists
      chatRepository.allConversations.collect { convList ->
        if (convList.isNotEmpty() && _uiState.value.currentConversationId == null) {
          _uiState.value = _uiState.value.copy(currentConversationId = convList.first().id)
        } else if (convList.isEmpty() && _uiState.value.currentConversationId == null) {
          val newId = chatRepository.createNewConversation("Axiolix Neural Stream")
          _uiState.value = _uiState.value.copy(currentConversationId = newId)
        }
      }
    }
  }

  fun completeIntro() {
    _uiState.value = _uiState.value.copy(introCompleted = true)
  }

  fun showAuthDialog() {
    _uiState.value = _uiState.value.copy(showAuthDialog = true)
  }

  fun dismissAuthDialog() {
    _uiState.value = _uiState.value.copy(showAuthDialog = false, errorMessage = null)
  }

  fun toggleDashboard() {
    _uiState.value = _uiState.value.copy(showDashboard = !_uiState.value.showDashboard)
  }

  fun openDashboard() {
    _uiState.value = _uiState.value.copy(showDashboard = true)
  }

  fun closeDashboard() {
    _uiState.value = _uiState.value.copy(showDashboard = false)
  }

  fun setSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun selectConversation(id: Long) {
    _uiState.value = _uiState.value.copy(
      currentConversationId = id,
      showDashboard = false
    )
  }

  fun createNewSession() {
    viewModelScope.launch {
      val count = conversations.value.size + 1
      val newId = chatRepository.createNewConversation("Neural Session #$count")
      _uiState.value = _uiState.value.copy(
        currentConversationId = newId,
        showDashboard = false
      )
    }
  }

  fun sendMessage(prompt: String) {
    val trimmed = prompt.trim()
    if (trimmed.isEmpty()) return

    val convId = _uiState.value.currentConversationId

if (convId == null) {
    _uiState.value = _uiState.value.copy(
        errorMessage = "IMAGE ERROR: No active chat session."
    )
    return
}

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isGenerating = true, errorMessage = null)
      try {
        chatRepository.sendMessage(convId, trimmed)
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(errorMessage = "Signal failure: ${e.localizedMessage}")
      } finally {
        _uiState.value = _uiState.value.copy(isGenerating = false)
      }
    }
  } 
  
fun analyzeImage(imageUri: Uri) {
    val convId = _uiState.value.currentConversationId ?: return

    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            isGenerating = true,
            errorMessage = null
        )

        try {
            val result = chatRepository.analyzeImage(
                conversationId = convId,
                context = getApplication<Application>(),
                imageUri = imageUri
            )

            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Image analysis failed: ${error.localizedMessage}"
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Image analysis failed: ${e.localizedMessage}"
            )
        } finally {
            _uiState.value = _uiState.value.copy(
                isGenerating = false
            )
        }
    }
}
  fun generateImage(prompt: String) {
    val trimmed = prompt.trim()
    if (trimmed.isEmpty()) return

    val convId = _uiState.value.currentConversationId ?: return

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isGenerating = true,
        errorMessage = null
      )

      try {
        val result = chatRepository.generateImage(
  conversationId = convId,
  context = getApplication<Application>(),
  prompt = trimmed
)

        result.onFailure { error ->
          _uiState.value = _uiState.value.copy(
            errorMessage = "IMAGE ERROR: ${error.message ?: error.javaClass.simpleName}"
          )
        }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          errorMessage = "IMAGE EXCEPTION: ${e.message ?: e.javaClass.simpleName}"
        )
      } finally {
        _uiState.value = _uiState.value.copy(
          isGenerating = false
        )
      }
    }
  }
  fun renameConversation(id: Long, newTitle: String) {
    viewModelScope.launch {
      val clean = newTitle.trim()
      if (clean.isNotEmpty()) {
        chatRepository.updateConversationTitle(id, clean)
      }
    }
  }

  fun togglePin(id: Long) {
    viewModelScope.launch {
      chatRepository.togglePinConversation(id)
    }
  }

  fun deleteConversation(id: Long) {
    viewModelScope.launch {
      chatRepository.deleteConversation(id)
      if (_uiState.value.currentConversationId == id) {
        val remaining = conversations.value.filter { it.id != id }
        if (remaining.isNotEmpty()) {
          _uiState.value = _uiState.value.copy(currentConversationId = remaining.first().id)
        } else {
          val newId = chatRepository.createNewConversation("New Session")
          _uiState.value = _uiState.value.copy(currentConversationId = newId)
        }
      }
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      chatRepository.deleteAllConversations()
      val newId = chatRepository.createNewConversation("Axiolix Neural Stream")
      _uiState.value = _uiState.value.copy(currentConversationId = newId)
    }
  }

  fun login(identifier: String, accessKey: String, onResult: (Boolean, String) -> Unit) {
    viewModelScope.launch {
      val res = authRepository.login(identifier, accessKey)
      res.onSuccess { user ->
        _uiState.value = _uiState.value.copy(
          currentUser = user,
          showAuthDialog = false,
          statusMessage = "Authentication verified. Welcome back, ${user.username}."
        )
        onResult(true, "Access Granted")
      }.onFailure { err ->
        val msg = err.localizedMessage ?: "Authentication failed."
        _uiState.value = _uiState.value.copy(errorMessage = msg)
        onResult(false, msg)
      }
    }
  }

  fun register(username: String, email: String, accessKey: String, onResult: (Boolean, String) -> Unit) {
    viewModelScope.launch {
      val res = authRepository.register(username, email, accessKey)
      res.onSuccess { user ->
        _uiState.value = _uiState.value.copy(
          currentUser = user,
          showAuthDialog = false,
          statusMessage = "Neural ID created. Clearance assigned: ${user.clearanceLevel}."
        )
        onResult(true, "Operative Registered")
      }.onFailure { err ->
        val msg = err.localizedMessage ?: "Registration failed."
        _uiState.value = _uiState.value.copy(errorMessage = msg)
        onResult(false, msg)
      }
    }
  }

  fun logout() {
    _uiState.value = _uiState.value.copy(
      currentUser = null,
      showAuthDialog = true
    )
  }
}
