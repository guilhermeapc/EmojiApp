// data/AppDatabase.kt
package com.guilhermeapc.emojiapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.model.GitHubRepo
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.model.OwnerTypeConverter
import com.guilhermeapc.emojiapp.model.RemoteKeys

@Database(
    entities = [Emoji::class, GitHubUser::class, GitHubRepo::class, RemoteKeys::class],
    version = 4, // Increment on schema change
    exportSchema = false
)
@TypeConverters(OwnerTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao
    abstract fun gitHubUserDao(): GitHubUserDao
    abstract fun gitHubRepoDao(): GitHubRepoDao
    abstract fun remoteKeysDao(): RemoteKeysDao // New DAO
}
