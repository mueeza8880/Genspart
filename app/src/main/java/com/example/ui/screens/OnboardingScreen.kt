package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
  viewModel: KidsparkViewModel,
  onComplete: () -> Unit,
  onBack: () -> Unit
) {
  var currentStep by remember { mutableStateOf(1) } // 1 to 4

  // State
  var parentName by remember { mutableStateOf("") }
  var language by remember { mutableStateOf("English") }

  var childNickname by remember { mutableStateOf("") }
  var childAge by remember { mutableStateOf("5") }

  val availableInterests = listOf(
    "Building & Blocks", "Nature & Animals", "Drawing & Colors",
    "Music & Rhythm", "Puzzles & Mazes", "Storytelling", "Water & Sensory"
  )
  val selectedInterests = remember { mutableStateListOf<String>() }

  val availablePriorities = listOf(
    "Learning", "Creativity", "Communication", "Healthy routines",
    "Physical play", "Independence", "Social skills", "Emotional awareness", "Problem-solving"
  )
  val selectedPriorities = remember { mutableStateListOf<String>() }

  var wakeTime by remember { mutableStateOf("07:30 AM") }
  var bedtime by remember { mutableStateOf("08:00 PM") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Family Setup (${currentStep}/4)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(
            onClick = {
              if (currentStep > 1) currentStep-- else onBack()
            }
          ) {
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
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (currentStep > 1) {
            OutlinedButton(
              onClick = { currentStep-- },
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Previous")
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          Button(
            onClick = {
              if (currentStep < 4) {
                currentStep++
              } else {
                // Finalize onboarding
                val age = childAge.toIntOrNull()?.coerceIn(1, 10) ?: 5
                viewModel.saveParentSettings(
                  name = parentName.ifBlank { "Parent" },
                  language = language,
                  notifications = true
                )
                viewModel.addNewChild(
                  nickname = childNickname.ifBlank { "Leo" },
                  birthMonth = 6,
                  birthYear = 2026 - age,
                  age = age,
                  interests = selectedInterests.toList(),
                  goals = selectedPriorities.toList()
                )
                onComplete()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("onboarding_next_button")
          ) {
            Text(if (currentStep == 4) "Launch KIDSPARK" else "Continue")
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .padding(20.dp)
    ) {
      LinearProgressIndicator(
        progress = { currentStep / 4f },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp),
        color = BrandTeal,
        trackColor = WarmSurfaceVariant
      )

      Spacer(modifier = Modifier.height(20.dp))

      when (currentStep) {
        1 -> {
          // Parent Profile
          Text(
            text = "Welcome! Let's get to know you",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "KIDSPARK is controlled by adult parents and caregivers.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = parentName,
            onValueChange = { parentName = it },
            label = { Text("Parent Display Name") },
            placeholder = { Text("e.g. Sarah, Alex, Tariq") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("onboarding_parent_name"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Text("Preferred Language", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
            listOf("English", "اردو (Urdu-ready)").forEach { lang ->
              FilterChip(
                selected = language.startsWith(lang.take(4)),
                onClick = { language = lang },
                label = { Text(lang) },
                shape = RoundedCornerShape(8.dp)
              )
            }
          }
        }

        2 -> {
          // Child Profile
          Text(
            text = "Tell us about your child",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "We use nicknames only; never legal names, exact birthdays, or personal addresses.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = childNickname,
            onValueChange = { childNickname = it },
            label = { Text("Child Nickname") },
            placeholder = { Text("e.g. Leo, Maya, Zayn") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("onboarding_child_name"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = childAge,
            onValueChange = { childAge = it },
            label = { Text("Child Age (1 to 10)") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("onboarding_child_age"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text("Favorite Play Interests", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            availableInterests.forEach { interest ->
              val isSelected = selectedInterests.contains(interest)
              FilterChip(
                selected = isSelected,
                onClick = {
                  if (isSelected) selectedInterests.remove(interest) else selectedInterests.add(interest)
                },
                label = { Text(interest, fontSize = 12.sp) }
              )
            }
          }
        }

        3 -> {
          // Family Priorities
          Text(
            text = "Select Family Priorities",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Choose up to 3 focus areas to personalize your daily recommendations.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            availablePriorities.forEach { priority ->
              val isSelected = selectedPriorities.contains(priority)
              FilterChip(
                selected = isSelected,
                onClick = {
                  if (isSelected) selectedPriorities.remove(priority)
                  else if (selectedPriorities.size < 3) selectedPriorities.add(priority)
                },
                label = { Text(priority) },
                shape = RoundedCornerShape(10.dp)
              )
            }
          }
        }

        4 -> {
          // Daily Planning Schedule
          Text(
            text = "Daily Routine Preferences",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Approximate schedule rhythm helps us build your initial day plan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = wakeTime,
            onValueChange = { wakeTime = it },
            label = { Text("Approximate Wake-up Time") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = bedtime,
            onValueChange = { bedtime = it },
            label = { Text("Approximate Bedtime") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(20.dp))

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = BrandTealLight,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("✨", fontSize = 24.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "We will generate a personalized day plan you can edit anytime!",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandTealDark
              )
            }
          }
        }
      }
    }
  }
}
