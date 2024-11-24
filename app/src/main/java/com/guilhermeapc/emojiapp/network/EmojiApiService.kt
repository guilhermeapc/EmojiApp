// EmojiApiService.kt
package com.guilhermeapc.emojiapp.network

import retrofit2.http.GET

interface EmojiApiService {
    @GET("emojis")
    suspend fun fetchEmojis(): Map<String, String>
}
