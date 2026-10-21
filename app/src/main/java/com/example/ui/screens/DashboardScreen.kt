package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityCatalogItem
import com.example.data.model.ChildProfile
import com.example.data.model.RoutineItem
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.components.ActivitySummaryCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  viewModel: KidsparkViewModel,
  onNavigate: (Screen) -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val routines by viewModel.routines.collectAsState()
  val allActivities = viewModel.allActivities
  val favorites by viewModel.favoriteIds.collectAsState()
  val screenFreeProgress by viewModel.screenFreeProgress.collectAsState()

  // Completed routines calculation
  val completedCount = routines.count { it.isCompleted }
  val totalCount = routines.size
  val routineProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

  // Recommendation tailored to active child
  val childAge = activeChild?.approximateAge ?: 5
  val recommendedActivity = remember(childAge) {
    allActivities.firstOrNull { it.targetAgeBracket == childAge || it.targetAgeBracket == (childAge - 1).coerceAtLeast(1) }
      ?: allActivities.first()
  }

  // Parent Reflection input state
  var reflectionText by remember { mutableStateOf("") }
  var reflectionSaved by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Child Profile Card
    item {
      activeChild?.let { child ->
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("child_profile_hero_card")
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(BrandTeal),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = child.avatarEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                  Text(
                    text = child.nickname,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${child.approximateAge} years old • Routine: ${child.wakeTime} – ${child.bedtime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              IconButton(
                onClick = { onNavigate(Screen.Children) },
                modifier = Modifier.testTag("switch_child_icon")
              ) {
                Icon(
                  imageVector = Icons.Default.SwapHoriz,
                  contentDescription = "Switch Child",
                  tint = BrandTeal
                )
              }
            }

            if (child.selectedInterests.isNotEmpty()) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Interests & Emerging Strengths",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(6.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                child.selectedInterests.forEach { interest ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftYellowLight
                  ) {
                    Text(
                      text = interest,
                      style = MaterialTheme.typography.labelSmall,
                      color = Color(0xFF6B5100),
                      fontWeight = FontWeight.Medium,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }

            if (child.parentGoals.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Focus Areas: ${child.parentGoals.joinToString(" • ")}",
                style = MaterialTheme.typography.bodySmall,
                color = BrandTealDark,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // 2. Today's Overview & Routine Progress
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandTealContainer),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("todays_overview_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Today's Schedule Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BrandTealDark
              )
              Text(
                text = "$completedCount of $totalCount activities completed",
                style = MaterialTheme.typography.bodySmall,
                color = BrandTealDark.copy(alpha = 0.8f)
              )
            }

            Surface(
              shape = CircleShape,
              color = BrandTeal
            ) {
              Text(
                text = "${(routineProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          LinearProgressIndicator(
            progress = { routineProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = BrandTeal,
            trackColor = Color.White.copy(alpha = 0.6f)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            TextButton(
              onClick = { onNavigate(Screen.Planner) },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = "Open Full Daily Planner →",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BrandTealDark
              )
            }

            TextButton(
              onClick = { onNavigate(Screen.WeeklyPlan) },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = "AI 7-Day Plan →",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BrandTealDark
              )
            }
          }
        }
      }
    }

    // 3. Growth Focus of the Day
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = WarmCoralLight,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "💡", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Growth Focus of the Day",
              style = MaterialTheme.typography.labelSmall,
              color = WarmCoralDark,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Creative Thinking & Sensory Curiosity",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = DeepNavyText
            )
            Text(
              text = "Notice what makes your child pause and inspect today.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // 4. Daily Routine Timeline (Next upcoming routines)
    item {
      SectionHeader(
        title = "Daily Routine Timeline",
        subtitle = "Check off as your family flows through the day",
        actionLabel = "Edit Schedule",
        onActionClick = { onNavigate(Screen.Planner) }
      )

      if (routines.isEmpty()) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🗓️", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No routines scheduled yet",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Tap below to populate age-tailored daily routines.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { onNavigate(Screen.Planner) },
              colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
            ) {
              Text("Create Day Routine")
            }
          }
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          routines.take(4).forEach { routine ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (routine.isCompleted) WarmSurface.copy(alpha = 0.6f) else WarmSurface
              ),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleRoutine(routine.id, !routine.isCompleted) }
                .testTag("routine_item_${routine.id}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = routine.isCompleted,
                  onCheckedChange = { checked ->
                    viewModel.toggleRoutine(routine.id, checked)
                  },
                  colors = CheckboxDefaults.colors(checkedColor = BrandTeal)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = routine.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (routine.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${routine.timeStr} • ${routine.category} (${routine.durationMinutes}m)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                if (routine.isCompleted) {
                  Text(text = "✨", fontSize = 16.sp)
                }
              }
            }
          }
        }
      }
    }

    // 5. Recommended Activity
    item {
      SectionHeader(
        title = "Recommended For Today",
        subtitle = "Tailored for ${activeChild?.nickname ?: "your child"}'s age & curiosity",
        actionLabel = "Explore 100+",
        onActionClick = { onNavigate(Screen.Activities) }
      )

      ActivitySummaryCard(
        activity = recommendedActivity,
        isFavorite = favorites.contains(recommendedActivity.id),
        onToggleFavorite = { viewModel.toggleFavorite(recommendedActivity.id) },
        onClick = { onNavigate(Screen.ActivityDetail(recommendedActivity.id)) },
        onAddToPlanner = { viewModel.addActivityToDailyPlan(recommendedActivity) }
      )
    }

    // 6. 30-Day Screen-Free Challenge Banner
    item {
      val completedScreenFreeDays = screenFreeProgress.count { it.isCompleted }
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MintPastel),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigate(Screen.ScreenFree) }
          .testTag("screen_free_banner_card")
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(MintDark),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🌿", fontSize = 26.sp)
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "30-Day Screen-Free Adventure",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1B4D3E)
            )
            Text(
              text = "Less scrolling. More creating & connecting. ($completedScreenFreeDays/30 completed)",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF266350)
            )
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open",
            tint = MintDark
          )
        }
      }
    }

    // 7. Parent Reflection Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("parent_reflection_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "💛", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Parent Reflection of the Day",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "What made ${activeChild?.nickname ?: "your child"} smile today?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = reflectionText,
            onValueChange = {
              reflectionText = it
              reflectionSaved = false
            },
            placeholder = { Text("e.g. He loved building a tall tower and laughed when it wobbled!") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reflection_input"),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            AnimatedVisibility(visible = reflectionSaved) {
              Text(
                text = "✓ Saved to Growth Journal!",
                style = MaterialTheme.typography.bodySmall,
                color = MintDark,
                fontWeight = FontWeight.Bold
              )
            }

            Button(
              onClick = {
                if (reflectionText.isNotBlank()) {
                  viewModel.addJournalEntry(
                    category = "Daily Joy",
                    content = reflectionText,
                    smileMoment = reflectionText,
                    skills = listOf("Joy & Playfulness")
                  )
                  reflectionSaved = true
                  reflectionText = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("save_reflection_button")
            ) {
              Text("Save Reflection")
            }
          }
        }
      }
    }
  }
}
