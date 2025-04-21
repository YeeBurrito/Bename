package com.example.bename.data

import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getPeopleStream(): Flow<List<Person>>

    suspend fun getPeople(): List<Person>

    fun getPersonStream(personId: Long): Flow<Person>

    suspend fun getPerson(personId: Long): Person?

    suspend fun createPerson(firstName: String, desc: String): Long

    suspend fun updatePerson(person: Person): Int

    suspend fun deletePerson(personId: Long): Int

    suspend fun deleteAllPeople(): Int
}