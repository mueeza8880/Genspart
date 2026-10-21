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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PathwayDetailScreen(
  pathwayId: String,
  viewModel: KidsparkViewModel,
  onBack: () -> Unit,
  onNavigateToWeeklyPlan: () -> Unit
) {
  val pathway = remember(pathwayId) { viewModel.repository.getPathwayById(pathwayId) }

  // Interactive local checklist state
  val checkedItems = remember { mutableStateMapOf<Int, Boolean>() }

  if (pathway == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Pathway not found")
    }
    return
  }

  val accentColor = remember(pathway.accentColorHex) {
    try {
      Color(android.graphics.Color.parseColor(pathway.accentColorHex))
    } catch (_: Exception) {
      BrandTeal
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(pathway.title, maxLines = 1) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("pathway_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          Button(
            onClick = onNavigateToWeeklyPlan,
            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("pathway_add_to_weekly_plan_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Integrate into 7-Day Plan")
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
      // Hero Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = pathway.iconEmoji, fontSize = 30.sp)
              }

              Spacer(modifier = Modifier.width(16.dp))

              Column {
                Text(
                  text = pathway.title,
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = pathway.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = pathway.description,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = WarmSurfaceVariant
            ) {
              Text(
                text = pathway.ageGuidance,
                style = MaterialTheme.typography.bodySmall,
                color = DeepNavyText,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }

      // Related Everyday Skills
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Related Everyday Capabilities",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              pathway.relatedEverydaySkills.forEach { skill ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = BrandTealLight
                ) {
                  Text(
                    text = "✦ $skill",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandTealDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Projects by Age Bracket
      item {
        Text(
          text = "Suggested Pathway Projects",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      items(pathway.projects) { project ->
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
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = SoftYellowLight
              ) {
                Text(
                  text = project.ageSuitability,
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF6B5100),
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Text(
                text = "⏱️ ${project.estimatedTime}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = project.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = project.description,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Materials: ${project.materials}",
              style = MaterialTheme.typography.bodySmall,
              color = BrandTealDark,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // Parent Reflection Prompts
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmCoralLight)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "💭", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Parent Reflection Questions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = WarmCoralDark
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            pathway.reflectionQuestions.forEach { question ->
              Text(
                text = "• $question",
                style = MaterialTheme.typography.bodySmall,
                color = DeepNavyText,
                modifier = Modifier.padding(vertical = 2.dp)
              )
            }
          }
        }
      }

      // Challenge Checklist
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Optional Challenge Checklist",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Fun, low-stakes micro-challenges for this pathway:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            pathway.challengeChecklist.forEachIndexed { index, challenge ->
              val isDone = checkedItems[index] ?: false
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = isDone,
                  onCheckedChange = { checkedItems[index] = it },
                  colors = CheckboxDefaults.colors(checkedColor = BrandTeal)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = challenge,
                  style = MaterialTheme.typography.bodyMedium,
                  color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }
  }
}
