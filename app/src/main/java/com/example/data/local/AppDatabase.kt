package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.UserAccountEntity

@Database(
  entities = [
    ConversationEntity::class,
    ChatMessageEntity::class,
    UserAccountEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun chatDao(): ChatDao
  abstract fun userDao(): UserDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "axiolix_database"
        )
          .fallbackToDestructiveMigration(dropAllTables = false)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
