package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
  entities = [
    ParentProfile::class,
    ChildProfile::class,
    RoutineItem::class,
    ChildActivityLog::class,
    FavoriteActivity::class,
    ChildHabit::class,
    JournalEntry::class,
    ScreenFreeProgress::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KidsparkDatabase : RoomDatabase() {
  abstract fun kidsparkDao(): KidsparkDao

  companion object {
    @Volatile
    private var INSTANCE: KidsparkDatabase? = null

    fun getDatabase(context: Context): KidsparkDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          KidsparkDatabase::class.java,
          "kidspark_database"
        )
          .fallbackToDestructiveMigration(true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
