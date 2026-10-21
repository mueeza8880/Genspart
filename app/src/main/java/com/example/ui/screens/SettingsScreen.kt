package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: KidsparkViewModel,
  onBack: () -> Unit,
  onNavigateToChildren: () -> Unit,
  onNavigateToInsights: () -> Unit
) {
  val parentProfile by viewModel.parentProfile.collectAsState()

  var displayName by remember(parentProfile) { mutableStateOf(parentProfile?.displayName ?: "Parent") }
  var language by remember(parentProfile) { mutableStateOf(parentProfile?.preferredLanguage ?: "English") }
  var notifications by remember(parentProfile) { mutableStateOf(parentProfile?.notificationsEnabled ?: true) }

  var showExportReportDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showPricingDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .padding(16.dp),
      contentPadding = PaddingValues(bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Parent Profile Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Parent Account Profile",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = displayName,
              onValueChange = { displayName = it },
              label = { Text("Parent Display Name") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Preferred Language", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
              listOf("English", "اردو (Urdu-ready)").forEach { lang ->
                FilterChip(
                  selected = language.startsWith(lang.take(4)),
                  onClick = {
                    language = lang
                    viewModel.saveParentSettings(displayName, language, notifications)
                  },
                  label = { Text(lang) },
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { viewModel.saveParentSettings(displayName, language, notifications) },
              colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Save Profile Changes")
            }
          }
        }
      }

      // App Preferences & Features
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Preferences & Family Features",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Notifications toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Gentle In-App Reminders", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("Helpful reminders for planned routines & bedtime reflections", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Switch(
                checked = notifications,
                onCheckedChange = {
                  notifications = it
                  viewModel.saveParentSettings(displayName, language, it)
                },
                colors = SwitchDefaults.colors(checkedThumbColor = BrandTeal, checkedTrackColor = BrandTealContainer)
              )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            SettingsNavRow(
              icon = Icons.Outlined.FamilyRestroom,
              title = "Children Profiles",
              subtitle = "Add, edit, or switch between children",
              onClick = onNavigateToChildren
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            SettingsNavRow(
              icon = Icons.Outlined.BarChart,
              title = "Growth Insights Dashboard",
              subtitle = "View participation summaries & category metrics",
              onClick = onNavigateToInsights
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            SettingsNavRow(
              icon = Icons.Outlined.Download,
              title = "Export Growth Report",
              subtitle = "View printable weekly and monthly summary",
              onClick = { showExportReportDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            SettingsNavRow(
              icon = Icons.Outlined.WorkspacePremium,
              title = "Subscription Tiers (Free, Plus, Family)",
              subtitle = "View membership architecture and plans",
              onClick = { showPricingDialog = true }
            )
          }
        }
      }

      // Privacy, Safety & COPPA Compliance Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WarmSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔒", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Child Privacy & Data Protection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "• Parent-controlled: Designed exclusively for adult parents and caregivers.\n" +
                "• Minimized collection: Uses nicknames only; never requires exact birth dates, child photos, or addresses.\n" +
                "• Zero child advertising trackers: Strictly private and safe from third-party ads.\n" +
                "• Strict data isolation: Your family's journals and routines are stored securely.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
              onClick = { showPrivacyDialog = true },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text("Read Complete Privacy Disclosures →", color = BrandTeal, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Export Data Dialog
  if (showExportReportDialog) {
    AlertDialog(
      onDismissRequest = { showExportReportDialog = false },
      title = { Text("Export Family Growth Summary", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Printable report ready for export:")
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = WarmIvoryBg,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("✦ KIDSPARK Weekly Progress Report", fontWeight = FontWeight.Bold, color = BrandTealDark)
              Text("✦ Active Child: ${viewModel.activeChild.value?.nickname ?: "Leo"}")
              Text("✦ Completed Routines, Habits & Observations")
              Text("✦ Clean, printable parent-ready format")
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.showToast("Report exported successfully!")
            showExportReportDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
        ) {
          Text("Download Summary")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportReportDialog = false }) { Text("Close") }
      }
    )
  }

  // Privacy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("KIDSPARK Privacy & Child Safety Policy") },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "KIDSPARK adheres to the principle of Least-Privilege Data Collection. We collect minimal parent and child details necessary for personalized routine recommendations and habit building.\n\n" +
              "No child data is sold or shared with external advertising networks. Family journal reflections remain completely private to your family.",
            style = MaterialTheme.typography.bodySmall
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showPrivacyDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
        ) {
          Text("Understood")
        }
      }
    )
  }

  // Pricing Dialog
  if (showPricingDialog) {
    AlertDialog(
      onDismissRequest = { showPricingDialog = false },
      title = { Text("KIDSPARK Membership Tiers") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(shape = RoundedCornerShape(8.dp), color = BrandTealLight, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("FREE PLAN", fontWeight = FontWeight.Bold, color = BrandTealDark)
              Text("Single child profile, basic routine planner, curated activity library.", style = MaterialTheme.typography.bodySmall)
            }
          }
          Surface(shape = RoundedCornerShape(8.dp), color = WarmCoralLight, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("PLUS PLAN ($4.99/mo)", fontWeight = FontWeight.Bold, color = WarmCoralDark)
              Text("Multiple children, AI weekly planner, AI coach, printable growth reports.", style = MaterialTheme.typography.bodySmall)
            }
          }
          Text("All features are unlocked in this build for full developer testing.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
      confirmButton = {
        Button(
          onClick = { showPricingDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
        ) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
fun SettingsNavRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
  }
}
