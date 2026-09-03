package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "chat_messages",
  foreignKeys = [
    ForeignKey(
      entity = ConversationEntity::class,
      parentColumns = ["id"],
      childColumns = ["conversationId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("conversationId")]
)
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val conversationId: Long,
  val sender: String, // "USER" or "AXIOLIX"
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isError: Boolean = false
)
