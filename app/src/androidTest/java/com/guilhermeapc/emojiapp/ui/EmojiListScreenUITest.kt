// EmojiListScreenUITest.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.viewmodel.EmojiUiState
import com.guilhermeapc.emojiapp.viewmodel.EmojiViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class EmojiListScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emojiListScreen_clickEmoji_logsEmojiName() {
        // Given
        val mockViewModel = mockk<EmojiViewModel>(relaxed = true)
        val mockNavController = mockk<NavController>(relaxed = true)
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            EmojiUiState(
                isLoading = false,
                emojis = emojis,
                selectedEmoji = null,
                error = null
            )
        )

        // When
        val targetEmojiName = "+1"
        val expectedEmoji = emojis.first { it.name == targetEmojiName }
        composeTestRule.setContent {
            EmojiListScreen(
                viewModel = mockViewModel,
                navController = mockNavController
            )
        }

        // Then
        composeTestRule.onNodeWithText(
            substring = true,
            ignoreCase = true,
            text = targetEmojiName
        )
            .performClick()
        assertEquals(expectedEmoji, mockViewModel.uiState.value.selectedEmoji)
    }

    @Test
    fun emojiListScreen_pullsToRefresh_callsFetchEmojis() {
        // Given
        val mockViewModel = mockk<EmojiViewModel>(relaxed = true)
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            EmojiUiState(
                isLoading = false,
                emojis = listOf(
                    Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
                    Emoji(name = "-1", url = "https://emoji.url/minus1.png")
                ),
                selectedEmoji = null,
                error = null,
                isRefreshing = false
            )
        )

        // When
        composeTestRule.setContent {
            EmojiListScreen(navController = mockk(relaxed = true), viewModel = mockViewModel)
        }

        // Then
        // Perform swipe down gesture to trigger pull-to-refresh
        composeTestRule.onNode(hasTestTag("pull_refresh_box")).performTouchInput {
            swipeDown()
        }

        // Verify that fetchEmojis() was called
        coVerify { mockViewModel.fetchEmojis() }
    }
}
