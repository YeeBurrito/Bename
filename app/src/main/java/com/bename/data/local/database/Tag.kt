// Created by Ethan Temple ("YeeBurrito") 2026
package com.bename.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Room entity representing a tag
@Entity
data class Tag(
    val name: String,                    // Name of the tag
    val locations: List<String> = emptyList(), // List of location names associated with this tag
    val personIds: List<Int> = emptyList(),    // List of person IDs associated with this tag
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0                     // Unique ID for the tag
}

// Data Access Object for Tag entity
@Dao
interface TagDao {
    // Returns all tags ordered by uid in descending order as a Flow
    @Query("SELECT * FROM tag ORDER BY uid DESC") // LIMIT 10
    fun getTags(): Flow<List<Tag>>

    // Returns all tags as a list (not a Flow)
    @Query("SELECT * FROM tag ORDER BY uid DESC")
    suspend fun getAllTags(): List<Tag>

    // Searches tags by name containing the query string, returns up to 10 results
    @Query("SELECT * FROM tag WHERE name LIKE '%' || :query || '%' ORDER BY uid DESC LIMIT 10")
    fun searchTags(query: String): Flow<List<Tag>>

    // Inserts a new tag into the database
    @Insert
    suspend fun insertTag(item: Tag)

    // Updates an existing tag in the database
    @Update
    suspend fun updateTag(item: Tag)

    // Returns tags associated with a specific locationId
    @Query("""
    SELECT * FROM tag
    WHERE locations = :locationId
       OR locations LIKE :locationId || ';;%'
       OR locations LIKE '%;;' || :locationId || ';;%'
       OR locations LIKE '%;;' || :locationId
""")
    fun getTagsForLocation(locationId: String): List<Tag>

    // Deletes a tag from the database
    @Delete
    suspend fun deleteTag(tag: Tag)
}