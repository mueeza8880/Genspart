package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityDetailScreen(
  activityId: String,
  viewModel: KidsparkViewModel,
  onBack: () -> Unit
) {
  val activity = remember(activityId) { viewModel.repository.getActivityById(activityId) }
  val favorites by viewModel.favoriteIds.collectAsState()
  val isFavorite = favorites.contains(activityId)

  var showLogCompleteDialog by remember { mutableStateOf(false) }

  if (activity == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Activity not found")
    }
    return
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(activity.title, maxLines = 1) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.toggleFavorite(activityId) }) {
            Icon(
              imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = "Favorite",
              tint = if (isFavorite) WarmCoral else MaterialTheme.colorScheme.onSurface
            )
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
            onClick = { viewModel.addActivityToDailyPlan(activity) },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_add_to_planner_button"),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add to Plan")
          }

          Button(
            onClick = { showLogCompleteDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_mark_completed_button"),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Log Done")
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
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Tags Header
      item {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandTealContainer
          ) {
            Text(
              text = activity.ageRange,
              style = MaterialTheme.typography.labelSmall,
              color = BrandTealDark,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = WarmCoralLight
          ) {
            Text(
              text = activity.category,
              style = MaterialTheme.typography.labelSmall,
              color = WarmCoralDark,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = SoftYellowLight
          ) {
            Text(
              text = "⏱️ ${activity.estimatedDuration}",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF6B5100),
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MintPastel
          ) {
            Text(
              text = if (activity.isOutdoor) "🌳 Outdoor" else "🏠 Indoor",
              style = MaterialTheme.typography.labelSmall,
              color = MintDark,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }
      }

      // Title & Overview
      item {
        Text(
          text = activity.title,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Parent Participation Level: ${activity.parentParticipationLevel}",
              style = MaterialTheme.typography.labelMedium,
              color = BrandTealDark,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = activity.setupInstructions,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Required Materials
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "📦", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Materials Needed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            activity.requiredMaterials.forEach { material ->
              Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(BrandTeal)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = material,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      // Step-by-Step Directions
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "📋", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Step-by-Step Directions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            activity.stepByStep.forEachIndexed { index, step ->
              Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top
              ) {
                Surface(
                  shape = CircleShape,
                  color = BrandTealLight,
                  modifier = Modifier.size(26.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${index + 1}",
                      style = MaterialTheme.typography.labelSmall,
                      color = BrandTealDark,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = step,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }

      // Developmental Areas Supported
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🌱", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Areas Supported",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              activity.developmentalAreas.forEach { area ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = LightBluePastel
                ) {
                  Text(
                    text = "✓ $area",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightBlueDark,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Variations
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSurface),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "🌱 Easier Variation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MintDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = activity.easierVariation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSurface),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "🚀 Challenge Step",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WarmCoralDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = activity.challengingVariation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Safety Considerations
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFFFFF3E0),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Text(text = "🛡️", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Safety Considerations",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = activity.safetyNotes,
                style = MaterialTheme.typography.bodySmall,
                color = DeepNavyText
              )
            }
          }
        }
      }
    }
  }

  // Log Activity Complete Dialog
  if (showLogCompleteDialog) {
    var rating by remember { mutableStateOf(5) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showLogCompleteDialog = false },
      title = { Text("Log Activity Completed", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "How did your child enjoy '${activity.title}'?",
            style = MaterialTheme.typography.bodyMedium
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            (1..5).forEach { star ->
              IconButton(onClick = { rating = star }) {
                Text(
                  text = if (star <= rating) "⭐" else "☆",
                  fontSize = 24.sp
                )
              }
            }
          }

          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Parent Observation Notes (optional)") },
            placeholder = { Text("What did you notice? Any new strengths?") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.logActivityComplete(activity, rating, notes)
            showLogCompleteDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Save to Record")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogCompleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
