package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserAccountEntity

@Dao
interface UserDao {
  @Query("SELECT * FROM user_accounts WHERE username = :username COLLATE NOCASE LIMIT 1")
  suspend fun getUserByUsername(username: String): UserAccountEntity?

  @Query("SELECT * FROM user_accounts WHERE email = :email COLLATE NOCASE LIMIT 1")
  suspend fun getUserByEmail(email: String): UserAccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserAccountEntity): Long

  @Query("UPDATE user_accounts SET lastLoginAt = :timestamp WHERE id = :userId")
  suspend fun updateLastLogin(userId: Long, timestamp: Long)

  @Query("SELECT * FROM user_accounts ORDER BY lastLoginAt DESC LIMIT 1")
  suspend fun getLatestLoggedInUser(): UserAccountEntity?
}
