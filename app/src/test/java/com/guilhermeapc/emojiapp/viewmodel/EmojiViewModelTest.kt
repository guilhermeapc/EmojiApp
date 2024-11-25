// EmojiViewModelTest.kt

package com.guilhermeapc.emojiapp.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.guilhermeapc.emojiapp.data.EmojiDao
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.network.EmojiApiService
import com.guilhermeapc.emojiapp.repository.EmojiRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EmojiViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: EmojiRepository
    private lateinit var emojiDao: EmojiDao
    private lateinit var apiService: EmojiApiService
    private lateinit var viewModel: EmojiViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        emojiDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = EmojiRepository(apiService, emojiDao)
        viewModel = EmojiViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRandomEmoji selects a random emoji from cached data`() = runTest {
        // Given
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png"),
            Emoji(name = "100", url = "https://emoji.url/100.png")
        )
        coEvery { repository.getEmojis() } returns emojis

        // When
        viewModel.getRandomEmoji()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertNull(uiState.error)
        assertNotNull(uiState.selectedEmoji)
        assert(emojis.contains(uiState.selectedEmoji))
    }

    @Test
    fun `getRandomEmoji handles empty emoji list`() = runTest {
        // Given
        val emojis = emptyList<Emoji>()
        coEvery { repository.getEmojis() } returns emojis

        // When
        viewModel.getRandomEmoji()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("No emojis available", uiState.error)
        assertNull(uiState.selectedEmoji)
    }

    @Test
    fun `getRandomEmoji handles exceptions`() = runTest {
        // Given
        coEvery { repository.getEmojis() } throws Exception("Network Error")

        // When
        viewModel.getRandomEmoji()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("Network Error", uiState.error)
        assertNull(uiState.selectedEmoji)
    }

    @Test
    fun `removeEmoji removes the specified emoji from the list`() = runTest {
        // Given
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )
        coEvery { repository.getEmojis() } returns emojis
        viewModel.fetchEmojis()
        advanceUntilIdle()

        // When
        viewModel.removeEmoji(emojis[0]) // Remove "+1"

        // Then
        val expectedEmojis = listOf(emojis[1])
        assertEquals(expectedEmojis, viewModel.uiState.value.emojis)
    }
}
