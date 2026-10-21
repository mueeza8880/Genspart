package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiService
import com.example.data.model.*
import com.example.data.repository.KidsparkRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class Screen(val route: String) {
  object Welcome : Screen("welcome")
  object Onboarding : Screen("onboarding")
  object Dashboard : Screen("dashboard")
  object Planner : Screen("planner")
  object Activities : Screen("activities")
  data class ActivityDetail(val activityId: String) : Screen("activity_detail/$activityId")
  object Explore : Screen("explore")
  data class PathwayDetail(val pathwayId: String) : Screen("pathway_detail/$pathwayId")
  object Journal : Screen("journal")
  object Habits : Screen("habits")
  object ScreenFree : Screen("screen_free")
  object AICoach : Screen("ai_coach")
  object WeeklyPlan : Screen("weekly_plan")
  object Insights : Screen("insights")
  object Children : Screen("children")
  object Settings : Screen("settings")
  object Certificate : Screen("certificate")
}

data class ChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val isUser: Boolean,
  val text: String,
  val timestamp: Long = System.currentTimeMillis()
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class KidsparkViewModel(application: Application) : AndroidViewModel(application) {
  val repository = KidsparkRepository(application)

  // Current screen
  private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  // Selected sub-items for detail screens
  private val _selectedActivityId = MutableStateFlow<String?>(null)
  val selectedActivityId: StateFlow<String?> = _selectedActivityId.asStateFlow()

  private val _selectedPathwayId = MutableStateFlow<String?>(null)
  val selectedPathwayId: StateFlow<String?> = _selectedPathwayId.asStateFlow()

  // Navigation stack for back button handling
  private val screenStack = mutableListOf<Screen>(Screen.Dashboard)

  fun navigateTo(screen: Screen) {
    if (screenStack.lastOrNull() != screen) {
      screenStack.add(screen)
    }
    when (screen) {
      is Screen.ActivityDetail -> _selectedActivityId.value = screen.activityId
      is Screen.PathwayDetail -> _selectedPathwayId.value = screen.pathwayId
      else -> {}
    }
    _currentScreen.value = screen
  }

  fun navigateBack(): Boolean {
    if (screenStack.size > 1) {
      screenStack.removeAt(screenStack.size - 1)
      val prev = screenStack.last()
      _currentScreen.value = prev
      return true
    }
    return false
  }

  // Repository Flows
  val parentProfile = repository.getParentProfile()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allChildren = repository.getAllChildren()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeChild = repository.activeChild
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val routines: StateFlow<List<RoutineItem>> = activeChild.flatMapLatest { child ->
    if (child != null) repository.getRoutinesForChild(child.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val habits: StateFlow<List<ChildHabit>> = activeChild.flatMapLatest { child ->
    if (child != null) repository.getHabitsForChild(child.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val journalEntries: StateFlow<List<JournalEntry>> = activeChild.flatMapLatest { child ->
    if (child != null) repository.getJournalEntries(child.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val screenFreeProgress: StateFlow<List<ScreenFreeProgress>> = activeChild.flatMapLatest { child ->
    if (child != null) repository.getScreenFreeProgress(child.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activityLogs: StateFlow<List<ChildActivityLog>> = activeChild.flatMapLatest { child ->
    if (child != null) repository.getActivityLogsForChild(child.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val favoriteIds: StateFlow<List<String>> = activeChild.flatMapLatest { child ->
    if (child != null) repository.getFavorites(child.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Catalog items
  val allActivities = repository.getAllActivities()
  val allPathways = repository.getPathways()
  val screenFreeMissions = repository.getScreenFreeMissions()

  // AI Coach state
  private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        isUser = false,
        text = "Hello! I am your KIDSPARK Parent Coach. I'm here to suggest personalized play ideas, answer curiosity questions, or help organize peaceful family days. What would you like to explore today?"
      )
    )
  )
  val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

  private val _isAiLoading = MutableStateFlow(false)
  val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

  // Weekly plan state
  private val _weeklyPlan = MutableStateFlow<WeeklyPlanState?>(null)
  val weeklyPlan: StateFlow<WeeklyPlanState?> = _weeklyPlan.asStateFlow()

  // Notification Toast Message
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  fun showToast(msg: String) {
    _userMessage.value = msg
  }

  fun clearToast() {
    _userMessage.value = null
  }

  // Activity Library Filtering
  val activitySearchQuery = MutableStateFlow("")
  val selectedAgeFilter = MutableStateFlow("All")
  val selectedCategoryFilter = MutableStateFlow("All")
  val onlyFavoritesFilter = MutableStateFlow(false)

  val filteredActivities: StateFlow<List<ActivityCatalogItem>> = combine(
    activitySearchQuery,
    selectedAgeFilter,
    selectedCategoryFilter,
    onlyFavoritesFilter,
    favoriteIds
  ) { query, age, category, onlyFav, favs ->
    allActivities.filter { act ->
      val matchesQuery = query.isBlank() || act.title.contains(query, ignoreCase = true) ||
        act.category.contains(query, ignoreCase = true) ||
        act.requiredMaterials.any { it.contains(query, ignoreCase = true) }

      val matchesAge = when (age) {
        "All" -> true
        "1–2" -> act.targetAgeBracket == 1
        "3–4" -> act.targetAgeBracket == 3
        "5–6" -> act.targetAgeBracket == 5
        "7–8" -> act.targetAgeBracket == 7
        "9–10" -> act.targetAgeBracket == 9
        else -> true
      }

      val matchesCategory = category == "All" || act.category.equals(category, ignoreCase = true)
      val matchesFav = !onlyFav || favs.contains(act.id)

      matchesQuery && matchesAge && matchesCategory && matchesFav
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allActivities)

  // --- Actions ---

  fun switchActiveChild(childId: String) {
    viewModelScope.launch {
      repository.setActiveChild(childId)
      showToast("Active child switched")
    }
  }

  fun addNewChild(
    nickname: String,
    birthMonth: Int,
    birthYear: Int,
    age: Int,
    interests: List<String>,
    goals: List<String>
  ) {
    viewModelScope.launch {
      val emojis = listOf("⭐", "🚀", "🎨", "🌱", "🦋", "🦁", "🐬", "💡")
      val colors = listOf("#087F8C", "#FF8A65", "#6B4E9E", "#2E7D5B", "#D85D36", "#1E6091")
      val randomEmoji = emojis.random()
      val randomColor = colors.random()

      val newChild = ChildProfile(
        nickname = nickname.trim(),
        birthMonth = birthMonth,
        birthYear = birthYear,
        approximateAge = age,
        selectedInterests = interests,
        parentGoals = goals,
        avatarEmoji = randomEmoji,
        avatarBgHex = randomColor
      )
      repository.addChild(newChild)
      showToast("Child profile for '$nickname' created!")
      navigateTo(Screen.Dashboard)
    }
  }

  fun deleteChild(childId: String) {
    viewModelScope.launch {
      repository.deleteChild(childId)
      showToast("Profile removed.")
    }
  }

  fun toggleRoutine(routineId: String, completed: Boolean) {
    viewModelScope.launch {
      repository.toggleRoutineCompletion(routineId, completed)
      if (completed) {
        showToast("Great job! Routine marked complete ✨")
      }
    }
  }

  fun addRoutine(
    title: String,
    category: String,
    timeStr: String,
    duration: Int,
    notes: String
  ) {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      val item = RoutineItem(
        childId = child.id,
        title = title.trim(),
        category = category,
        timeStr = timeStr.trim(),
        durationMinutes = duration,
        notes = notes.trim(),
        orderIndex = (routines.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
      )
      repository.addRoutine(item)
      showToast("Added to daily schedule!")
    }
  }

  fun updateRoutine(routine: RoutineItem) {
    viewModelScope.launch {
      repository.updateRoutine(routine)
      showToast("Routine updated")
    }
  }

  fun deleteRoutine(routineId: String) {
    viewModelScope.launch {
      repository.deleteRoutine(routineId)
      showToast("Routine removed")
    }
  }

  fun toggleFavorite(activityId: String) {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      repository.toggleFavorite(child.id, activityId, favoriteIds.value)
    }
  }

  fun logActivityComplete(activity: ActivityCatalogItem, rating: Int = 5, notes: String = "") {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      repository.logActivityCompletion(child.id, activity, rating, notes)
      showToast("Activity logged! Well done!")
    }
  }

  fun addActivityToDailyPlan(activity: ActivityCatalogItem, time: String = "10:00 AM") {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      val routine = RoutineItem(
        childId = child.id,
        title = activity.title,
        category = activity.category,
        timeStr = time,
        durationMinutes = 30,
        notes = "Materials: ${activity.requiredMaterials.take(3).joinToString(", ")}",
        orderIndex = (routines.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
      )
      repository.addRoutine(routine)
      showToast("Activity added to Today's Planner!")
    }
  }

  fun toggleHabitToday(habit: ChildHabit) {
    viewModelScope.launch {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      repository.toggleHabitDate(habit, today)
      showToast("Habit updated for today!")
    }
  }

  fun addHabit(title: String, category: String, icon: String, frequency: String, note: String) {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      val habit = ChildHabit(
        childId = child.id,
        title = title.trim(),
        category = category,
        iconEmoji = icon,
        frequency = frequency,
        encouragementNote = note.ifBlank { "You did it!" }
      )
      repository.addHabit(habit)
      showToast("Habit added!")
    }
  }

  fun deleteHabit(habitId: String) {
    viewModelScope.launch {
      repository.deleteHabit(habitId)
      showToast("Habit removed")
    }
  }

  fun addJournalEntry(category: String, content: String, smileMoment: String, skills: List<String>) {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      val entry = JournalEntry(
        childId = child.id,
        date = today,
        category = category,
        content = content.trim(),
        smileMoment = smileMoment.trim(),
        skillsNoticed = skills
      )
      repository.addJournalEntry(entry)
      showToast("Journal reflection saved!")
    }
  }

  fun deleteJournalEntry(entryId: String) {
    viewModelScope.launch {
      repository.deleteJournalEntry(entryId)
      showToast("Entry removed")
    }
  }

  fun toggleScreenFreeDay(dayNumber: Int, completed: Boolean, note: String = "") {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      repository.toggleScreenFreeDay(child.id, dayNumber, completed, note)
      if (completed) {
        showToast("Day $dayNumber Mission Accomplished! 🌟")
      }
    }
  }

  fun addScreenFreeDayToPlanner(mission: ScreenFreeMission, time: String = "03:30 PM") {
    val child = activeChild.value ?: return
    viewModelScope.launch {
      val routine = RoutineItem(
        childId = child.id,
        title = "Day ${mission.day}: ${mission.title}",
        category = "Free play",
        timeStr = time,
        durationMinutes = 35,
        notes = "Screen-Free Adventure: ${mission.objective}",
        orderIndex = (routines.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
      )
      repository.addRoutine(routine)
      showToast("Mission added to today's schedule!")
    }
  }

  // --- AI Coach Chat ---
  fun sendCoachPrompt(prompt: String) {
    if (prompt.isBlank()) return
    val child = activeChild.value ?: return

    val userMsg = ChatMessage(isUser = true, text = prompt.trim())
    _chatMessages.value = _chatMessages.value + userMsg
    _isAiLoading.value = true

    viewModelScope.launch {
      val replyText = GeminiService.askCoach(
        userPrompt = prompt,
        childName = child.nickname,
        childAge = child.approximateAge,
        interests = child.selectedInterests,
        goals = child.parentGoals
      )
      val botMsg = ChatMessage(isUser = false, text = replyText)
      _chatMessages.value = _chatMessages.value + botMsg
      _isAiLoading.value = false
    }
  }

  // --- AI Weekly Plan ---
  fun generateWeeklyPlan(availableMinutes: Int = 60, outdoorPreference: String = "Mixed") {
    val child = activeChild.value ?: return
    _isAiLoading.value = true
    viewModelScope.launch {
      val rawPlan = GeminiService.generateWeeklyPlan(
        childName = child.nickname,
        childAge = child.approximateAge,
        interests = child.selectedInterests,
        goals = child.parentGoals,
        availableMinutesPerDay = availableMinutes,
        outdoorPreference = outdoorPreference
      )

      val days = rawPlan.map { (dayName, triple) ->
        DailyPlanDay(
          dayName = dayName,
          dateStr = "This Week",
          learningActivity = triple.first,
          movementActivity = triple.second,
          creativeActivity = triple.third,
          estimatedTotalMinutes = availableMinutes
        )
      }

      _weeklyPlan.value = WeeklyPlanState(
        childName = child.nickname,
        focusTheme = "Creativity & Active Discovery",
        days = days
      )
      _isAiLoading.value = false
      showToast("7-Day Personalized Plan Generated!")
    }
  }

  fun applyWeeklyPlanToPlanner() {
    val plan = _weeklyPlan.value ?: return
    val child = activeChild.value ?: return
    viewModelScope.launch {
      val newRoutines = plan.days.flatMapIndexed { index, day ->
        listOf(
          RoutineItem(
            childId = child.id,
            title = "${day.dayName}: ${day.learningActivity}",
            category = "Learning",
            timeStr = "09:30 AM",
            durationMinutes = 30,
            orderIndex = index * 3 + 1
          ),
          RoutineItem(
            childId = child.id,
            title = "${day.dayName}: ${day.movementActivity}",
            category = "Physical movement",
            timeStr = "02:00 PM",
            durationMinutes = 25,
            orderIndex = index * 3 + 2
          ),
          RoutineItem(
            childId = child.id,
            title = "${day.dayName}: ${day.creativeActivity}",
            category = "Creativity",
            timeStr = "04:30 PM",
            durationMinutes = 30,
            orderIndex = index * 3 + 3
          )
        )
      }
      repository.addRoutine(newRoutines.first()) // Add sample to today
      showToast("Weekly activities merged into schedule!")
    }
  }

  fun saveParentSettings(name: String, language: String, notifications: Boolean) {
    viewModelScope.launch {
      val current = parentProfile.value ?: ParentProfile()
      repository.saveParentProfile(
        current.copy(
          displayName = name.trim(),
          preferredLanguage = language,
          notificationsEnabled = notifications
        )
      )
      showToast("Settings updated successfully")
    }
  }
}
