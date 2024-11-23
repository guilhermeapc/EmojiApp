// AppDatabase.kt
package com.guilhermeapc.emojiapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guilhermeapc.emojiapp.model.Emoji

/***
 * entities: The list of ROOM entities (tables) managed by the database.
 * version: The version number of the database. Increment on schema changes.
 * exportSchema: When set to 'false', ROOM won't export the schema to a folder. 'true' if to keep a version history.
 ***/
@Database(entities = [Emoji::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao
}