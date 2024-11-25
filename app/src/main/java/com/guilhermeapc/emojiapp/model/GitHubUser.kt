// models/GitHubUser.kt
package com.guilhermeapc.emojiapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_users")
data class GitHubUser(
    @PrimaryKey val login: String, // GitHub username
    val id: Int,
    val avatar_url: String
)
