// NavGraph.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "emoji_screen") {
        composable("emoji_screen") {
            EmojiScreen(navController = navController)
        }
        composable("emoji_list_screen") {
            EmojiListScreen(navController = navController)
        }
    }
}
