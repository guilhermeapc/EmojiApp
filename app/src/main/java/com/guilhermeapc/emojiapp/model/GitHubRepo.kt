// models/GitHubRepo.kt
package com.guilhermeapc.emojiapp.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "github_repos",
    indices = [Index(value = ["login"])]
)
data class GitHubRepo(
    @PrimaryKey val id: Int,
    val full_name: String,
    val private: Boolean,
    val owner: Owner,
    val login: String
)

data class Owner(
    val login: String,
    val avatar_url: String
)

class OwnerTypeConverter {

    @TypeConverter
    fun fromOwner(owner: Owner?): String? {
        return Gson().toJson(owner) // Convert Owner to JSON string
    }

    @TypeConverter
    fun toOwner(ownerString: String?): Owner? {
        return Gson().fromJson(ownerString, Owner::class.java) // Convert JSON string to Owner
    }
}
