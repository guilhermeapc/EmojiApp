// EmojiRepository.kt
package com.guilhermeapc.emojiapp.repository

import com.guilhermeapc.emojiapp.data.EmojiDao
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.network.EmojiApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class EmojiRepository @Inject constructor(
    private val apiService: EmojiApiService,
    private val emojiDao: EmojiDao
) {
    suspend fun getEmojis(): List<Emoji> {
        Timber.d("Fetching emojis from the database")
        val cachedEmojis = emojiDao.getAllEmojis().first()
        if (cachedEmojis.isNotEmpty()) {
            Timber.d("Emojis found in cache: $cachedEmojis")
            return cachedEmojis
        }

        Timber.d("No emojis found in cache. Making network request to fetch emojis")
        val emojiMap = apiService.fetchEmojis()
        Timber.d("Received emojis from API: $emojiMap")
        val emojiList = emojiMap.map { Emoji(it.key, it.value) }
        emojiDao.insertAll(emojiList)
        return emojiList
    }

    // To use eventually in Pull-To-Refresh behaviour
    suspend fun refreshEmojis(): List<Emoji> {
        Timber.d("Refreshing emojis from the API")
        val emojiMap = apiService.fetchEmojis()
        Timber.d("Received emojis from API: $emojiMap")
        val emojiList = emojiMap.map { Emoji(it.key, it.value) }
        emojiDao.deleteAllEmojis()
        emojiDao.insertAll(emojiList)
        return emojiList
    }
}
