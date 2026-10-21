package com.example.data.repository

import android.content.Context
import com.example.data.local.KidsparkDatabase
import com.example.data.model.*
import com.example.data.seed.ActivitySeedData
import com.example.data.seed.PathwaySeedData
import com.example.data.seed.ScreenFreeSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class KidsparkRepository(context: Context) {
  private val db = KidsparkDatabase.getDatabase(context)
  private val dao = db.kidsparkDao()
  private val scope = CoroutineScope(Dispatchers.IO)

  init {
    // Check initial launch state
    scope.launch {
      val existingParent = dao.getParentProfileSync()
      if (existingParent == null) {
        val sampleChildId = UUID.randomUUID().toString()
        val defaultParent = ParentProfile(
          id = "primary_parent",
          displayName = "Sarah",
          preferredLanguage = "English",
          onboardingCompleted = true,
          activeChildId = sampleChildId
        )
        dao.saveParentProfile(defaultParent)

        // Pre-create initial child for instant delight
        val defaultChild = ChildProfile(
          id = sampleChildId,
          nickname = "Leo",
          birthMonth = 5,
          birthYear = 2021,
          approximateAge = 5,
          selectedInterests = listOf("Building & Blocks", "Nature & Animals", "Drawing & Colors"),
          parentGoals = listOf("Creativity", "Emotional awareness", "Problem-solving"),
          wakeTime = "07:30 AM",
          bedtime = "08:00 PM",
          avatarBgHex = "#087F8C",
          avatarEmoji = "🚀"
        )
        dao.insertChild(defaultChild)
        seedRoutinesForChild(sampleChildId, 5)
        seedHabitsForChild(sampleChildId)
        seedSampleJournal(sampleChildId)
      }
    }
  }

  // --- Parent Profile & Active Child ---
  fun getParentProfile(): Flow<ParentProfile?> = dao.getParentProfile()

  suspend fun saveParentProfile(profile: ParentProfile) {
    dao.saveParentProfile(profile)
  }

  suspend fun setActiveChild(childId: String) {
    val current = dao.getParentProfileSync() ?: ParentProfile()
    dao.saveParentProfile(current.copy(activeChildId = childId))
  }

  val activeChild: Flow<ChildProfile?> = dao.getParentProfile().flatMapLatest { parent ->
    val activeId = parent?.activeChildId
    if (activeId != null) {
      dao.getChildById(activeId)
    } else {
      flowOf(null)
    }
  }

  // --- Children ---
  fun getAllChildren(): Flow<List<ChildProfile>> = dao.getAllChildren()

  suspend fun addChild(child: ChildProfile): String {
    dao.insertChild(child)
    seedRoutinesForChild(child.id, child.approximateAge)
    seedHabitsForChild(child.id)
    setActiveChild(child.id)
    return child.id
  }

  suspend fun updateChild(child: ChildProfile) {
    dao.updateChild(child)
  }

  suspend fun deleteChild(childId: String) {
    dao.deleteChild(childId)
    val remaining = dao.getAllChildrenSync()
    if (remaining.isNotEmpty()) {
      setActiveChild(remaining.first().id)
    } else {
      val parent = dao.getParentProfileSync()
      if (parent != null) {
        dao.saveParentProfile(parent.copy(activeChildId = null))
      }
    }
  }

  // --- Routines ---
  fun getRoutinesForChild(childId: String): Flow<List<RoutineItem>> = dao.getRoutinesForChild(childId)

  suspend fun addRoutine(routine: RoutineItem) {
    dao.insertRoutine(routine)
  }

  suspend fun updateRoutine(routine: RoutineItem) {
    dao.updateRoutine(routine)
  }

  suspend fun toggleRoutineCompletion(routineId: String, completed: Boolean) {
    dao.updateRoutineCompletion(routineId, completed)
  }

  suspend fun deleteRoutine(routineId: String) {
    dao.deleteRoutine(routineId)
  }

  suspend fun seedRoutinesForChild(childId: String, age: Int) {
    val existing = dao.getRoutinesForChildSync(childId)
    if (existing.isNotEmpty()) return

    val routines = listOf(
      RoutineItem(
        childId = childId,
        title = "Morning Wake Up & Sun Salutation",
        category = "Morning",
        timeStr = "07:30 AM",
        durationMinutes = 20,
        notes = "Open blinds and stretch arms like reaching for the sun.",
        orderIndex = 1
      ),
      RoutineItem(
        childId = childId,
        title = "Nutritious Family Breakfast",
        category = "Meals",
        timeStr = "08:15 AM",
        durationMinutes = 30,
        notes = "Fruit salad and toast; share one happy morning thought.",
        orderIndex = 2
      ),
      RoutineItem(
        childId = childId,
        title = "Learning Through Play Exploration",
        category = "Learning",
        timeStr = "09:30 AM",
        durationMinutes = 40,
        notes = "Hands-on puzzle solving or nature discovery.",
        orderIndex = 3
      ),
      RoutineItem(
        childId = childId,
        title = "Free Imaginative Play",
        category = "Free play",
        timeStr = "10:30 AM",
        durationMinutes = 45,
        notes = "Child-led playtime with building blocks and toy figures.",
        orderIndex = 4
      ),
      RoutineItem(
        childId = childId,
        title = "Outdoor Movement & Fresh Air",
        category = "Physical movement",
        timeStr = "11:30 AM",
        durationMinutes = 40,
        notes = "Park walk, chalk games, or living room obstacle course.",
        orderIndex = 5
      ),
      RoutineItem(
        childId = childId,
        title = "Balanced Lunch & Hydration",
        category = "Meals",
        timeStr = "12:30 PM",
        durationMinutes = 35,
        notes = "Fuel up and drink cool water.",
        orderIndex = 6
      ),
      RoutineItem(
        childId = childId,
        title = "Quiet Time & Picture Books",
        category = "Quiet time",
        timeStr = "01:30 PM",
        durationMinutes = 30,
        notes = "Resting on the rug with soft instrumental music and storybooks.",
        orderIndex = 7
      ),
      RoutineItem(
        childId = childId,
        title = "Creative Arts & Building Project",
        category = "Family connection",
        timeStr = "03:30 PM",
        durationMinutes = 35,
        notes = "Drawing, clay, or mini cardboard engineering.",
        orderIndex = 8
      ),
      RoutineItem(
        childId = childId,
        title = "Dinner & Evening Reflection",
        category = "Meals",
        timeStr = "06:30 PM",
        durationMinutes = 45,
        notes = "Ask: 'What made you smile today?'",
        orderIndex = 9
      ),
      RoutineItem(
        childId = childId,
        title = "Bedtime Routine & Cozy Storybook",
        category = "Bedtime",
        timeStr = "07:45 PM",
        durationMinutes = 30,
        notes = "Warm bath, brush teeth, and 2 cozy chapters in bed.",
        orderIndex = 10
      )
    )
    dao.insertRoutines(routines)
  }

  // --- Activities ---
  fun getAllActivities(): List<ActivityCatalogItem> = ActivitySeedData.activities

  fun getActivityById(id: String): ActivityCatalogItem? =
    ActivitySeedData.activities.firstOrNull { it.id == id }

  fun getActivityLogsForChild(childId: String): Flow<List<ChildActivityLog>> =
    dao.getActivityLogsForChild(childId)

  suspend fun logActivityCompletion(
    childId: String,
    activity: ActivityCatalogItem,
    rating: Int = 5,
    notes: String = ""
  ) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val log = ChildActivityLog(
      childId = childId,
      activityId = activity.id,
      activityTitle = activity.title,
      category = activity.category,
      completedDate = dateStr,
      parentRating = rating,
      parentNotes = notes
    )
    dao.insertActivityLog(log)
  }

  fun getFavorites(childId: String): Flow<List<String>> = dao.getFavoritesForChild(childId)

  suspend fun toggleFavorite(childId: String, activityId: String, currentFavorites: List<String>) {
    if (currentFavorites.contains(activityId)) {
      dao.removeFavorite(childId, activityId)
    } else {
      dao.addFavorite(FavoriteActivity(childId = childId, activityId = activityId))
    }
  }

  // --- Habits ---
  fun getHabitsForChild(childId: String): Flow<List<ChildHabit>> = dao.getHabitsForChild(childId)

  suspend fun addHabit(habit: ChildHabit) {
    dao.insertHabit(habit)
  }

  suspend fun toggleHabitDate(habit: ChildHabit, dateStr: String) {
    val updated = if (habit.completedDates.contains(dateStr)) {
      habit.completedDates - dateStr
    } else {
      habit.completedDates + dateStr
    }
    dao.updateHabit(habit.copy(completedDates = updated))
  }

  suspend fun deleteHabit(habitId: String) {
    dao.deleteHabit(habitId)
  }

  suspend fun seedHabitsForChild(childId: String) {
    val existing = dao.getHabitsForChildSync(childId)
    if (existing.isNotEmpty()) return

    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val defaultHabits = listOf(
      ChildHabit(
        childId = childId,
        title = "Brushing Teeth Morning & Night",
        category = "Hygiene",
        iconEmoji = "🪥",
        frequency = "Daily",
        completedDates = listOf(dateStr),
        encouragementNote = "Sparkling bright smile!"
      ),
      ChildHabit(
        childId = childId,
        title = "Tidying Play Space Together",
        category = "Responsibility",
        iconEmoji = "🧸",
        frequency = "Daily",
        completedDates = emptyList(),
        encouragementNote = "Every block back in its home."
      ),
      ChildHabit(
        childId = childId,
        title = "Reading Stories Together",
        category = "Learning",
        iconEmoji = "📖",
        frequency = "Daily",
        completedDates = listOf(dateStr),
        encouragementNote = "Imagination takes flight!"
      ),
      ChildHabit(
        childId = childId,
        title = "Drinking Fresh Water",
        category = "Health",
        iconEmoji = "💧",
        frequency = "Daily",
        completedDates = listOf(dateStr),
        encouragementNote = "Healthy and refreshed!"
      ),
      ChildHabit(
        childId = childId,
        title = "Outdoor Movement & Play",
        category = "Movement",
        iconEmoji = "🌳",
        frequency = "Daily",
        completedDates = emptyList(),
        encouragementNote = "Energy and fresh air!"
      )
    )
    dao.insertHabits(defaultHabits)
  }

  // --- Growth Journal ---
  fun getJournalEntries(childId: String): Flow<List<JournalEntry>> = dao.getJournalEntriesForChild(childId)

  suspend fun addJournalEntry(entry: JournalEntry) {
    dao.insertJournalEntry(entry)
  }

  suspend fun deleteJournalEntry(entryId: String) {
    dao.deleteJournalEntry(entryId)
  }

  private suspend fun seedSampleJournal(childId: String) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val sampleEntry = JournalEntry(
      childId = childId,
      date = today,
      category = "Creative Moment",
      content = "Today Leo built an incredible multi-story bridge out of wooden blocks and cardboard tubes. When the top arch toppled over, instead of getting upset, he laughed, inspected the base, and decided to add a double pillar to make it stronger!",
      smileMoment = "He beamed with pride when the toy car rolled across without wobbling.",
      skillsNoticed = listOf("Resilience after failure", "Structural balance", "Spatial reasoning")
    )
    dao.insertJournalEntry(sampleEntry)
  }

  // --- Screen-Free 30-Day Adventure ---
  fun getScreenFreeProgress(childId: String): Flow<List<ScreenFreeProgress>> =
    dao.getScreenFreeProgressForChild(childId)

  fun getScreenFreeMissions(): List<ScreenFreeMission> = ScreenFreeSeedData.missions

  suspend fun toggleScreenFreeDay(childId: String, day: Int, completed: Boolean, note: String = "") {
    val dateStr = if (completed) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) else null
    val progress = ScreenFreeProgress(
      id = "${childId}_day_$day",
      childId = childId,
      dayNumber = day,
      isCompleted = completed,
      completedDate = dateStr,
      parentNote = note
    )
    dao.saveScreenFreeProgress(progress)
  }

  // --- Future Skills Pathways ---
  fun getPathways(): List<FutureSkillsPathway> = PathwaySeedData.pathways

  fun getPathwayById(id: String): FutureSkillsPathway? =
    PathwaySeedData.pathways.firstOrNull { it.id == id }
}
