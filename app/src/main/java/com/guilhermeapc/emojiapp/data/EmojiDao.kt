// EmojiDao.kt
package com.guilhermeapc.emojiapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guilhermeapc.emojiapp.model.Emoji
import kotlinx.coroutines.flow.Flow

@Dao
interface EmojiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(emojis: List<Emoji>)

    @Query("SELECT * FROM emojis")
    fun getAllEmojis(): Flow<List<Emoji>>

    @Query("DELETE FROM emojis")
    suspend fun deleteAllEmojis()
}
