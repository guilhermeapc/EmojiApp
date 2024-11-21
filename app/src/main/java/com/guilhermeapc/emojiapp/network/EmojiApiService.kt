//EmojiApiService.kt
package com.guilhermeapc.emojiapp.network


import com.guilhermeapc.emojiapp.model.Emoji
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmojiApiService @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) {
    private val baseUrl = "https://api.github.com/emojis"

    suspend fun fetchEmojis(): List<Emoji> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Network call failed: ${response.code}")

            val body = response.body?.string() ?: throw Exception("Empty response body")

            // Gson cannot directly deserialize to Map<String, String> in a type-safe way using reified types,
            // so we use define a type token.
            val type = object : TypeToken<Map<String, String>>() {}.type

            // Deserialize JSON to Map<String, String> using the defined type
            val map: Map<String, String> = gson.fromJson(body, type)

            // Convert to Emoji list
            map.map { Emoji(it.key, it.value) }
        }
    }
}