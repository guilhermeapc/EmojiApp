// ui/AvatarListScreenUITest.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.viewmodel.EmojiUiState
import com.guilhermeapc.emojiapp.viewmodel.EmojiViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class AvatarListScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun avatarListScreen_displaysCachedUsers() {
        // Given
        val mockViewModel = mockk<EmojiViewModel>(relaxed = true)
        val users = listOf(
            GitHubUser(
                login = "octocat",
                id = 1,
                avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
            ),
            GitHubUser(
                login = "hubot",
                id = 2,
                avatar_url = "https://avatars.githubusercontent.com/u/2?v=4"
            )
        )
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            EmojiUiState(
                isLoading = false,
                cachedUsers = users,
                error = null
            )
        )

        // When
        composeTestRule.setContent {
            AvatarListScreen(navController = mockk(relaxed = true), viewModel = mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("octocat").assertIsDisplayed()
        composeTestRule.onNodeWithText("hubot").assertIsDisplayed()
    }

    @Test
    fun avatarListScreen_deletesUserOnDeleteButtonClick() {
        // Given
        val mockViewModel = mockk<EmojiViewModel>(relaxed = true)
        val user = GitHubUser(
            login = "octocat",
            id = 1,
            avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
        )
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            EmojiUiState(
                isLoading = false,
                cachedUsers = listOf(user),
                error = null
            )
        )

        // When
        composeTestRule.setContent {
            AvatarListScreen(navController = mockk(relaxed = true), viewModel = mockViewModel)
        }

        // Click the delete button
        composeTestRule.onNodeWithContentDescription("Delete Avatar").performClick()

        // Then
        coVerify { mockViewModel.deleteGitHubUser(user) }
    }
}
