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
import com.example.data.model.ChildHabit
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HabitTrackerScreen(
  viewModel: KidsparkViewModel,
  onNavigate: (Screen) -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val habits by viewModel.habits.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }

  val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = SoftYellowDark,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("add_habit_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Habit")
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      // Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(WarmSurface)
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Healthy Habit Builder",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Gentle everyday routines for ${activeChild?.nickname ?: "Child"} (no pressure or shame)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          OutlinedButton(
            onClick = { onNavigate(Screen.Journal) },
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Journal", fontSize = 12.sp)
          }
        }
      }

      // Habits list
      if (habits.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "✨", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No habits tracked yet",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Tap + to add habits like reading, brushing teeth, or tidying toys.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
          contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(habits, key = { it.id }) { habit ->
            val isDoneToday = habit.completedDates.contains(today)
            HabitCard(
              habit = habit,
              isDoneToday = isDoneToday,
              onToggleToday = { viewModel.toggleHabitToday(habit) },
              onDelete = { viewModel.deleteHabit(habit.id) }
            )
          }
        }
      }
    }
  }

  if (showAddDialog) {
    AddHabitDialog(
      onDismiss = { showAddDialog = false },
      onSave = { title, category, icon, frequency, note ->
        viewModel.addHabit(title, category, icon, frequency, note)
        showAddDialog = false
      }
    )
  }
}

@Composable
fun HabitCard(
  habit: ChildHabit,
  isDoneToday: Boolean,
  onToggleToday: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDoneToday) MintPastel.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("habit_card_${habit.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(if (isDoneToday) MintDark.copy(alpha = 0.2f) else SoftYellowLight),
        contentAlignment = Alignment.Center
      ) {
        Text(text = habit.iconEmoji, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = habit.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${habit.category} • ${habit.frequency} • ${habit.completedDates.size} days logged",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (habit.encouragementNote.isNotBlank()) {
          Text(
            text = "💛 ${habit.encouragementNote}",
            style = MaterialTheme.typography.labelSmall,
            color = BrandTealDark
          )
        }
      }

      IconButton(
        onClick = onToggleToday,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(if (isDoneToday) MintDark else OutlineBorder.copy(alpha = 0.5f))
          .testTag("toggle_habit_${habit.id}")
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Check Habit",
          tint = if (isDoneToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Outlined.Delete,
          contentDescription = "Delete",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun AddHabitDialog(
  onDismiss: () -> Unit,
  onSave: (title: String, category: String, icon: String, frequency: String, note: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Daily Care") }
  var icon by remember { mutableStateOf("✨") }
  var frequency by remember { mutableStateOf("Daily") }
  var note by remember { mutableStateOf("Super proud of you!") }

  val icons = listOf("🪥", "🧸", "📖", "💧", "🌳", "🍎", "🎨", "🤝", "🛏️")
  val categories = listOf("Daily Care", "Responsibility", "Learning", "Health", "Kindness", "Movement")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Habit Goal", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Habit Name") },
          placeholder = { Text("e.g. Tidying blocks, Drinking water") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Text("Pick an Icon", style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          icons.forEach { ic ->
            Surface(
              shape = CircleShape,
              color = if (icon == ic) SoftYellow else WarmSurfaceVariant,
              modifier = Modifier
                .size(36.dp)
                .clickable { icon = ic }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = ic, fontSize = 18.sp)
              }
            }
          }
        }

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Encouragement Note") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSave(title, category, icon, frequency, note)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = SoftYellowDark),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Save Habit")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
