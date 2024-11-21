// EmojiRepository.kt
package com.guilhermeapc.emojiapp.repository

import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.network.EmojiApiService
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class EmojiRepository @Inject constructor(
    private val apiService: EmojiApiService
) {
    suspend fun getEmojis(): List<Emoji> {
        Timber.d("Making network request to fetch emojis")
        val emojiMap = apiService.fetchEmojis()
        Timber.d("Received emoji map: $emojiMap")
        return emojiMap.map { Emoji(name = it.name, url = it.url) }
    }
}
