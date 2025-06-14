package com.bename.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class Tag(
    val name: String,
    val locations: List<String> = emptyList(),
    val personIds: List<Int> = emptyList(),
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tag ORDER BY uid DESC") // LIMIT 10
    fun getTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tag WHERE name LIKE '%' || :query || '%' ORDER BY uid DESC LIMIT 10")
    fun searchTags(query: String): Flow<List<Tag>>

    @Insert
    suspend fun insertTag(item: Tag)

    @Update
    suspend fun updateTag(item: Tag)
}