// data/GitHubUserDao.kt
package com.guilhermeapc.emojiapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guilhermeapc.emojiapp.model.GitHubUser
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubUserDao {

    @Query("SELECT * FROM github_users WHERE login = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): GitHubUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: GitHubUser)

    @Delete
    suspend fun deleteUser(user: GitHubUser)

    @Query("SELECT * FROM github_users")
    fun getAllUsers(): Flow<List<GitHubUser>>
}
