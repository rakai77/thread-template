package com.example.thread.thread.presentation.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.thread.thread.presentation.navigation.MainNavigation
import com.example.thread.thread.presentation.theme.ThreadTheme


@Composable
fun ThreadApp() {
    val navController = rememberNavController()
    ThreadTheme {
        MainNavigation(navController = navController)
    }
}


