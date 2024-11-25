// data/Migrations.kt

/***
 * Migrations from versions of the database.
 * Alternatively, one could handle changes with
 * destructive migrations seen as it is a sample app,
 * but this is not recommended for production use as it
 * will most likely result in data loss.
 */
package com.guilhermeapc.emojiapp.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQL statement to create the new 'github_users' table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `github_users` (
                `login` TEXT PRIMARY KEY NOT NULL,
                `id` INTEGER NOT NULL,
                `avatar_url` TEXT NOT NULL
            )
        """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
        CREATE TABLE IF NOT EXISTS `github_repos` (
            `id` INTEGER PRIMARY KEY NOT NULL,
            `full_name` TEXT NOT NULL,
            `private` INTEGER NOT NULL,
            `owner` JSON NOT NULL,
        )
    """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `remote_keys` (
                `username` TEXT NOT NULL,
                `nextKey` INTEGER,
                PRIMARY KEY(`username`)
            )
        """.trimIndent()
        )
    }
}
