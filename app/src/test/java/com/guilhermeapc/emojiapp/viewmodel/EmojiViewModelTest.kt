// MainViewModelTest.kt

package com.guilhermeapc.emojiapp.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.guilhermeapc.emojiapp.data.EmojiDao
import com.guilhermeapc.emojiapp.data.GitHubRepoDao
import com.guilhermeapc.emojiapp.data.GitHubUserDao
import com.guilhermeapc.emojiapp.data.RemoteKeysDao
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.network.GitHubApiService
import com.guilhermeapc.emojiapp.repository.AppRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: AppRepository
    private lateinit var emojiDao: EmojiDao
    private lateinit var githubUserDao: GitHubUserDao
    private lateinit var gitHubRepoDao: GitHubRepoDao
    private lateinit var remoteKeysDao: RemoteKeysDao
    private lateinit var emojiApiService: GitHubApiService
    private lateinit var githubApiService: GitHubApiService
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        emojiDao = mockk(relaxed = true)
        emojiApiService = mockk(relaxed = true)
        gitHubRepoDao = mockk(relaxed = true)
        gitHubRepoDao = mockk(relaxed = true)
        repository = AppRepository(
            gitHubApiService = githubApiService,
            emojiDao = emojiDao,
            gitHubUserDao = githubUserDao,
            gitHubRepoDao = gitHubRepoDao,
            remoteKeysDao = remoteKeysDao
        )

        viewModel = MainViewModel(repository)
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

    @Test
    fun `deleteGitHubUser removes user from repository`() = runBlocking {
        // Given
        val user = GitHubUser(
            login = "octocat",
            id = 1,
            avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
        )
        viewModel.addGitHubUser(user) // Ensure user is added

        // When
        viewModel.deleteGitHubUser(user)

        // Then
        coVerify { repository.deleteGitHubUser(user) }
    }

    @Test
    fun `addGitHubUser adds user to repository`() = runBlocking {
        // Given
        val user = GitHubUser(
            login = "octocat",
            id = 1,
            avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
        )

        // When
        viewModel.addGitHubUser(user)

        // Then
        coVerify { repository.addGitHubUser(user) }
    }
}
