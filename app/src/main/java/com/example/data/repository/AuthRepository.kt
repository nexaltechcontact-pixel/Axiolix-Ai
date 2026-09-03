package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.local.entity.UserAccountEntity
import java.security.MessageDigest

class AuthRepository(private val userDao: UserDao) {

  private fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(password.toByteArray())
    return digest.fold("") { str, it -> str + "%02x".format(it) }
  }

  suspend fun getActiveSession(): UserAccountEntity? {
    return userDao.getLatestLoggedInUser()
  }

  suspend fun login(identifier: String, password: String): Result<UserAccountEntity> {
    val trimmed = identifier.trim()
    val user = userDao.getUserByUsername(trimmed) ?: userDao.getUserByEmail(trimmed)
    if (user == null) {
      return Result.failure(Exception("Operative identifier not recognized in neural register."))
    }

    val hashed = hashPassword(password)
    if (user.passwordHash != hashed) {
      return Result.failure(Exception("Access key mismatch. Neural authentication rejected."))
    }

    val now = System.currentTimeMillis()
    userDao.updateLastLogin(user.id, now)
    return Result.success(user.copy(lastLoginAt = now))
  }

  suspend fun register(
    username: String,
    email: String,
    password: String
  ): Result<UserAccountEntity> {
    val trimmedUsername = username.trim()
    val trimmedEmail = email.trim()

    if (trimmedUsername.length < 3) {
      return Result.failure(Exception("Operative callsign must be at least 3 characters."))
    }
    if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
      return Result.failure(Exception("Invalid secure net address format."))
    }
    if (password.length < 6) {
      return Result.failure(Exception("Security access key must be at least 6 characters."))
    }

    if (userDao.getUserByUsername(trimmedUsername) != null) {
      return Result.failure(Exception("Callsign already allocated to an active operative."))
    }
    if (userDao.getUserByEmail(trimmedEmail) != null) {
      return Result.failure(Exception("Secure net address already registered."))
    }

    val hashed = hashPassword(password)
    val newUser = UserAccountEntity(
      username = trimmedUsername,
      email = trimmedEmail,
      passwordHash = hashed,
      clearanceLevel = "LEVEL-4 QUANTUM CLEARANCE",
      createdAt = System.currentTimeMillis(),
      lastLoginAt = System.currentTimeMillis()
    )

    val id = userDao.insertUser(newUser)
    return Result.success(newUser.copy(id = id))
  }

  suspend fun createDefaultOperativeIfEmpty(): UserAccountEntity {
    val existing = userDao.getLatestLoggedInUser()
    if (existing != null) return existing

    val defaultOperative = UserAccountEntity(
      username = "Operative-Nexus",
      email = "nexus@axiolix.ai",
      passwordHash = hashPassword("axiolix2099"),
      clearanceLevel = "LEVEL-5 CHIEF ARCHITECT",
      createdAt = System.currentTimeMillis(),
      lastLoginAt = System.currentTimeMillis()
    )
    val id = userDao.insertUser(defaultOperative)
    return defaultOperative.copy(id = id)
  }
}
