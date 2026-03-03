// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.bename.data.local.database.Person
import com.bename.data.local.database.PersonDao
import javax.inject.Inject

// Interface for person repository operations
interface PersonRepository {
    val persons: Flow<List<Person>> // Flow of all persons

    suspend fun add(person: Person) // Add a new person
    suspend fun update(person: Person) // Update an existing person
    suspend fun clearAll() // Remove all persons
    suspend fun remove(person: Person) // Remove a specific person
    suspend fun getAllPersons(): List<Person> // Get all persons as a list
}

// Default implementation of PersonRepository using Room DAO
class DefaultPersonRepository @Inject constructor(
    private val personDao: PersonDao
) : PersonRepository {

    // Flow of all persons from the database
    override val persons: Flow<List<Person>> =
        personDao.getPersons()

    // Insert a new person into the database
    override suspend fun add(person: Person) {
        personDao.insertPerson(person)
    }

    // Update an existing person in the database
    override suspend fun update(person: Person) {
        personDao.updatePerson(person)
    }

    // Delete all persons from the database
    override suspend fun clearAll() {
        personDao.clearAll()
    }

    // Delete a specific person from the database
    override suspend fun remove(person: Person) {
        personDao.delete(person)
    }

    // Get all persons as a list (not a Flow)
    override suspend fun getAllPersons(): List<Person> {
        return personDao.getAllPersons()
    }
}
