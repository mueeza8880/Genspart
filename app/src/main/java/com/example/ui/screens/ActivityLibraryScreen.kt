package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.components.ActivitySummaryCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityLibraryScreen(
  viewModel: KidsparkViewModel,
  onNavigate: (Screen) -> Unit
) {
  val searchQuery by viewModel.activitySearchQuery.collectAsState()
  val selectedAge by viewModel.selectedAgeFilter.collectAsState()
  val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
  val onlyFavorites by viewModel.onlyFavoritesFilter.collectAsState()
  val activities by viewModel.filteredActivities.collectAsState()
  val favorites by viewModel.favoriteIds.collectAsState()

  val ageFilters = listOf("All", "1–2", "3–4", "5–6", "7–8", "9–10")
  val categories = listOf(
    "All", "Sensory play", "Language development", "Creativity",
    "Logical thinking", "Physical movement", "Social interaction",
    "Emotional awareness", "STEM exploration", "Nature and outdoors",
    "Independent living skills", "Family bonding", "Screen-free play"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Search
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(WarmSurface)
        .padding(16.dp)
    ) {
      Text(
        text = "Age-Based Activity Library",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "100 original play & learning experiences for ages 1–10",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.activitySearchQuery.value = it },
        placeholder = { Text("Search by title, category, or materials...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.activitySearchQuery.value = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("activity_search_bar"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Age Group Filter Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Age:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(ageFilters) { age ->
            val isSelected = age == selectedAge
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectedAgeFilter.value = age },
              label = { Text(age, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = BrandTeal,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Category Filter Row & Favorites Toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = onlyFavorites,
          onClick = { viewModel.onlyFavoritesFilter.value = !onlyFavorites },
          label = { Text("Favorites (${favorites.size})", fontSize = 12.sp) },
          leadingIcon = {
            Icon(
              imageVector = if (onlyFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = if (onlyFavorites) WarmCoral else MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectedCategoryFilter.value = cat },
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
    }

    // Results Count & List
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${activities.size} activities available",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
    }

    if (activities.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "🔍", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No activities match your filters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Try clearing search or choosing another age bracket.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              viewModel.activitySearchQuery.value = ""
              viewModel.selectedAgeFilter.value = "All"
              viewModel.selectedCategoryFilter.value = "All"
              viewModel.onlyFavoritesFilter.value = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
          ) {
            Text("Reset All Filters")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(activities, key = { it.id }) { activity ->
          ActivitySummaryCard(
            activity = activity,
            isFavorite = favorites.contains(activity.id),
            onToggleFavorite = { viewModel.toggleFavorite(activity.id) },
            onClick = { onNavigate(Screen.ActivityDetail(activity.id)) },
            onAddToPlanner = { viewModel.addActivityToDailyPlan(activity) }
          )
        }
      }
    }
  }
}
