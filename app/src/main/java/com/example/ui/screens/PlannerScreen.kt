package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.RoutineItem
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlannerScreen(
  viewModel: KidsparkViewModel,
  onNavigate: (Screen) -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val routines by viewModel.routines.collectAsState()

  var selectedCategory by remember { mutableStateOf("All") }
  var showAddDialog by remember { mutableStateOf(false) }
  var editingRoutine by remember { mutableStateOf<RoutineItem?>(null) }

  val categories = listOf(
    "All", "Morning", "Meals", "Learning", "Free play",
    "Physical movement", "Outdoor activities", "Quiet time",
    "Family connection", "Chores", "Bedtime"
  )

  val filteredRoutines = remember(routines, selectedCategory) {
    if (selectedCategory == "All") routines
    else routines.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  val completedCount = routines.count { it.isCompleted }
  val totalCount = routines.size

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = BrandTeal,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("add_routine_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Routine")
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      // Header Info
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(WarmSurface)
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Daily Routine Planner",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "For ${activeChild?.nickname ?: "Child"} • $completedCount of $totalCount completed",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          FilledTonalButton(
            onClick = { onNavigate(Screen.WeeklyPlan) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = BrandTealLight)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandTealDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("AI Plan", color = BrandTealDark, style = MaterialTheme.typography.labelSmall)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = cat },
              label = { Text(cat) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = BrandTeal,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // Routines List
      if (filteredRoutines.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🗓️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No routines in this category",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Tap the + button to schedule an activity or reset filter.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
              onClick = { selectedCategory = "All" }
            ) {
              Text("Show All Routines")
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
          contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredRoutines, key = { it.id }) { routine ->
            RoutineCard(
              routine = routine,
              onToggleComplete = { viewModel.toggleRoutine(routine.id, !routine.isCompleted) },
              onEdit = { editingRoutine = routine },
              onDelete = { viewModel.deleteRoutine(routine.id) }
            )
          }
        }
      }
    }
  }

  // Add / Edit Routine Dialog
  if (showAddDialog || editingRoutine != null) {
    val initial = editingRoutine
    RoutineDialog(
      initial = initial,
      onDismiss = {
        showAddDialog = false
        editingRoutine = null
      },
      onSave = { title, category, timeStr, duration, notes ->
        if (initial != null) {
          viewModel.updateRoutine(
            initial.copy(
              title = title,
              category = category,
              timeStr = timeStr,
              durationMinutes = duration,
              notes = notes
            )
          )
        } else {
          viewModel.addRoutine(title, category, timeStr, duration, notes)
        }
        showAddDialog = false
        editingRoutine = null
      }
    )
  }
}

@Composable
fun RoutineCard(
  routine: RoutineItem,
  onToggleComplete: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val categoryColor = when (routine.category.lowercase()) {
    "morning" -> SoftYellowLight
    "meals" -> WarmCoralLight
    "learning" -> BrandTealLight
    "free play" -> LavenderPastel
    "physical movement" -> MintPastel
    "quiet time" -> LightBluePastel
    "bedtime" -> Color(0xFFE8EAF6)
    else -> WarmSurfaceVariant
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (routine.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("routine_card_${routine.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = routine.isCompleted,
        onCheckedChange = { onToggleComplete() },
        colors = CheckboxDefaults.colors(checkedColor = BrandTeal),
        modifier = Modifier.testTag("check_${routine.id}")
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = categoryColor,
            modifier = Modifier.padding(end = 6.dp)
          ) {
            Text(
              text = routine.category,
              style = MaterialTheme.typography.labelSmall,
              color = DeepNavyText,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = routine.timeStr,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = BrandTealDark
          )
          Text(
            text = " (${routine.durationMinutes}m)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = routine.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = if (routine.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )

        if (routine.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = routine.notes,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
          )
        }
      }

      Row {
        IconButton(
          onClick = onEdit,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = "Edit",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "Delete",
            tint = WarmCoralDark,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun RoutineDialog(
  initial: RoutineItem? = null,
  onDismiss: () -> Unit,
  onSave: (title: String, category: String, time: String, duration: Int, notes: String) -> Unit
) {
  var title by remember { mutableStateOf(initial?.title ?: "") }
  var category by remember { mutableStateOf(initial?.category ?: "Learning") }
  var timeStr by remember { mutableStateOf(initial?.timeStr ?: "09:30 AM") }
  var duration by remember { mutableStateOf(initial?.durationMinutes?.toString() ?: "30") }
  var notes by remember { mutableStateOf(initial?.notes ?: "") }

  val categories = listOf(
    "Morning", "Meals", "Learning", "Free play",
    "Physical movement", "Outdoor activities", "Quiet time",
    "Family connection", "Chores", "Bedtime"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (initial != null) "Edit Routine" else "Add Custom Routine",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Activity Title") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_routine_title"),
          shape = RoundedCornerShape(12.dp)
        )

        Text("Category", style = MaterialTheme.typography.labelSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 12.sp) }
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = timeStr,
            onValueChange = { timeStr = it },
            label = { Text("Time (e.g. 10:00 AM)") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Minutes") },
            modifier = Modifier.width(100.dp),
            shape = RoundedCornerShape(12.dp)
          )
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes / Materials (optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSave(
              title,
              category,
              timeStr,
              duration.toIntOrNull() ?: 30,
              notes
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("dialog_save_routine")
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
