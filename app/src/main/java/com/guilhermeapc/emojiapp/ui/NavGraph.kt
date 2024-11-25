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
            MainScreen(navController = navController)
        }
        composable("emoji_list_screen") {
            EmojiListScreen(navController = navController)
        }
        composable("avatar_list") {
            AvatarListScreen(navController = navController)
        }
    }
}
