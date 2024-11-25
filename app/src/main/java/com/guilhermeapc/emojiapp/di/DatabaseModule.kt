// DatabaseModule.kt
package com.guilhermeapc.emojiapp.di

import android.content.Context
import androidx.room.Room
import com.guilhermeapc.emojiapp.data.AppDatabase
import com.guilhermeapc.emojiapp.data.EmojiDao
import com.guilhermeapc.emojiapp.data.GitHubRepoDao
import com.guilhermeapc.emojiapp.data.GitHubUserDao
import com.guilhermeapc.emojiapp.data.MIGRATION_1_2
import com.guilhermeapc.emojiapp.data.MIGRATION_2_3
import com.guilhermeapc.emojiapp.data.MIGRATION_3_4
import com.guilhermeapc.emojiapp.data.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context // inject application-level Context for db creation
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "emoji_database"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideEmojiDao(
        appDatabase: AppDatabase
    ): EmojiDao {
        return appDatabase.emojiDao()
    }

    @Provides
    fun provideGitHubUserDao(database: AppDatabase): GitHubUserDao {
        return database.gitHubUserDao()
    }

    @Provides
    fun provideGitHubRepoDao(database: AppDatabase): GitHubRepoDao {
        return database.gitHubRepoDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}
