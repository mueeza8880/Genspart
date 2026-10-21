package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
  onGetStarted: () -> Unit,
  onSignIn: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(WarmIvoryBg)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Spacer(modifier = Modifier.height(20.dp))

    // Logo & Visual Emblem
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(BrandTeal),
        contentAlignment = Alignment.Center
      ) {
        Text("✨", fontSize = 54.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "KIDSPARK",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = BrandTealDark,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Raise Their Potential, One Day at a Time.",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = WarmCoralDark,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "The parent-controlled companion to organize everyday routines, nurture future capabilities, and inspire offline curiosity for ages 1–10.",
        style = MaterialTheme.typography.bodyMedium,
        color = DeepNavyText,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }

    // Value Prop Cards
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      ValueRow("📅", "Smart Daily Routine Planner", "Interactive schedules, habit building, and family flow.")
      ValueRow("🔍", "100+ Age-Based Activities", "Sensory, STEM, language & outdoor play ideas.")
      ValueRow("🌱", "Future Skills Explorer", "10 pathways nurturing curiosity without child rankings.")
    }

    // Action Buttons
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onGetStarted,
        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("welcome_get_started_button")
      ) {
        Text("Get Started", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = onSignIn,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("welcome_sign_in_button")
      ) {
        Text("I Already Have an Account", style = MaterialTheme.typography.titleMedium)
      }
    }
  }
}

@Composable
fun ValueRow(emoji: String, title: String, description: String) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = WarmSurface,
    tonalElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = emoji, fontSize = 24.sp)
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}
