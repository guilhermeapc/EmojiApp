// data/GitHubRepoDao.kt
package com.guilhermeapc.emojiapp.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guilhermeapc.emojiapp.model.GitHubRepo

// GitHubRepoDao.kt
@Dao
interface GitHubRepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<GitHubRepo>)

    @Query("DELETE FROM github_repos WHERE login = :username")
    suspend fun deleteReposByUser(username: String)

    @Query("SELECT * FROM github_repos WHERE login = :username ORDER BY id ASC")
    fun getReposByUser(username: String): PagingSource<Int, GitHubRepo>
}