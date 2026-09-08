package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.api.GeminiApiClient
import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(
  private val chatDao: ChatDao,
  private val geminiApiClient: GeminiApiClient = GeminiApiClient()
) {

  val allConversations: Flow<List<ConversationEntity>> =
    chatDao.getAllConversations()

  fun getMessagesForConversation(
    conversationId: Long
  ): Flow<List<ChatMessageEntity>> {
    return chatDao.getMessagesForConversation(conversationId)
  }

  suspend fun createNewConversation(
    title: String = "New Session"
  ): Long {
    val conversation = ConversationEntity(
      title = title,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )

    return chatDao.insertConversation(conversation)
  }

  suspend fun updateConversationTitle(
    conversationId: Long,
    newTitle: String
  ) {
    val existing = chatDao.getConversationById(conversationId)

    if (existing != null) {
      chatDao.updateConversation(
        existing.copy(
          title = newTitle,
          updatedAt = System.currentTimeMillis()
        )
      )
    }
  }

  suspend fun togglePinConversation(
    conversationId: Long
  ) {
    val existing = chatDao.getConversationById(conversationId)

    if (existing != null) {
      chatDao.updateConversation(
        existing.copy(
          isPinned = !existing.isPinned
        )
      )
    }
  }

  suspend fun deleteConversation(
    conversationId: Long
  ) {
    chatDao.deleteConversationById(conversationId)
  }

  suspend fun deleteAllConversations() {
    chatDao.deleteAllConversations()
  }

  suspend fun sendMessage(
    conversationId: Long,
    text: String
  ): Result<ChatMessageEntity> {

    val now = System.currentTimeMillis()

    // 1. Insert user message
    val userMsg = ChatMessageEntity(
      conversationId = conversationId,
      sender = "USER",
      content = text,
      timestamp = now
    )

    chatDao.insertMessage(userMsg)

    // 2. Update conversation title if default
    val conv = chatDao.getConversationById(conversationId)

    if (conv != null) {
      val isDefault =
        conv.title == "New Session" ||
        conv.title.startsWith("Session #")

      val updatedTitle = if (isDefault) {
        text.take(32).trim().let {
          if (text.length > 32) "$it..." else it
        }
      } else {
        conv.title
      }

      chatDao.updateConversation(
        conv.copy(
          title = updatedTitle,
          updatedAt = now
        )
      )
    }

    // 3. Fetch conversation history
    val historyEntities = mutableListOf<Pair<String, String>>()

    val replyResult = geminiApiClient.generateChatReply(
      historyEntities,
      text
    )

    val botReplyText = replyResult.getOrElse {
      "Neural synchronization error. Subsystems recalibrating..."
    }

    // 4. Insert Axiolix response
    val botMsg = ChatMessageEntity(
      conversationId = conversationId,
      sender = "AXIOLIX",
      content = botReplyText,
      timestamp = System.currentTimeMillis()
    )

    val botMsgId = chatDao.insertMessage(botMsg)

    // 5. Update conversation timestamp
    if (conv != null) {
      chatDao.updateConversation(
        conv.copy(
          updatedAt = System.currentTimeMillis()
        )
      )
    }

    return Result.success(
      botMsg.copy(id = botMsgId)
    )
  }

  suspend fun analyzeImage(
    conversationId: Long,
    context: Context,
    imageUri: Uri
  ): Result<ChatMessageEntity> {

    val now = System.currentTimeMillis()

    // 1. Insert image selection message
    val userMsg = ChatMessageEntity(
      conversationId = conversationId,
      sender = "USER",
      content = "🖼️ Image selected for analysis",
      timestamp = now
    )

    chatDao.insertMessage(userMsg)

    // 2. Send image to Gemini
    val result = geminiApiClient.analyzeImage(
      context = context,
      imageUri = imageUri,
      prompt = "Analyze this image carefully. Describe the important objects, visible text, scene, colors, and useful details. If text is visible, read it accurately. Be concise but informative."
    )

    val analysis = result.getOrElse {
      return Result.failure(it)
    }

    // 3. Insert Axiolix analysis response
    val botMsg = ChatMessageEntity(
      conversationId = conversationId,
      sender = "AXIOLIX",
      content = analysis,
      timestamp = System.currentTimeMillis()
    )

    val botMsgId = chatDao.insertMessage(botMsg)

    // 4. Update conversation timestamp
    val conv = chatDao.getConversationById(conversationId)

    if (conv != null) {
      chatDao.updateConversation(
        conv.copy(
          updatedAt = System.currentTimeMillis()
        )
      )
    }

    return Result.success(
      botMsg.copy(id = botMsgId)
    )
  }
  
    suspend fun generateImage(
    conversationId: Long,
    context: Context,
    prompt: String
  ): Result<ChatMessageEntity> {

    val now = System.currentTimeMillis()

    val userMsg = ChatMessageEntity(
      conversationId = conversationId,
      sender = "USER",
      content = "🎨 Image generation: $prompt",
      timestamp = now
    )

    chatDao.insertMessage(userMsg)

    val result = geminiApiClient.generateImage(prompt)

    if (result.isFailure) {
      return Result.failure(
        result.exceptionOrNull()
          ?: Exception("Image generation failed.")
      )
    }

    val imageBytes = result.getOrThrow()

    val imageDir = java.io.File(context.filesDir, "generated_images")

    if (!imageDir.exists()) {
      imageDir.mkdirs()
    }

    val imageFile = java.io.File(
      imageDir,
      "generated_${System.currentTimeMillis()}.png"
    )

    imageFile.writeBytes(imageBytes)

    val botMsg = ChatMessageEntity(
      conversationId = conversationId,
      sender = "AXIOLIX",
      content = "🖼️IMAGE_FILE:${imageFile.absolutePath}",
      timestamp = System.currentTimeMillis()
    )

    val botMsgId = chatDao.insertMessage(botMsg)

    val conv = chatDao.getConversationById(conversationId)

    if (conv != null) {
      chatDao.updateConversation(
        conv.copy(
          updatedAt = System.currentTimeMillis()
        )
      )
    }

    return Result.success(
      botMsg.copy(id = botMsgId)
    )
  }
  suspend fun getLastMessage(
    conversationId: Long
  ): ChatMessageEntity? {
    return chatDao.getLastMessage(conversationId)
  }

  suspend fun getMessageCount(
    conversationId: Long
  ): Int {
    return chatDao.getMessageCount(conversationId)
  }
}
