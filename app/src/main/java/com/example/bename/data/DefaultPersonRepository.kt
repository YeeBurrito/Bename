package com.example.bename.data

import com.example.bename.data.source.local.BenameDao
import com.example.bename.data.source.network.NetworkDataSource
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
}