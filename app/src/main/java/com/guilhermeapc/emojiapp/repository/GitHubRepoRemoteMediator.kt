// repository/GitHubRepoRemoteMediator.kt
package com.guilhermeapc.emojiapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.guilhermeapc.emojiapp.data.GitHubRepoDao
import com.guilhermeapc.emojiapp.data.RemoteKeysDao
import com.guilhermeapc.emojiapp.model.GitHubRepo
import com.guilhermeapc.emojiapp.model.RemoteKeys
import com.guilhermeapc.emojiapp.network.GitHubApiService
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GitHubRepoRemoteMediator(
    private val apiService: GitHubApiService,
    private val repoDao: GitHubRepoDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val username: String
) : RemoteMediator<Int, GitHubRepo>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GitHubRepo>
    ): MediatorResult {
        // Determine the page number to load
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = remoteKeysDao.remoteKeysByUsername(username)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                // Not needed for GitHub repos
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = remoteKeysDao.remoteKeysByUsername(username)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        try {
            Timber.d("RemoteMediator: Loading page $page for user $username")

            // Fetch data from GitHub API
            val response = apiService.getRepos(username, page, state.config.pageSize)
            val repos = response.map { repoResponse ->
                GitHubRepo(
                    id = repoResponse.id,
                    full_name = repoResponse.full_name,
                    private = repoResponse.private,
                    owner = repoResponse.owner,
                    login = repoResponse.owner.login,
                )
            }

            val endOfPaginationReached = repos.isEmpty()
            Timber.d("RemoteMediator: Loaded ${repos.size} repos. End of pagination: $endOfPaginationReached")

            if (loadType == LoadType.REFRESH) {
                // Clear existing data on refresh
                remoteKeysDao.clearRemoteKeys()
                repoDao.deleteReposByUser(username)
            }

            // Calculate the next key
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = RemoteKeys(user = username, nextKey = nextKey)
            remoteKeysDao.insertAll(listOf(keys))

            // Insert the fetched repos into the database
            repoDao.insertRepos(repos)

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            Timber.e(e, "RemoteMediator: IOException during load")
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            Timber.e(e, "RemoteMediator: HttpException during load")
            return MediatorResult.Error(e)
        }
    }
}
