// network/GitHubApiService.kt
package com.guilhermeapc.emojiapp.network

import com.guilhermeapc.emojiapp.model.GitHubUser
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser
}
