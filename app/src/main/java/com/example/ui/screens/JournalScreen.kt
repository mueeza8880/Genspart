package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.JournalEntry
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JournalScreen(
  viewModel: KidsparkViewModel,
  onNavigate: (Screen) -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val entries by viewModel.journalEntries.collectAsState()

  var selectedCategory by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

  val categories = listOf("All", "Daily Joy", "Creative Moment", "Curious Question", "Milestone", "Kindness")

  val filteredEntries = remember(entries, selectedCategory, searchQuery) {
    entries.filter { entry ->
      val matchesCat = selectedCategory == "All" || entry.category.equals(selectedCategory, ignoreCase = true)
      val matchesQuery = searchQuery.isBlank() ||
        entry.content.contains(searchQuery, ignoreCase = true) ||
        entry.smileMoment.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery
    }
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = WarmCoral,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("add_journal_entry_fab")
      ) {
        Icon(Icons.Default.Edit, contentDescription = "New Entry")
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      // Header & Navigation Tabs between Growth tools
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
              text = "Child Growth Journal",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Private parent reflections for ${activeChild?.nickname ?: "Child"}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row {
            FilledTonalButton(
              onClick = { onNavigate(Screen.Habits) },
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Habits ✨", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
            FilledTonalButton(
              onClick = { onNavigate(Screen.ScreenFree) },
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("30-Day 🌿", fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search memories, questions, skills...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = WarmCoral,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }

      // Entries List
      if (filteredEntries.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📖", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No reflections recorded yet",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Tap the pen button below to write your first reflection.",
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
          items(filteredEntries, key = { it.id }) { entry ->
            JournalCard(
              entry = entry,
              onDelete = { viewModel.deleteJournalEntry(entry.id) }
            )
          }
        }
      }
    }
  }

  // Add Journal Entry Dialog
  if (showAddDialog) {
    AddJournalDialog(
      onDismiss = { showAddDialog = false },
      onSave = { category, content, smileMoment, skills ->
        viewModel.addJournalEntry(category, content, smileMoment, skills)
        showAddDialog = false
      }
    )
  }
}

@Composable
fun JournalCard(
  entry: JournalEntry,
  onDelete: () -> Unit
) {
  val badgeColor = when (entry.category) {
    "Daily Joy" -> SoftYellowLight
    "Creative Moment" -> LavenderPastel
    "Curious Question" -> LightBluePastel
    "Milestone" -> MintPastel
    "Kindness" -> WarmCoralLight
    else -> WarmSurfaceVariant
  }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = badgeColor
          ) {
            Text(
              text = entry.category,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = DeepNavyText,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Text(
            text = entry.date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = entry.content,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
      )

      if (entry.smileMoment.isNotBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = WarmIvoryBg
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("😊", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Smile Moment: ${entry.smileMoment}",
              style = MaterialTheme.typography.bodySmall,
              color = BrandTealDark,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      if (entry.skillsNoticed.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          entry.skillsNoticed.forEach { skill ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = BrandTealLight
            ) {
              Text(
                text = "✦ $skill",
                style = MaterialTheme.typography.labelSmall,
                color = BrandTealDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddJournalDialog(
  onDismiss: () -> Unit,
  onSave: (category: String, content: String, smileMoment: String, skills: List<String>) -> Unit
) {
  var category by remember { mutableStateOf("Daily Joy") }
  var content by remember { mutableStateOf("") }
  var smileMoment by remember { mutableStateOf("") }
  var skillsText by remember { mutableStateOf("") }

  val categories = listOf("Daily Joy", "Creative Moment", "Curious Question", "Milestone", "Kindness")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Write Growth Reflection", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
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

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Observation / Story") },
          placeholder = { Text("What happened today? What did you notice?") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("journal_content_input"),
          maxLines = 4,
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = smileMoment,
          onValueChange = { smileMoment = it },
          label = { Text("What made them smile?") },
          placeholder = { Text("e.g. Splashing in puddles in yellow boots") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = skillsText,
          onValueChange = { skillsText = it },
          label = { Text("Emerging Skills (comma-separated)") },
          placeholder = { Text("e.g. Patience, Sharing, Balance") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (content.isNotBlank()) {
            val skills = skillsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            onSave(category, content, smileMoment, skills)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = WarmCoral),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("save_journal_entry_button")
      ) {
        Text("Save Reflection")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
