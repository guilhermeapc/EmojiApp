// EmojiListScreenUITest.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.viewmodel.AppUiState
import com.guilhermeapc.emojiapp.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class EmojiListScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emojiListScreen_pullsToRefresh_callsFetchEmojis() {
        // Given
        val mockViewModel = mockk<MainViewModel>(relaxed = true)
        coEvery { mockViewModel.uiState } returns MutableStateFlow(
            AppUiState(
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
