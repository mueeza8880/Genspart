package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityCatalogItem
import com.example.data.model.ChildProfile
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun KidsparkTopBar(
  parentName: String,
  activeChild: ChildProfile?,
  allChildren: List<ChildProfile>,
  onSwitchChild: (String) -> Unit,
  onAddChildClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onAiCoachClick: () -> Unit
) {
  var showChildDropdown by remember { mutableStateOf(false) }

  Surface(
    color = MaterialTheme.colorScheme.background,
    tonalElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Hi, $parentName! 👋",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onBackground,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Small moments make a big difference",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // AI Coach quick action button
        IconButton(
          onClick = onAiCoachClick,
          modifier = Modifier
            .size(40.dp)
            .background(BrandTealContainer, CircleShape)
            .testTag("top_ai_coach_button")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Coach",
            tint = BrandTealDark,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Child Switcher Badge
        Box {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(WarmSurface)
              .clickable { showChildDropdown = true }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("child_switcher_button"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(WarmCoral),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = activeChild?.avatarEmoji ?: "⭐",
                fontSize = 12.sp
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = activeChild?.nickname ?: "Child",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Switch Child",
              modifier = Modifier.size(18.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          DropdownMenu(
            expanded = showChildDropdown,
            onDismissRequest = { showChildDropdown = false }
          ) {
            Text(
              text = "Select Child Profile",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            allChildren.forEach { child ->
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = child.avatarEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = child.nickname,
                        fontWeight = if (child.id == activeChild?.id) FontWeight.Bold else FontWeight.Normal
                      )
                      Text(
                        text = "Age ${child.approximateAge}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                },
                onClick = {
                  onSwitchChild(child.id)
                  showChildDropdown = false
                },
                leadingIcon = {
                  if (child.id == activeChild?.id) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandTeal)
                  }
                }
              )
            }

            HorizontalDivider()

            DropdownMenuItem(
              text = { Text("+ Add Another Child", color = BrandTeal, fontWeight = FontWeight.Bold) },
              onClick = {
                showChildDropdown = false
                onAddChildClick()
              },
              leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = BrandTeal) }
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Settings Button
        IconButton(
          onClick = onSettingsClick,
          modifier = Modifier.testTag("top_settings_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun KidsparkBottomNavigation(
  currentScreen: Screen,
  onNavigate: (Screen) -> Unit
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    modifier = Modifier
      .navigationBarsPadding()
      .testTag("bottom_nav_bar")
  ) {
    NavigationBarItem(
      selected = currentScreen is Screen.Dashboard,
      onClick = { onNavigate(Screen.Dashboard) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Dashboard) Icons.Filled.Home else Icons.Outlined.Home,
          contentDescription = "Home"
        )
      },
      label = { Text("Home") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BrandTealDark,
        indicatorColor = BrandTealContainer
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Planner,
      onClick = { onNavigate(Screen.Planner) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Planner) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
          contentDescription = "Planner"
        )
      },
      label = { Text("Planner") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BrandTealDark,
        indicatorColor = BrandTealContainer
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Activities || currentScreen is Screen.ActivityDetail,
      onClick = { onNavigate(Screen.Activities) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Activities) Icons.Filled.Explore else Icons.Outlined.Explore,
          contentDescription = "Activities"
        )
      },
      label = { Text("Library") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BrandTealDark,
        indicatorColor = BrandTealContainer
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Explore || currentScreen is Screen.PathwayDetail,
      onClick = { onNavigate(Screen.Explore) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Explore) Icons.Filled.Star else Icons.Outlined.StarBorder,
          contentDescription = "Future Skills"
        )
      },
      label = { Text("Skills") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BrandTealDark,
        indicatorColor = BrandTealContainer
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Journal || currentScreen is Screen.Habits || currentScreen is Screen.ScreenFree,
      onClick = { onNavigate(Screen.Journal) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Journal) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
          contentDescription = "Growth Journal"
        )
      },
      label = { Text("Growth") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = BrandTealDark,
        indicatorColor = BrandTealContainer
      )
    )
  }
}

@Composable
fun SectionHeader(
  title: String,
  subtitle: String? = null,
  actionLabel: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (actionLabel != null && onActionClick != null) {
      TextButton(
        onClick = onActionClick,
        modifier = Modifier.testTag("action_${actionLabel.lowercase().replace(" ", "_")}")
      ) {
        Text(
          text = actionLabel,
          style = MaterialTheme.typography.labelMedium,
          color = BrandTeal,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun ActivitySummaryCard(
  activity: ActivityCatalogItem,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  onAddToPlanner: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("activity_card_${activity.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Age tag & category
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandTealLight,
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Text(
              text = activity.ageRange,
              style = MaterialTheme.typography.labelSmall,
              color = BrandTealDark,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Text(
            text = activity.category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) WarmCoral else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = activity.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = activity.setupInstructions,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = activity.estimatedDuration,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row {
          OutlinedButton(
            onClick = onAddToPlanner,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add to Plan", style = MaterialTheme.typography.labelSmall)
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("Details", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
          }
        }
      }
    }
  }
}
