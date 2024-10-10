package com.yilmaz.ble.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yilmaz.ble.features.ble.presentation.screen.home_screen.HomeScreen

@Composable
fun SetUpNavigationGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavigationGraph.ScreenHome
    ) {
        composable<NavigationGraph.ScreenHome> {
            HomeScreen(navHostController)
        }
    }
}