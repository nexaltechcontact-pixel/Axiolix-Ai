package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,
  val email: String,
  val passwordHash: String,
  val clearanceLevel: String = "LEVEL-3 CLEARANCE",
  val createdAt: Long = System.currentTimeMillis(),
  val lastLoginAt: Long = System.currentTimeMillis()
)
