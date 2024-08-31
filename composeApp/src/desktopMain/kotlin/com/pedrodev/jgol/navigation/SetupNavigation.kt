package com.pedrodev.jgol.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedrodev.jgol.view.Home
import com.pedrodev.jgol.view.MainEditorScreen

@Composable
fun SetupNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Home",
    ) {
        composable(
            route = "Home"
        ) {
            Home(navController = navController)
        }
        composable(
            route = "Editor"
        ) {
            MainEditorScreen(navController = navController)
        }
    }

}