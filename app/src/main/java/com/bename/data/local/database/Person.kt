/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bename.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class Person(
    val firstName: String,
    val lastName: String? = null,
    val description: String? = null,
    var hidden: Boolean = false,
    var tagIds: List<Int> = emptyList(),
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface PersonDao {
    // Get the last 10 persons ordered by uid in descending order
    @Query("SELECT * FROM person ORDER BY uid DESC") // LIMIT 10
    fun getPersons(): Flow<List<Person>>

    // Add a new person to the database
    @Insert
    suspend fun insertPerson(item: Person)

    @Update
    suspend fun updatePerson(item: Person)

    @Query("DELETE FROM person")
    suspend fun clearAll()

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM person")
    suspend fun getAllPersons(): List<Person>
}
