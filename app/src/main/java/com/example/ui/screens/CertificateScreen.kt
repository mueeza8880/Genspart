package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KidsparkViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
  viewModel: KidsparkViewModel,
  onBack: () -> Unit
) {
  val activeChild by viewModel.activeChild.collectAsState()
  val today = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Adventure Certificate") },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("certificate_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(WarmIvoryBg)
        .padding(innerPadding)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Beautiful Printable Certificate Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(4.dp, SoftYellow, RoundedCornerShape(20.dp))
          .padding(4.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("🏆", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "KIDSPARK OFFICIAL CERTIFICATE",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = BrandTealDark,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Master of Screen-Free Adventures",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DeepNavyText,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "This certificate honors",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = activeChild?.nickname ?: "Explorer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = WarmCoralDark,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "For showing curiosity, creative imagination, resilience, and joyful spirit across the 30-Day Offline Journey.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = DeepNavyText
          )

          Spacer(modifier = Modifier.height(16.dp))

          HorizontalDivider(color = OutlineBorder)

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(today, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
              Text("Awarded Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text("⭐ KIDSPARK ⭐", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = BrandTeal)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Verified 🌟", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MintDark)
              Text("Parent Verified", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = { viewModel.showToast("Certificate saved to gallery / print queue!") },
        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
      ) {
        Icon(Icons.Default.Download, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save / Print Certificate")
      }
    }
  }
}
