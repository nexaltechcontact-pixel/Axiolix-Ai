package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
  @Query("SELECT * FROM conversations ORDER BY isPinned DESC, updatedAt DESC")
  fun getAllConversations(): Flow<List<ConversationEntity>>

  @Query("SELECT * FROM conversations WHERE id = :id")
  suspend fun getConversationById(id: Long): ConversationEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConversation(conversation: ConversationEntity): Long

  @Update
  suspend fun updateConversation(conversation: ConversationEntity)

  @Query("DELETE FROM conversations WHERE id = :id")
  suspend fun deleteConversationById(id: Long)

  @Query("DELETE FROM conversations")
  suspend fun deleteAllConversations()

  @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity): Long

  @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
  suspend fun deleteMessagesForConversation(conversationId: Long)

  @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT 1")
  suspend fun getLastMessage(conversationId: Long): ChatMessageEntity?

  @Query("SELECT COUNT(*) FROM chat_messages WHERE conversationId = :conversationId")
  suspend fun getMessageCount(conversationId: Long): Int

  @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
  fun searchConversations(query: String): Flow<List<ConversationEntity>>
}
