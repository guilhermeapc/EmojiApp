// models/RemoteKeys.kt
package com.guilhermeapc.emojiapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val user: String, // e.g., "google"
    val nextKey: Int?
)
