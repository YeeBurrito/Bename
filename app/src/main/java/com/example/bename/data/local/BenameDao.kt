package com.example.bename.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BenameDao {

    /**
     * Observes list of people
     *
     * @return all people
     */
    @Query("SELECT * FROM person")
    fun observeAll(): Flow<List<LocalPerson>>

    /**
     * Observes a single person
     *
     * @param personId the person id
     * @return the person with personId
     */
    @Query("SELECT * FROM person WHERE id = :personId")
    fun observeById(personId: Long): Flow<LocalPerson>

    /**
     * Select all people from the people table
     *
     * @return all people
     */
    @Query("SELECT * FROM person")
    suspend fun getAll(): List<LocalPerson>

    /**
     * Select a person by id
     *
     * @param personId the person id
     * @return the person with personId
     */
    @Query("SELECT * FROM person WHERE id = :personId")
    suspend fun getById(personId: Long): LocalPerson?

    /**
     * Insert or update a person in the database. If a person already exists, replace it.
     *
     * @param person the person to be inserted or updated
     */
    @Upsert
    suspend fun upsert(person: LocalPerson)

    /**
     * Insert or update people in the database. If a person already exists, replace it.
     *
     * @param people the people to be inserted or updated
     */
    @Upsert
    suspend fun upsertAll(people: List<LocalPerson>)

    /**
     * Delete a person by id
     *
     * @return the number of people deleted. This should always be 1
     */
    @Query("DELETE FROM person WHERE id = :personId")
    suspend fun deleteById(personId: Long): Int

    /**
     * Delete all people
     *
     * @return the number of people deleted
     */
    @Query("DELETE FROM person")
    suspend fun deleteAll(): Int
}