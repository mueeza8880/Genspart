package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
  viewModel: KidsparkViewModel,
  onBack: () -> Unit,
  onNavigateToPlanner: () -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val weeklyPlan by viewModel.weeklyPlan.collectAsState()
  val isLoading by viewModel.isAiLoading.collectAsState()

  var selectedTime by remember { mutableStateOf(60) }
  var outdoorPreference by remember { mutableStateOf("Balanced") }

  // Auto-generate if not generated yet
  LaunchedEffect(Unit) {
    if (weeklyPlan == null) {
      viewModel.generateWeeklyPlan(selectedTime, outdoorPreference)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("AI 7-Day Weekly Planner", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("weekly_plan_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.generateWeeklyPlan(selectedTime, outdoorPreference) },
            enabled = !isLoading
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = { viewModel.generateWeeklyPlan(selectedTime, outdoorPreference) },
            modifier = Modifier
              .weight(1f)
              .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Regenerate")
          }

          Button(
            onClick = {
              viewModel.applyWeeklyPlanToPlanner()
              onNavigateToPlanner()
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("apply_weekly_plan_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Accept Plan")
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Configuration Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Personalized for ${activeChild?.nickname ?: "Child"} (Age ${activeChild?.approximateAge ?: 5})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Focus: Balanced Learning, Gross Motor Movement, and Creative Play without over-scheduling.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Daily Available Time:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
              listOf(30, 45, 60, 90).forEach { mins ->
                FilterChip(
                  selected = selectedTime == mins,
                  onClick = {
                    selectedTime = mins
                    viewModel.generateWeeklyPlan(mins, outdoorPreference)
                  },
                  label = { Text("$mins mins") },
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }
          }
        }
      }

      if (isLoading) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(color = BrandTeal)
              Spacer(modifier = Modifier.height(12.dp))
              Text("Synthesizing age-tailored weekly schedule...", style = MaterialTheme.typography.bodyMedium)
            }
          }
        }
      } else {
        weeklyPlan?.let { plan ->
          items(plan.days) { day ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = WarmSurface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = day.dayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandTealDark
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandTealLight
                  ) {
                    Text(
                      text = "~${day.estimatedTotalMinutes} mins total",
                      style = MaterialTheme.typography.labelSmall,
                      color = BrandTealDark,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Learning
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("🔬", fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("Learning through Play", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(day.learningActivity, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Movement
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("🏃", fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("Movement & Outdoors", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(day.movementActivity, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Creative
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("🎨", fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("Creative & Family Connection", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(day.creativeActivity, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
