package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ui.ChatMessage
import com.example.ui.KidsparkViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AICoachScreen(
  viewModel: KidsparkViewModel,
  onBack: () -> Unit
) {
  val messages by viewModel.chatMessages.collectAsState()
  val isLoading by viewModel.isAiLoading.collectAsState()
  val activeChild by viewModel.activeChild.collectAsState()

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  val quickPrompts = listOf(
    "Plan a rainy afternoon without screens",
    "Fun science experiment using household items",
    "Storytelling activity for bedtime",
    "How to encourage tidying up without battles",
    "Quick 15-minute sensory play idea"
  )

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BrandTealContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandTealDark, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("KIDSPARK AI Coach", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              Text("Guiding for ${activeChild?.nickname ?: "Child"} (Age ${activeChild?.approximateAge ?: 5})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("ai_coach_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.imePadding().navigationBarsPadding()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          // Quick suggestion chips
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
          ) {
            items(quickPrompts) { prompt ->
              SuggestionChip(
                onClick = { viewModel.sendCoachPrompt(prompt) },
                label = { Text(prompt, fontSize = 12.sp) },
                shape = RoundedCornerShape(12.dp)
              )
            }
          }

          // Input field and send button
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              placeholder = { Text("Ask for activity ideas, advice, schedules...") },
              modifier = Modifier
                .weight(1f)
                .testTag("ai_coach_input"),
              shape = RoundedCornerShape(20.dp),
              maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = {
                if (inputText.isNotBlank() && !isLoading) {
                  viewModel.sendCoachPrompt(inputText)
                  inputText = ""
                }
              },
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(BrandTeal)
                .testTag("ai_coach_send_button"),
              enabled = inputText.isNotBlank() && !isLoading
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        CoachBubble(
          message = msg,
          onAddToPlanner = {
            activeChild?.let { child ->
              viewModel.addRoutine(
                title = "AI Coach Idea for ${child.nickname}",
                category = "Learning",
                timeStr = "11:00 AM",
                duration = 30,
                notes = msg.text.take(120)
              )
            }
          }
        )
      }

      if (isLoading) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BrandTeal, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "KIDSPARK Coach is formulating thoughtful suggestions...",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun CoachBubble(
  message: ChatMessage,
  onAddToPlanner: () -> Unit
) {
  val isUser = message.isUser

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (isUser) 18.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 18.dp
      ),
      color = if (isUser) BrandTeal else WarmSurface,
      tonalElevation = if (isUser) 0.dp else 2.dp,
      modifier = Modifier.widthIn(max = 320.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = message.text,
          style = MaterialTheme.typography.bodyMedium,
          color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
        )

        // If from bot and contains activity details, provide "Add to Planner" action
        if (!isUser && message.text.contains("Step")) {
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = onAddToPlanner,
            colors = ButtonDefaults.buttonColors(containerColor = BrandTealContainer),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = BrandTealDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add to Planner", style = MaterialTheme.typography.labelSmall, color = BrandTealDark, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
