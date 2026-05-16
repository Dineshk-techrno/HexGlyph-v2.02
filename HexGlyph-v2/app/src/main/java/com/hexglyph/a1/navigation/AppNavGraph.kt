package com.hexglyph.a1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hexglyph.a1.feature.analytics.AnalyticsScreen
import com.hexglyph.a1.feature.decode.DecodeScreen
import com.hexglyph.a1.feature.encode.EncodeScreen
import com.hexglyph.a1.feature.export.ExportScreen
import com.hexglyph.a1.feature.history.HistoryScreen
import com.hexglyph.a1.feature.settings.SettingsScreen
import com.hexglyph.a1.feature.home.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController  = navController,
        startDestination = Screen.Home.route,
        enterTransition  = { NavigationAnimations.enterTransition() },
        exitTransition   = { NavigationAnimations.exitTransition() },
        popEnterTransition = { NavigationAnimations.popEnterTransition() },
        popExitTransition  = { NavigationAnimations.popExitTransition() }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToEncode    = { navController.navigate(Screen.Encode.route) },
                onNavigateToDecode    = { navController.navigate(Screen.Decode.route) },
                onNavigateToHistory   = { navController.navigate(Screen.History.route) },
                onNavigateToSettings  = { navController.navigate(Screen.Settings.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
            )
        }
        composable(Screen.Encode.route) {
            EncodeScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Decode.route) {
            DecodeScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onNavigateToExport = { navController.navigate(Screen.Export.route) }
            )
        }
        composable(Screen.Export.route) {
            ExportScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }
    }
}
