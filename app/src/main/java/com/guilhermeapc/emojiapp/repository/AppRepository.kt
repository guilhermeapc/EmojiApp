// AppRepository.kt
package com.guilhermeapc.emojiapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.guilhermeapc.emojiapp.data.EmojiDao
import com.guilhermeapc.emojiapp.data.GitHubRepoDao
import com.guilhermeapc.emojiapp.data.GitHubUserDao
import com.guilhermeapc.emojiapp.data.RemoteKeysDao
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.model.GitHubRepo
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.network.GitHubApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class AppRepository @Inject constructor(
    private val gitHubApiService: GitHubApiService,
    private val emojiDao: EmojiDao,
    private val gitHubUserDao: GitHubUserDao,
    private val gitHubRepoDao: GitHubRepoDao,
    private val remoteKeysDao: RemoteKeysDao
) {
    suspend fun getEmojis(): List<Emoji> {
        Timber.d("Fetching emojis from the database")
        return emojiDao.getAllEmojis().first().takeIf { it.isNotEmpty() }?.let { cachedEmojis ->
            Timber.d("Emojis found in cache: $cachedEmojis")
            cachedEmojis
        } ?: run {
            Timber.d("No emojis found in cache. Making network request to fetch emojis")
            val emojiMap = gitHubApiService.fetchEmojis()
            Timber.d("Received emojis from API: $emojiMap")
            val emojiList = emojiMap.map { Emoji(it.key, it.value) }
            Timber.d("Caching emojis: $emojiList")
            emojiDao.insertAll(emojiList)
            emojiList
        }
    }

    // GitHub User Methods

    suspend fun getGitHubUser(username: String): GitHubUser {
        Timber.d("Fetching user from the database")
        return gitHubUserDao.getUserByUsername(username) ?: run {
            // If cachedUser is null, fetch from API
            Timber.d("No user found in cache. Making network request")
            val user = gitHubApiService.getUser(username)
            // Cache the result
            Timber.d("Received user from API. Caching: \n $user")
            gitHubUserDao.insertUser(user)
            user
        }
    }

    suspend fun deleteGitHubUser(user: GitHubUser) {
        gitHubUserDao.deleteUser(user)
    }

    suspend fun addGitHubUser(user: GitHubUser) {
        gitHubUserDao.insertUser(user)
    }

    fun getAllGitHubUsers(): Flow<List<GitHubUser>> {
        Timber.d("Fetching all cached GitHub users")
        return gitHubUserDao.getAllUsers()
    }

    // GitHub Repo Methods

    // for future use if needed
    suspend fun addGitHubRepos(repos: List<GitHubRepo>) {
        gitHubRepoDao.insertRepos(repos)
    }

    // for future use if needed
    suspend fun deleteGitHubReposByUser(username: String) {
        gitHubRepoDao.deleteReposByUser(username)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getReposPager(username: String): Flow<PagingData<GitHubRepo>> {
        Timber.d("Creating Pager for GitHub Repos of user: $username")
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = GitHubRepoRemoteMediator(
                apiService = gitHubApiService,
                repoDao = gitHubRepoDao,
                remoteKeysDao = remoteKeysDao,
                username = username
            ),
            pagingSourceFactory = { gitHubRepoDao.getReposByUser(username) }
        ).flow
    }

}
