package com.example.bename.data

import com.example.bename.data.source.local.BenameDao
import com.example.bename.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPersonRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: BenameDao,
//    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
//    @ApplicationScope private val scope: CoroutineScope,
) : PersonRepository {
    override fun getPeopleStream(): Flow<List<Person>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPeople(): List<Person> {
        TODO("Not yet implemented")
    }

    override fun getPersonStream(personId: Long): Flow<Person> {
        TODO("Not yet implemented")
    }

    override suspend fun getPerson(personId: Long): Person? {
        TODO("Not yet implemented")
    }

    override suspend fun createPerson(firstName: String, desc: String): Long {
        val personId = UUID.randomUUID().toString().toLong()
        val person = Person(
            firstName = firstName,
            desc = desc,
            id = personId,
            lastName = "",
            hidden = false,
            created = Date(),
            updated = Date(),
        )
        localDataSource.upsert(person.toLocal())
        //savePeopleToNetwork()
        return personId
    }

    override suspend fun updatePerson(person: Person): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deletePerson(personId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPeople(): Int {
        TODO("Not yet implemented")
    }
}