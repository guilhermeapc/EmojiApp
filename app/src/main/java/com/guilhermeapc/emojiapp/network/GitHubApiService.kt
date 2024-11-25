// network/GitHubApiService.kt
package com.guilhermeapc.emojiapp.network

import com.guilhermeapc.emojiapp.model.GitHubRepo
import com.guilhermeapc.emojiapp.model.GitHubUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUser

    @GET("users/{username}/repos")
    suspend fun getRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<GitHubRepo>

    @GET("emojis")
    suspend fun fetchEmojis(): Map<String, String>
}
