package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.ScreenFreeMission
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun ScreenFreeScreen(
  viewModel: KidsparkViewModel,
  onNavigate: (Screen) -> Unit
) {
  val missions = viewModel.screenFreeMissions
  val progressList by viewModel.screenFreeProgress.collectAsState()
  val activeChild by viewModel.activeChild.collectAsState()

  val completedMap = remember(progressList) {
    progressList.associate { it.dayNumber to it.isCompleted }
  }

  val completedCount = completedMap.values.count { it }
  val progressRatio = completedCount.toFloat() / 30f

  var selectedMissionForModal by remember { mutableStateOf<ScreenFreeMission?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Hero Header with Progress
    Card(
      shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
      colors = CardDefaults.cardColors(containerColor = MintPastel),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "30-Day Screen-Free Adventure",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1B4D3E)
            )
            Text(
              text = "Less scrolling. More playing, creating & connecting.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF2E7D5B)
            )
          }

          if (completedCount >= 1) {
            FilledTonalButton(
              onClick = { onNavigate(Screen.Certificate) },
              colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Certificate 🏆", fontSize = 12.sp, color = MintDark, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "$completedCount of 30 Missions Completed",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B4D3E)
          )
          Text(
            text = "${(progressRatio * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B4D3E)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { progressRatio },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
          color = MintDark,
          trackColor = Color.White.copy(alpha = 0.6f)
        )
      }
    }

    // Missions List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(missions, key = { it.day }) { mission ->
        val isCompleted = completedMap[mission.day] ?: false
        ScreenFreeMissionCard(
          mission = mission,
          isCompleted = isCompleted,
          onToggleComplete = { viewModel.toggleScreenFreeDay(mission.day, !isCompleted) },
          onClick = { selectedMissionForModal = mission },
          onAddToPlanner = { viewModel.addScreenFreeDayToPlanner(mission) }
        )
      }
    }
  }

  // Mission Detail Modal
  selectedMissionForModal?.let { mission ->
    val isCompleted = completedMap[mission.day] ?: false
    AlertDialog(
      onDismissRequest = { selectedMissionForModal = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MintPastel,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("Day ${mission.day}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MintDark)
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(mission.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Objective: ${mission.objective}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
          )

          Text(
            text = "Materials: ${mission.materials.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text("Instructions:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          mission.instructions.forEachIndexed { i, step ->
            Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall)
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = WarmIvoryBg,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Parent Reflection:", style = MaterialTheme.typography.labelSmall, color = WarmCoralDark, fontWeight = FontWeight.Bold)
              Text(mission.parentReflectionQuestion, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      },
      confirmButton = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = {
              viewModel.addScreenFreeDayToPlanner(mission)
              selectedMissionForModal = null
            }
          ) {
            Text("Add to Plan")
          }

          Button(
            onClick = {
              viewModel.toggleScreenFreeDay(mission.day, !isCompleted)
              selectedMissionForModal = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = MintDark)
          ) {
            Text(if (isCompleted) "Mark Incomplete" else "Mark Done ✓")
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedMissionForModal = null }) { Text("Close") }
      }
    )
  }
}

@Composable
fun ScreenFreeMissionCard(
  mission: ScreenFreeMission,
  isCompleted: Boolean,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit,
  onAddToPlanner: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCompleted) MintPastel.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("mission_card_${mission.day}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = if (isCompleted) MintDark else MintPastel,
            modifier = Modifier.size(34.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "${mission.day}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) Color.White else MintDark
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = mission.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${mission.theme} • ⏱️ ${mission.duration}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Checkbox(
          checked = isCompleted,
          onCheckedChange = { onToggleComplete() },
          colors = CheckboxDefaults.colors(checkedColor = MintDark),
          modifier = Modifier.testTag("check_mission_${mission.day}")
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = mission.objective,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = onClick,
          contentPadding = PaddingValues(0.dp)
        ) {
          Text("Read Full Instructions →", style = MaterialTheme.typography.labelSmall, color = MintDark, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onAddToPlanner,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add to Plan", style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}
