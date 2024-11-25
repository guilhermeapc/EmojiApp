// EmojiDaoTest.kt
package com.guilhermeapc.emojiapp.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.guilhermeapc.emojiapp.model.Emoji
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmojiDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var emojiDao: EmojiDao

    @Before
    fun setUp() {
        // Create an in-memory version of the database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        emojiDao = database.emojiDao()
    }

    @After
    fun tearDown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun insertAll_and_getAllEmojis() = runBlocking {
        // Given: A list of emojis to insert
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )

        // When: Inserting emojis into the database
        emojiDao.insertAll(emojis)

        // Then: Retrieving all emojis should return the inserted list
        val retrievedEmojis = emojiDao.getAllEmojis().first()
        assertEquals(2, retrievedEmojis.size)
        assertTrue(retrievedEmojis.containsAll(emojis))
    }

    @Test
    fun deleteAllEmojis_clearsDatabase() = runBlocking {
        // Given: Inserting emojis into the database
        val emojis = listOf(
            Emoji(name = "+1", url = "https://emoji.url/plus1.png"),
            Emoji(name = "-1", url = "https://emoji.url/minus1.png")
        )
        emojiDao.insertAll(emojis)

        // When: Deleting all emojis
        emojiDao.deleteAllEmojis()

        // Then: The database should be empty
        val retrievedEmojis = emojiDao.getAllEmojis().first()
        assertTrue(retrievedEmojis.isEmpty())
    }
}
