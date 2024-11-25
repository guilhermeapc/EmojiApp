// EmojiRepositoryTest.kt
package com.guilhermeapc.emojiapp.repository

import com.guilhermeapc.emojiapp.data.EmojiDao
import com.guilhermeapc.emojiapp.data.GitHubRepoDao
import com.guilhermeapc.emojiapp.data.GitHubUserDao
import com.guilhermeapc.emojiapp.data.RemoteKeysDao
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.network.GitHubApiService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmojiRepositoryTest {
    private lateinit var gitHubApiService: GitHubApiService
    private lateinit var emojiDao: EmojiDao
    private lateinit var gitHubUserDao: GitHubUserDao
    private lateinit var gitHubRepoDao: GitHubRepoDao
    private lateinit var remoteKeysDao: RemoteKeysDao
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        // Create mock instances
        gitHubApiService = mockk(relaxed = true)
        emojiDao = mockk(relaxed = true)
        gitHubRepoDao = mockk(relaxed = true)
        remoteKeysDao = mockk(relaxed = true)
        // Initialize repository with mocks
        repository = AppRepository(
            emojiDao = emojiDao,
            gitHubUserDao = gitHubUserDao,
            gitHubApiService = gitHubApiService,
            gitHubRepoDao = gitHubRepoDao,
            remoteKeysDao = remoteKeysDao
        )
    }

    @Test
    fun `getEmojis returns cached emojis when available`() = runBlocking {
        // Given: Mocked cached emojis
        val cachedEmojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )
        coEvery { emojiDao.getAllEmojis() } returns flowOf(cachedEmojis)

        // When: Calling getEmojis
        val result = repository.getEmojis()

        // Then: Should return cached emojis without calling API
        assertEquals(cachedEmojis, result)
    }

    @Test
    fun `getEmojis fetches from API and caches when no cached emojis`() = runBlocking {
        // Given: No cached emojis
        coEvery { emojiDao.getAllEmojis() } returns flowOf(emptyList())
        // Mock API response
        val apiEmojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )
        coEvery { gitHubApiService.fetchEmojis() } returns mapOf(
            "+1" to "https://emoji.url/plus1.png",
            "-1" to "https://emoji.url/minus1.png"
        )
        coEvery { emojiDao.insertAll(apiEmojis) } returns Unit

        // When: Calling getEmojis
        val result = repository.getEmojis()

        // Then: Should return emojis from API and cache them
        assertEquals(apiEmojis, result)
        // Asserts that the method was called exactly once.
        verify(exactly = 1) { emojiDao.getAllEmojis() }
    }

    @Test
    fun `getEmojis throws exception when API call fails and no cached emojis`() = runTest {
        // Given: No cached emojis
        coEvery { emojiDao.getAllEmojis() } returns flowOf(emptyList())
        // Mock API failure
        coEvery { gitHubApiService.fetchEmojis() } throws Exception("Network Error")

        // When & Then: Use Assert.assertThrows to verify exception
        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                repository.getEmojis()
            }
        }

        assertEquals("Network Error", exception.message)
    }
}
