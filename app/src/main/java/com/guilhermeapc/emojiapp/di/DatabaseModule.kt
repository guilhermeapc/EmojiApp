// DatabaseModule.kt
package com.guilhermeapc.emojiapp.di

import android.content.Context
import androidx.room.Room
import com.guilhermeapc.emojiapp.data.AppDatabase
import com.guilhermeapc.emojiapp.data.EmojiDao
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
        ).build()
    }

    @Provides
    fun provideEmojiDao(
        appDatabase: AppDatabase
    ): EmojiDao {
        return appDatabase.emojiDao()
    }
}
