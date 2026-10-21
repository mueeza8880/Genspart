package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KidsparkDao {

  // --- Parent Profile ---
  @Query("SELECT * FROM parent_profile WHERE id = 'primary_parent' LIMIT 1")
  fun getParentProfile(): Flow<ParentProfile?>

  @Query("SELECT * FROM parent_profile WHERE id = 'primary_parent' LIMIT 1")
  suspend fun getParentProfileSync(): ParentProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveParentProfile(profile: ParentProfile)

  // --- Child Profiles ---
  @Query("SELECT * FROM child_profiles ORDER BY createdAt ASC")
  fun getAllChildren(): Flow<List<ChildProfile>>

  @Query("SELECT * FROM child_profiles ORDER BY createdAt ASC")
  suspend fun getAllChildrenSync(): List<ChildProfile>

  @Query("SELECT * FROM child_profiles WHERE id = :childId LIMIT 1")
  fun getChildById(childId: String): Flow<ChildProfile?>

  @Query("SELECT * FROM child_profiles WHERE id = :childId LIMIT 1")
  suspend fun getChildByIdSync(childId: String): ChildProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChild(child: ChildProfile)

  @Update
  suspend fun updateChild(child: ChildProfile)

  @Query("DELETE FROM child_profiles WHERE id = :childId")
  suspend fun deleteChild(childId: String)

  // --- Routines ---
  @Query("SELECT * FROM routines WHERE childId = :childId ORDER BY orderIndex ASC, timeStr ASC")
  fun getRoutinesForChild(childId: String): Flow<List<RoutineItem>>

  @Query("SELECT * FROM routines WHERE childId = :childId")
  suspend fun getRoutinesForChildSync(childId: String): List<RoutineItem>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRoutine(routine: RoutineItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRoutines(routines: List<RoutineItem>)

  @Update
  suspend fun updateRoutine(routine: RoutineItem)

  @Query("UPDATE routines SET isCompleted = :completed WHERE id = :routineId")
  suspend fun updateRoutineCompletion(routineId: String, completed: Boolean)

  @Query("DELETE FROM routines WHERE id = :routineId")
  suspend fun deleteRoutine(routineId: String)

  @Query("DELETE FROM routines WHERE childId = :childId")
  suspend fun deleteRoutinesForChild(childId: String)

  // --- Activity Logs ---
  @Query("SELECT * FROM activity_logs WHERE childId = :childId ORDER BY timestamp DESC")
  fun getActivityLogsForChild(childId: String): Flow<List<ChildActivityLog>>

  @Query("SELECT * FROM activity_logs WHERE childId = :childId")
  suspend fun getActivityLogsForChildSync(childId: String): List<ChildActivityLog>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivityLog(log: ChildActivityLog)

  @Query("DELETE FROM activity_logs WHERE id = :logId")
  suspend fun deleteActivityLog(logId: String)

  // --- Favorites ---
  @Query("SELECT activityId FROM favorite_activities WHERE childId = :childId")
  fun getFavoritesForChild(childId: String): Flow<List<String>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addFavorite(fav: FavoriteActivity)

  @Query("DELETE FROM favorite_activities WHERE childId = :childId AND activityId = :activityId")
  suspend fun removeFavorite(childId: String, activityId: String)

  // --- Habits ---
  @Query("SELECT * FROM child_habits WHERE childId = :childId")
  fun getHabitsForChild(childId: String): Flow<List<ChildHabit>>

  @Query("SELECT * FROM child_habits WHERE childId = :childId")
  suspend fun getHabitsForChildSync(childId: String): List<ChildHabit>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabit(habit: ChildHabit)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabits(habits: List<ChildHabit>)

  @Update
  suspend fun updateHabit(habit: ChildHabit)

  @Query("DELETE FROM child_habits WHERE id = :habitId")
  suspend fun deleteHabit(habitId: String)

  // --- Journal ---
  @Query("SELECT * FROM journal_entries WHERE childId = :childId ORDER BY createdAt DESC")
  fun getJournalEntriesForChild(childId: String): Flow<List<JournalEntry>>

  @Query("SELECT * FROM journal_entries WHERE childId = :childId")
  suspend fun getJournalEntriesForChildSync(childId: String): List<JournalEntry>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertJournalEntry(entry: JournalEntry)

  @Update
  suspend fun updateJournalEntry(entry: JournalEntry)

  @Query("DELETE FROM journal_entries WHERE id = :entryId")
  suspend fun deleteJournalEntry(entryId: String)

  // --- Screen Free Adventure ---
  @Query("SELECT * FROM screen_free_progress WHERE childId = :childId")
  fun getScreenFreeProgressForChild(childId: String): Flow<List<ScreenFreeProgress>>

  @Query("SELECT * FROM screen_free_progress WHERE childId = :childId")
  suspend fun getScreenFreeProgressForChildSync(childId: String): List<ScreenFreeProgress>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveScreenFreeProgress(progress: ScreenFreeProgress)
}
