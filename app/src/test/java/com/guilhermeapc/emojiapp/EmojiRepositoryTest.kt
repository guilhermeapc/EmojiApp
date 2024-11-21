// EmojiRepositoryTest.kt
package com.guilhermeapc.emojiapp

import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.network.EmojiApiService
import com.guilhermeapc.emojiapp.repository.EmojiRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmojiRepositoryTest {

    private lateinit var apiService: EmojiApiService
    private lateinit var repository: EmojiRepository

    @Before
    fun setUp() {
        // mock instance of EmojiApiService
        apiService = mockk()
        repository = EmojiRepository(apiService)
    }

    @After
    fun tearDown() {
        //TODO Perform cleanup
    }

    @Test
    fun `getEmojis returns list of emojis from API`() = runBlocking {
        // Given: Mocked API response
        val mockEmojiList = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )

        // When: apiService.fetchEmojis() is called, return mockEmojiList
        coEvery { apiService.fetchEmojis() } returns mockEmojiList

        // Execute: Call getEmojis()
        val result = repository.getEmojis()

        // Then: Verify the result matches the mocked data
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(mockEmojiList, result)
    }

    @Test
    fun `getEmojis throws exception when API call fails`() = runBlocking {
        // Given: Mocked API failure
        coEvery { apiService.fetchEmojis() } throws Exception("Network Error")

        try {
            // Execute: Call getEmojis(), expecting an exception
            repository.getEmojis()
            // If no exception is thrown, fail the test
            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            // Then: Verify the exception message
            assertEquals("Network Error", e.message)
        }
    }
}
