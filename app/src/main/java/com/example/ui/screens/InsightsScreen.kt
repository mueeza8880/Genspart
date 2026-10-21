package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
  viewModel: KidsparkViewModel,
  onBack: () -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val routines by viewModel.routines.collectAsState()
  val habits by viewModel.habits.collectAsState()
  val journalEntries by viewModel.journalEntries.collectAsState()
  val activityLogs by viewModel.activityLogs.collectAsState()
  val screenFreeProgress by viewModel.screenFreeProgress.collectAsState()

  val completedRoutines = routines.count { it.isCompleted }
  val totalRoutines = routines.size
  val habitsCount = habits.size
  val journalCount = journalEntries.size
  val screenFreeCompleted = screenFreeProgress.count { it.isCompleted }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Parent Growth Insights", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("insights_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Transparency Notice
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = WarmSurfaceVariant
        ) {
          Text(
            text = "ℹ️ Note: These metrics reflect household participation and parent observations only. KIDSPARK never ranks or compares children.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      // Summary Metric Cards Grid
      item {
        Text(
          text = "Participation Overview for ${activeChild?.nickname ?: "Child"}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricBox(
            modifier = Modifier.weight(1f),
            emoji = "🗓️",
            count = "$completedRoutines / $totalRoutines",
            label = "Routines Today",
            color = BrandTealLight,
            textColor = BrandTealDark
          )

          MetricBox(
            modifier = Modifier.weight(1f),
            emoji = "📖",
            count = "$journalCount",
            label = "Reflections Saved",
            color = WarmCoralLight,
            textColor = WarmCoralDark
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricBox(
            modifier = Modifier.weight(1f),
            emoji = "✨",
            count = "$habitsCount",
            label = "Active Habits",
            color = SoftYellowLight,
            textColor = Color(0xFF6B5100)
          )

          MetricBox(
            modifier = Modifier.weight(1f),
            emoji = "🌿",
            count = "$screenFreeCompleted / 30",
            label = "Screen-Free Days",
            color = MintPastel,
            textColor = MintDark
          )
        }
      }

      // Activity Categories Breakdown
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Exploration by Category",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Diverse experiences nurture well-rounded confidence",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            CategoryBar("Creative Arts & Building", 0.85f, WarmCoral)
            Spacer(modifier = Modifier.height(10.dp))
            CategoryBar("Movement & Active Play", 0.70f, MintDark)
            Spacer(modifier = Modifier.height(10.dp))
            CategoryBar("Language & Storytelling", 0.90f, Color(0xFF6B4E9E))
            Spacer(modifier = Modifier.height(10.dp))
            CategoryBar("Nature & STEM Exploration", 0.65f, BrandTeal)
            Spacer(modifier = Modifier.height(10.dp))
            CategoryBar("Emotional Awareness & Kindness", 0.80f, SoftYellowDark)
          }
        }
      }

      // Recent Reflection Highlights
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Recent Parent Journal Observations",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (journalEntries.isEmpty()) {
              Text(
                text = "No reflections yet. Add a quick note in the Growth Journal!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            } else {
              journalEntries.take(3).forEach { entry ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Text("✦", color = BrandTeal, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = entry.content,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 2
                    )
                    Text(
                      text = "${entry.category} • ${entry.date}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

@Composable
fun MetricBox(
  modifier: Modifier = Modifier,
  emoji: String,
  count: String,
  label: String,
  color: Color,
  textColor: Color
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = color,
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = emoji, fontSize = 24.sp)
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = count,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = textColor
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor.copy(alpha = 0.85f),
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun CategoryBar(
  label: String,
  fraction: Float,
  color: Color
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
      Text(text = "${(fraction * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { fraction },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = color,
      trackColor = WarmSurfaceVariant
    )
  }
}
