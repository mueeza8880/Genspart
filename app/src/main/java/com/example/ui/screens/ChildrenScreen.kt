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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.ChildProfile
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChildrenScreen(
  viewModel: KidsparkViewModel,
  onBack: () -> Unit
) {
  val allChildren by viewModel.allChildren.collectAsState()
  val activeChild by viewModel.activeChild.collectAsState()

  var showAddChildDialog by remember { mutableStateOf(false) }
  var childToDelete by remember { mutableStateOf<ChildProfile?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Family Profiles", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("children_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddChildDialog = true },
        containerColor = BrandTeal,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("add_child_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Child")
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .padding(16.dp)
    ) {
      Text(
        text = "Manage Children Profiles",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Each child has their own private daily routines, journal reflections, and activity progress.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(allChildren, key = { it.id }) { child ->
          val isActive = child.id == activeChild?.id

          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isActive) BrandTealContainer.copy(alpha = 0.5f) else WarmSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 3.dp else 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.switchActiveChild(child.id) }
              .testTag("child_card_${child.id}")
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(50.dp)
                      .clip(CircleShape)
                      .background(BrandTeal),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = child.avatarEmoji, fontSize = 26.sp)
                  }

                  Spacer(modifier = Modifier.width(14.dp))

                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = child.nickname,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = BrandTeal
                        ) {
                          Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                    Text(
                      text = "${child.approximateAge} years old (Born ${child.birthMonth}/${child.birthYear})",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                if (allChildren.size > 1) {
                  IconButton(onClick = { childToDelete = child }) {
                    Icon(
                      imageVector = Icons.Outlined.Delete,
                      contentDescription = "Delete Profile",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              if (child.selectedInterests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  child.selectedInterests.take(3).forEach { interest ->
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = SoftYellowLight
                    ) {
                      Text(
                        text = interest,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B5100),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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

  // Add Child Dialog
  if (showAddChildDialog) {
    AddChildDialog(
      onDismiss = { showAddChildDialog = false },
      onSave = { name, month, year, age, interests, goals ->
        viewModel.addNewChild(name, month, year, age, interests, goals)
        showAddChildDialog = false
      }
    )
  }

  // Confirm Delete Dialog
  childToDelete?.let { child ->
    AlertDialog(
      onDismissRequest = { childToDelete = null },
      title = { Text("Delete Profile for ${child.nickname}?") },
      text = {
        Text("This will remove all associated daily routines, journal reflections, and habit tracking for ${child.nickname}.")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteChild(child.id)
            childToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = WarmCoralDark)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { childToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddChildDialog(
  onDismiss: () -> Unit,
  onSave: (name: String, month: Int, year: Int, age: Int, interests: List<String>, goals: List<String>) -> Unit
) {
  var nickname by remember { mutableStateOf("") }
  var ageText by remember { mutableStateOf("5") }

  val availableInterests = listOf(
    "Building & Blocks", "Nature & Animals", "Drawing & Colors",
    "Music & Rhythm", "Puzzles & Mazes", "Storytelling", "Water & Sensory"
  )
  val selectedInterests = remember { mutableStateListOf<String>() }

  val availableGoals = listOf(
    "Creativity", "Emotional awareness", "Problem-solving",
    "Independence", "Physical play", "Healthy routines"
  )
  val selectedGoals = remember { mutableStateListOf<String>() }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Child Profile", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = nickname,
          onValueChange = { nickname = it },
          label = { Text("Child's Nickname (not legal name)") },
          placeholder = { Text("e.g. Leo, Maya, Zayn") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_child_name"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = ageText,
          onValueChange = { ageText = it },
          label = { Text("Approximate Age (1–10)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_child_age"),
          shape = RoundedCornerShape(12.dp)
        )

        Text("Interests (tap to select)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          availableInterests.forEach { item ->
            val isSelected = selectedInterests.contains(item)
            FilterChip(
              selected = isSelected,
              onClick = {
                if (isSelected) selectedInterests.remove(item) else selectedInterests.add(item)
              },
              label = { Text(item, fontSize = 11.sp) }
            )
          }
        }

        Text("Parent Goals (up to 3)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          availableGoals.forEach { goal ->
            val isSelected = selectedGoals.contains(goal)
            FilterChip(
              selected = isSelected,
              onClick = {
                if (isSelected) selectedGoals.remove(goal)
                else if (selectedGoals.size < 3) selectedGoals.add(goal)
              },
              label = { Text(goal, fontSize = 11.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (nickname.isNotBlank()) {
            val age = ageText.toIntOrNull()?.coerceIn(1, 10) ?: 5
            onSave(nickname, 6, 2026 - age, age, selectedInterests.toList(), selectedGoals.toList())
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("dialog_save_child_button")
      ) {
        Text("Save Child")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
