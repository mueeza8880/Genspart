package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "parent_profile")
data class ParentProfile(
  @PrimaryKey val id: String = "primary_parent",
  val displayName: String = "Parent",
  val email: String = "parent@kidspark.family",
  val preferredLanguage: String = "English", // "English" or "Urdu"
  val notificationsEnabled: Boolean = true,
  val reminderTime: String = "07:30",
  val onboardingCompleted: Boolean = false,
  val activeChildId: String? = null
)

@Entity(tableName = "child_profiles")
data class ChildProfile(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val nickname: String,
  val birthMonth: Int,
  val birthYear: Int,
  val approximateAge: Int,
  val selectedInterests: List<String> = emptyList(),
  val parentGoals: List<String> = emptyList(),
  val wakeTime: String = "07:30 AM",
  val bedtime: String = "08:00 PM",
  val avatarBgHex: String = "#087F8C",
  val avatarEmoji: String = "⭐",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "routines")
data class RoutineItem(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val childId: String,
  val title: String,
  val category: String, // Morning, Meals, Learning, Free play, Physical movement, Outdoor activities, Quiet time, Family connection, Chores, Bedtime
  val timeStr: String,
  val durationMinutes: Int = 30,
  val notes: String = "",
  val isCompleted: Boolean = false,
  val isWeekend: Boolean = false,
  val dateRecorded: String = "", // YYYY-MM-DD
  val orderIndex: Int = 0
)

data class ActivityCatalogItem(
  val id: String,
  val title: String,
  val ageRange: String, // "1–2 years", "3–4 years", "5–6 years", "7–8 years", "9–10 years"
  val targetAgeBracket: Int, // 1 for 1-2, 3 for 3-4, 5 for 5-6, 7 for 7-8, 9 for 9-10
  val estimatedDuration: String,
  val category: String,
  val requiredMaterials: List<String>,
  val setupInstructions: String,
  val stepByStep: List<String>,
  val parentParticipationLevel: String, // "Active Co-play", "Gentle Guidance", "Independent with Supervision"
  val safetyNotes: String,
  val developmentalAreas: List<String>,
  val easierVariation: String,
  val challengingVariation: String,
  val isOutdoor: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ChildActivityLog(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val childId: String,
  val activityId: String,
  val activityTitle: String,
  val category: String,
  val completedDate: String, // YYYY-MM-DD
  val parentRating: Int = 5,
  val parentNotes: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_activities")
data class FavoriteActivity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val childId: String,
  val activityId: String
)

@Entity(tableName = "child_habits")
data class ChildHabit(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val childId: String,
  val title: String,
  val category: String,
  val iconEmoji: String = "✨",
  val frequency: String = "Daily", // Daily, Weekdays, Weekends
  val completedDates: List<String> = emptyList(), // list of YYYY-MM-DD
  val encouragementNote: String = "Great effort today!"
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val childId: String,
  val date: String, // YYYY-MM-DD
  val category: String, // "Daily Joy", "Creative Moment", "Curious Question", "Milestone", "Kindness"
  val content: String,
  val smileMoment: String = "",
  val skillsNoticed: List<String> = emptyList(),
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "screen_free_progress")
data class ScreenFreeProgress(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val childId: String,
  val dayNumber: Int, // 1 to 30
  val isCompleted: Boolean = false,
  val completedDate: String? = null,
  val parentNote: String = ""
)

data class ScreenFreeMission(
  val day: Int,
  val title: String,
  val theme: String,
  val objective: String,
  val duration: String,
  val materials: List<String>,
  val instructions: List<String>,
  val ageAdaptation: String,
  val parentReflectionQuestion: String
)

data class PathwayProject(
  val id: String,
  val title: String,
  val ageSuitability: String,
  val description: String,
  val materials: String,
  val estimatedTime: String
)

data class FutureSkillsPathway(
  val id: String,
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val accentColorHex: String,
  val description: String,
  val relatedEverydaySkills: List<String>,
  val ageGuidance: String,
  val projects: List<PathwayProject>,
  val reflectionQuestions: List<String>,
  val challengeChecklist: List<String>
)

data class DailyPlanDay(
  val dayName: String,
  val dateStr: String,
  val learningActivity: String,
  val movementActivity: String,
  val creativeActivity: String,
  val estimatedTotalMinutes: Int = 75
)

data class WeeklyPlanState(
  val childName: String,
  val focusTheme: String,
  val days: List<DailyPlanDay>
)
