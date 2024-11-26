// MainViewModelTest.kt

package com.guilhermeapc.emojiapp.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.model.GitHubRepo
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.model.Owner
import com.guilhermeapc.emojiapp.repository.AppRepository
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: AppRepository
    private lateinit var viewModel: MainViewModel

    // Variables to hold mock data
    private var currentEmojis = listOf<Emoji>()
    private var currentGitHubUsers = MutableStateFlow<List<GitHubUser>>(emptyList())
    private var currentGitHubRepos = MutableStateFlow<List<GitHubRepo>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock the repository
        repository = mockk<AppRepository>()

        // Mock getReposPager to return empty PagingData initially
        every { repository.getReposPager(any()) } returns flowOf(PagingData.from(emptyList<GitHubRepo>()))

        // Mock getAllGitHubUsers to return the currentGitHubUsers flow
        every { repository.getAllGitHubUsers() } returns currentGitHubUsers

        // Mock addGitHubUser to add the user to currentGitHubUsers
        coEvery { repository.addGitHubUser(any()) } coAnswers {
            val user = firstArg<GitHubUser>()
            currentGitHubUsers.value = currentGitHubUsers.value + user
        }

        // Mock deleteGitHubUser to remove the user from currentGitHubUsers
        coEvery { repository.deleteGitHubUser(any()) } coAnswers {
            val user = firstArg<GitHubUser>()
            currentGitHubUsers.value = currentGitHubUsers.value - user
        }

        // Mock getEmojis to return currentEmojis
        coEvery { repository.getEmojis() } coAnswers { currentEmojis }

        // Mock addGitHubRepos to add repos to currentGitHubRepos
        coEvery { repository.addGitHubRepos(any()) } coAnswers {
            val repos = firstArg<List<GitHubRepo>>()
            currentGitHubRepos.value = currentGitHubRepos.value + repos
        }

        // Mock deleteGitHubReposByUser to remove repos by username
        coEvery { repository.deleteGitHubReposByUser(any()) } coAnswers {
            val username = firstArg<String>()
            currentGitHubRepos.value =
                currentGitHubRepos.value.filter { it.owner.login != username }
        }

        // Mock getReposPager to return the currentGitHubRepos as PagingData filtered by username
        every { repository.getReposPager(any()) } answers {
            val username = firstArg<String>()
            flowOf(PagingData.from(currentGitHubRepos.value.filter { it.owner.login == username }))
        }

        // Instantiate the ViewModel with the mocked repository
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    // Emoji Tests
    @Test
    fun `getRandomEmoji selects a random emoji from cached data`() = runTest {
        // Given
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png"),
            Emoji(name = "100", url = "https://emoji.url/100.png")
        )
        currentEmojis = emojis
        coEvery { repository.getEmojis() } returns currentEmojis

        // When
        viewModel.getRandomEmoji()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
        assertNotNull(uiState.selectedEmoji)
        assert(emojis.contains(uiState.selectedEmoji))
    }

    @Test
    fun `getRandomEmoji handles empty emoji list`() = runTest {
        // Given
        currentEmojis = emptyList()
        coEvery { repository.getEmojis() } returns currentEmojis

        // When
        viewModel.getRandomEmoji()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
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
        assertFalse(uiState.isLoading)
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
        currentEmojis = emojis
        coEvery { repository.getEmojis() } returns currentEmojis

        // Fetch emojis to populate the ViewModel's state
        viewModel.fetchEmojis()
        advanceUntilIdle()

        // When
        viewModel.removeEmoji(emojis[0]) // Remove "+1"

        // Simulate the repository's getEmojis() reflecting the removal
        currentEmojis = currentEmojis - emojis[0]
        coEvery { repository.getEmojis() } returns currentEmojis

        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(listOf(emojis[1]), uiState.emojis)
    }

    // GitHub User Tests
    @Test
    fun `addGitHubUser adds user to repository`() = runTest {
        // Given
        val user = GitHubUser(
            login = "octocat", id = 1, avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
        )

        // When
        viewModel.addGitHubUser(user)
        advanceUntilIdle()

        // Then
        coVerify { repository.addGitHubUser(user) }
        val users = repository.getAllGitHubUsers().first()
        assertTrue(users.contains(user))
    }

    @Test
    fun `deleteGitHubUser removes user from repository`() = runTest {
        // Given
        val user = GitHubUser(
            login = "octocat", id = 1, avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
        )

        // Ensure the user is in the currentGitHubUsers flow
        currentGitHubUsers.value = listOf(user)
        coEvery { repository.getAllGitHubUsers() } returns currentGitHubUsers

        // When - Delete the user
        viewModel.deleteGitHubUser(user)
        advanceUntilIdle()

        // Then - Verify the user is no longer in the repository
        val usersAfterDeletion = repository.getAllGitHubUsers().first()
        assertFalse(usersAfterDeletion.contains(user))
    }

    // GitHub Repositories Tests

    @Test
    fun `fetchGitHubRepos emits correct PagingData`() = runTest {
        // Given
        val username = "google"
        val repos = listOf(
            GitHubRepo(
                id = 1, full_name = "google/Repo1", private = false, owner = Owner(
                    login = "google", avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
                ), login = "google"
            ), GitHubRepo(
                id = 2, full_name = "google/Repo2", private = false, owner = Owner(
                    login = "google", avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
                ), login = "google"
            )
        )
        currentGitHubRepos.value = repos

        // When
        val collectedRepos = mutableListOf<PagingData<GitHubRepo>>()
        val job = launch {
            repository.getReposPager(username).collect {
                collectedRepos.add(it)
            }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertEquals(1, collectedRepos.size)
        val snapshot = convertPagingDataToList(collectedRepos.first())
        assertEquals(repos, snapshot)
    }

    @Test
    fun `addGitHubRepos adds repositories correctly`() = runTest {
        // Given
        val username = "google"
        val newRepos = listOf(
            GitHubRepo(
                id = 3, full_name = "google/Repo3", private = false, owner = Owner(
                    login = "google", avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
                ), login = "google"
            ), GitHubRepo(
                id = 4, full_name = "google/Repo4", private = false, owner = Owner(
                    login = "google", avatar_url = "https://avatars.githubusercontent.com/u/1?v=4"
                ), login = "google"
            )
        )

        // When
        repository.addGitHubRepos(newRepos)
        advanceUntilIdle()

        // Then
        coVerify { repository.addGitHubRepos(newRepos) }
        val updatedRepos = repository.getReposPager(username).first()
        val snapshot = convertPagingDataToList(updatedRepos)
        assertEquals(newRepos, snapshot)
    }

    // Helper classes for PagingData Differ

    private class GitHubRepoDiffCallback : DiffUtil.ItemCallback<GitHubRepo>() {
        override fun areItemsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean {
            return oldItem == newItem
        }
    }

    private class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    // Helper function to convert PagingData to List using AsyncPagingDataDiffer
    private suspend fun <T : Any> convertPagingDataToList(pagingData: PagingData<T>): List<T> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = GitHubRepoDiffCallback() as DiffUtil.ItemCallback<T>,
            updateCallback = NoopListCallback(),
            workerDispatcher = testDispatcher
        )
        differ.submitData(pagingData)
        return differ.snapshot().items
    }
}
