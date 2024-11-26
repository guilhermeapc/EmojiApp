// EmojiListScreenTest.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.viewmodel.AppUiState
import com.guilhermeapc.emojiapp.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class EmojiListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emojiListScreen_displaysEmojisInGrid() {
        // Given
        val mockViewModel = mockk<MainViewModel>(relaxed = true)
        val mockNavController = mockk<NavController>()
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            AppUiState(
                isLoading = false,
                emojis = emojis,
                selectedEmoji = null,
                error = null
            )
        )

        // When
        composeTestRule.setContent {
            EmojiListScreen(
                viewModel = mockViewModel,
                navController = mockNavController
            )
        }

        // Then
        composeTestRule.onNodeWithText("+1").assertIsDisplayed()
        composeTestRule.onNodeWithText("-1").assertIsDisplayed()
    }

    @Test
    fun emojiListScreen_showsLoadingIndicator() {
        // Given
        val mockViewModel = mockk<MainViewModel>(relaxed = true)
        val mockNavController = mockk<NavController>()
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            AppUiState(
                isLoading = true,
                emojis = emptyList(),
                selectedEmoji = null,
                error = null
            )
        )

        // When
        composeTestRule.setContent {
            EmojiListScreen(
                viewModel = mockViewModel,
                navController = mockNavController
            )
        }

        // Then
        composeTestRule.onNode(hasTestTag("progress_indicator")).assertIsDisplayed()
    }

    @Test
    fun emojiListScreen_showsErrorMessage() {
        // Given
        val mockViewModel = mockk<MainViewModel>(relaxed = true)
        val mockNavController = mockk<NavController>()
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            AppUiState(
                isLoading = false,
                emojis = emptyList(),
                selectedEmoji = null,
                error = "Failed to load emojis"
            )
        )

        // When
        composeTestRule.setContent {
            EmojiListScreen(
                viewModel = mockViewModel,
                navController = mockNavController
            )
        }

        // Then
        composeTestRule.onNodeWithText(
            ignoreCase = true,
            substring = true,
            text = "Error"
        ).assertIsDisplayed()
    }
}
