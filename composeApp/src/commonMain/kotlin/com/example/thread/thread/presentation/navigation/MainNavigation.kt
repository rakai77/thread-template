package com.example.thread.thread.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thread.thread.presentation.screen.home.HomeScreen
import com.example.thread.thread.presentation.screen.login.LoginScreen

@Composable
fun MainNavigation(
    navController: NavHostController,
) {

    NavHost(
        navController = navController,
        startDestination = AppRoute.LOGIN_SCREEN
    ) {
        composable(
            route = AppRoute.LOGIN_SCREEN
        ) {
            LoginScreen(navController = navController)
        }

        composable(
            route = AppRoute.HOME_SCREEN
        ) {
            HomeScreen(navController = navController)
        }
    }
}