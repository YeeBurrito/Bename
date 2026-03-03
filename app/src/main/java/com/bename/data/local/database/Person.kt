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

// Room entity representing a person
@Entity
data class Person(
    val firstName: String,              // Person's first name
    val lastName: String? = null,       // Person's last name (optional)
    val description: String? = null,    // Description or notes (optional)
    var hidden: Boolean = false,        // Whether the person is hidden
    var tagIds: List<Int> = emptyList() // List of tag IDs associated with this person
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0                    // Unique ID for the person
}

// Data Access Object for Person entity
@Dao
interface PersonDao {
    // Returns all persons ordered by uid in descending order as a Flow
    @Query("SELECT * FROM person ORDER BY uid DESC") // LIMIT 10
    fun getPersons(): Flow<List<Person>>

    // Inserts a new person into the database
    @Insert
    suspend fun insertPerson(item: Person)

    // Updates an existing person in the database
    @Update
    suspend fun updatePerson(item: Person)

    // Deletes all persons from the database
    @Query("DELETE FROM person")
    suspend fun clearAll()

    // Deletes a specific person from the database
    @Delete
    suspend fun delete(person: Person)

    // Returns all persons as a list (not a Flow)
    @Query("SELECT * FROM person")
    suspend fun getAllPersons(): List<Person>
}
