package com.example.dayframe

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayframe.feature.archive.ArchiveScreen
import com.example.dayframe.feature.calendar.CalendarScreen
import com.example.dayframe.feature.detail.DetailScreen
import com.example.dayframe.feature.editor.EditorScreen
import com.example.dayframe.feature.feed.FeedScreen
import com.example.dayframe.feature.onboarding.OnboardingScreen
import com.example.dayframe.feature.settings.SettingsScreen
import com.example.dayframe.feature.statistics.StatisticsScreen
import com.example.dayframe.navigation.Routes

private data class BottomDestination(val route: String, val label: String, val icon: ImageVector)

private val destinations = listOf(
    BottomDestination(Routes.Calendar, "달력", Icons.Outlined.CalendarMonth),
    BottomDestination(Routes.Feed, "피드", Icons.Outlined.GridView),
    BottomDestination(Routes.Stats, "통계", Icons.Outlined.Insights),
    BottomDestination(Routes.Archive, "보관함", Icons.Outlined.Archive),
)

@Composable
fun DayframeRoot(viewModel: DayframeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (!state.onboardingComplete) {
        OnboardingScreen(onComplete = viewModel::completeOnboarding)
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination?.route?.let {
        it == Routes.Calendar || it == Routes.Feed || it == Routes.Stats || it == Routes.Archive
    } ?: true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) DayframeBottomBar(navController, currentDestination?.route)
        },
    ) { paddingValues ->
        DayframeNavHost(navController, viewModel, paddingValues)
    }
}

@Composable
private fun DayframeBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        destinations.take(2).forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { navigateTopLevel(navController, destination.route) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.editor()) },
            icon = { Text("＋", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall) },
            label = { Text("추가") },
        )
        destinations.drop(2).forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { navigateTopLevel(navController, destination.route) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}

private fun navigateTopLevel(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun DayframeNavHost(
    navController: NavHostController,
    viewModel: DayframeViewModel,
    paddingValues: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Calendar,
        modifier = Modifier.fillMaxSize().padding(paddingValues),
    ) {
        composable(Routes.Calendar) {
            CalendarScreen(viewModel, onOpenDetail = { navController.navigate(Routes.detail(it)) }, onCreate = { date -> navController.navigate(Routes.editor(date = date.toString())) }, onSettings = { navController.navigate(Routes.Settings) })
        }
        composable(Routes.Feed) {
            FeedScreen(viewModel, onOpenDetail = { navController.navigate(Routes.detail(it)) }, onCreate = { navController.navigate(Routes.editor()) })
        }
        composable(Routes.Stats) { StatisticsScreen(viewModel) }
        composable(Routes.Archive) { ArchiveScreen(viewModel, onOpenDetail = { navController.navigate(Routes.detail(it)) }) }
        composable(Routes.Settings) { SettingsScreen(viewModel, onBack = { navController.popBackStack() }) }
        composable(
            route = Routes.Editor,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType; defaultValue = 0L },
                navArgument("date") { type = NavType.StringType; defaultValue = ""; nullable = true },
            ),
        ) { entry ->
            EditorScreen(
                viewModel = viewModel,
                entryId = entry.arguments?.getLong("entryId") ?: 0L,
                initialDate = entry.arguments?.getString("date").orEmpty(),
                onBack = { navController.popBackStack() },
                onSaved = { id -> navController.navigate(Routes.detail(id)) { popUpTo(Routes.Calendar) } },
            )
        }
        composable(
            route = Routes.Detail,
            arguments = listOf(navArgument("id") { type = NavType.LongType }),
        ) { entry ->
            DetailScreen(
                viewModel = viewModel,
                entryId = entry.arguments?.getLong("id") ?: 0L,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.editor(entryId = id)) },
            )
        }
    }
}
