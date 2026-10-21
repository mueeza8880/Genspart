package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.KidsparkViewModel
import com.example.ui.Screen
import com.example.ui.components.KidsparkBottomNavigation
import com.example.ui.components.KidsparkTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: KidsparkViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val parentProfile by viewModel.parentProfile.collectAsState()
        val activeChild by viewModel.activeChild.collectAsState()
        val allChildren by viewModel.allChildren.collectAsState()
        val userMessage by viewModel.userMessage.collectAsState()
        val context = LocalContext.current

        // Handle Toast feedback
        LaunchedEffect(userMessage) {
          userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
          }
        }

        // Handle System Back button
        BackHandler(enabled = currentScreen !is Screen.Dashboard && currentScreen !is Screen.Welcome) {
          viewModel.navigateBack()
        }

        // Screens that show bottom nav bar
        val showBottomBar = currentScreen is Screen.Dashboard ||
          currentScreen is Screen.Planner ||
          currentScreen is Screen.Activities ||
          currentScreen is Screen.Explore ||
          currentScreen is Screen.Journal ||
          currentScreen is Screen.Habits ||
          currentScreen is Screen.ScreenFree

        // Screens that show top header
        val showTopBar = showBottomBar

        Scaffold(
          topBar = {
            if (showTopBar) {
              KidsparkTopBar(
                parentName = parentProfile?.displayName ?: "Parent",
                activeChild = activeChild,
                allChildren = allChildren,
                onSwitchChild = { childId -> viewModel.switchActiveChild(childId) },
                onAddChildClick = { viewModel.navigateTo(Screen.Children) },
                onSettingsClick = { viewModel.navigateTo(Screen.Settings) },
                onAiCoachClick = { viewModel.navigateTo(Screen.AICoach) }
              )
            }
          },
          bottomBar = {
            if (showBottomBar) {
              KidsparkBottomNavigation(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
              )
            }
          }
        ) { innerPadding ->
          Box(modifier = Modifier.padding(innerPadding)) {
            when (val screen = currentScreen) {
              is Screen.Welcome -> WelcomeScreen(
                onGetStarted = { viewModel.navigateTo(Screen.Onboarding) },
                onSignIn = { viewModel.navigateTo(Screen.Dashboard) }
              )
              is Screen.Onboarding -> OnboardingScreen(
                viewModel = viewModel,
                onComplete = { viewModel.navigateTo(Screen.Dashboard) },
                onBack = { viewModel.navigateTo(Screen.Welcome) }
              )
              is Screen.Dashboard -> DashboardScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.Planner -> PlannerScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.Activities -> ActivityLibraryScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.ActivityDetail -> ActivityDetailScreen(
                activityId = screen.activityId,
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
              )
              is Screen.Explore -> ExplorePathwaysScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.PathwayDetail -> PathwayDetailScreen(
                pathwayId = screen.pathwayId,
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onNavigateToWeeklyPlan = { viewModel.navigateTo(Screen.WeeklyPlan) }
              )
              is Screen.Journal -> JournalScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.Habits -> HabitTrackerScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.ScreenFree -> ScreenFreeScreen(
                viewModel = viewModel,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
              )
              is Screen.AICoach -> AICoachScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
              )
              is Screen.WeeklyPlan -> WeeklyPlanScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onNavigateToPlanner = { viewModel.navigateTo(Screen.Planner) }
              )
              is Screen.Insights -> InsightsScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
              )
              is Screen.Children -> ChildrenScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
              )
              is Screen.Settings -> SettingsScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onNavigateToChildren = { viewModel.navigateTo(Screen.Children) },
                onNavigateToInsights = { viewModel.navigateTo(Screen.Insights) }
              )
              is Screen.Certificate -> CertificateScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
              )
            }
          }
        }
      }
    }
  }
}
