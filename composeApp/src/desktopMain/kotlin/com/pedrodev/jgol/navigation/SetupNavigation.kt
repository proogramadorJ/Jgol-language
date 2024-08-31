package com.pedrodev.jgol.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedrodev.jgol.view.HomeScreen
import com.pedrodev.jgol.view.EditorScreen

@Composable
fun SetupNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "HomeScreen",
    ) {
        composable(
            route = "HomeScreen"
        ) {
            HomeScreen(navController = navController)
        }
        composable(
            route = "EditorScreen"
        ) {
            EditorScreen(navController = navController)
        }
    }

}