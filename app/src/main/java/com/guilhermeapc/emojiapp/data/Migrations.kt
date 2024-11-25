// data/Migrations.kt

/***
 * Migrations from versions of the database.
 * Alternatively, one could handle through destructive migrations for a sample app,
 * but this is not recommended for production use as it will probably
 * result in data loss.
 */
package com.guilhermeapc.emojiapp.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQL statement to create the new 'github_users' table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `github_users` (
                `login` TEXT PRIMARY KEY NOT NULL,
                `id` INTEGER NOT NULL,
                `avatar_url` TEXT NOT NULL
            )
        """.trimIndent())
    }
}
