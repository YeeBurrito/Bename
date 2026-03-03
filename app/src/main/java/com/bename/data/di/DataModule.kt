// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data.di

import com.bename.data.DefaultLocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.bename.data.PersonRepository
import com.bename.data.DefaultPersonRepository
import com.bename.data.DefaultTagRepository
import com.bename.data.LocationRepository
import com.bename.data.TagRepository
import com.bename.data.local.database.Location
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag
import javax.inject.Inject
import javax.inject.Singleton

// Dagger Hilt module to provide repository implementations
@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    // Binds DefaultPersonRepository as the implementation for PersonRepository
    @Singleton
    @Binds
    fun bindsPersonRepository(
        personRepository: DefaultPersonRepository
    ): PersonRepository

    // Binds DefaultTagRepository as the implementation for TagRepository
    @Singleton
    @Binds
    fun bindsTagRepository(
        tagRepository: DefaultTagRepository
    ): TagRepository

    // Binds DefaultLocationRepository as the implementation for LocationRepository
    @Singleton
    @Binds
    fun bindsLocationRepository(
        locationRepository: DefaultLocationRepository
    ): LocationRepository
}

// Fake implementation of PersonRepository for testing purposes
class FakePersonRepository @Inject constructor() : PersonRepository {
    // Emits a static list of fake persons
    override val persons: Flow<List<Person>> = flowOf(fakePersons)

    override suspend fun add(person: Person) {
        throw NotImplementedError()
    }

    override suspend fun update(person: Person) {
        throw NotImplementedError()
    }

    override suspend fun clearAll() {
        throw NotImplementedError()
    }

    override suspend fun remove(person: Person) {
        throw NotImplementedError()
    }

    override suspend fun getAllPersons(): List<Person> {
        throw NotImplementedError()
    }
}

// Fake implementation of TagRepository for testing purposes
class FakeTagRepository @Inject constructor() : TagRepository {
    // Emits a static list of fake tags
    override val tags: Flow<List<Tag>> = flowOf(fakeTags)

    override suspend fun add(tag: Tag) {
        throw NotImplementedError()
    }

    override suspend fun update(tag: Tag) {
        throw NotImplementedError()
    }

    override fun searchTags(query: String): Flow<List<Tag>> {
        throw NotImplementedError()
    }

    override suspend fun getAllTags(): List<Tag> {
        throw NotImplementedError()
    }

    override suspend fun delete(tag: Tag) {
        throw NotImplementedError()
    }
}
// Fake implementation of LocationRepository for testing purposes
class FakeLocationRepository @Inject constructor() : LocationRepository {
    // Emits a static list of fake locations
    override val locations: Flow<List<Location>> = flowOf(fakeLocations)

    override suspend fun add(location: Location) {
        throw NotImplementedError()
    }

    override suspend fun remove(location: Location) {
        throw NotImplementedError()
    }
}

// Sample fake data for testing
val fakePersons = listOf(
    Person(
        firstName = "John",
        lastName = "Doe",
        description = "A sample person",
        hidden = false,
        tagIds = listOf(1, 2)
    ),
    Person(
        firstName = "Jane",
        lastName = "Smith",
        description = "Another sample person",
        hidden = false,
        tagIds = listOf(2)
    ),
    Person(
        firstName = "Alice",
        lastName = "Johnson",
        description = "Yet another sample person",
        hidden = true,
        tagIds = emptyList()
    )
)

val fakeTags = listOf(
    Tag(
        name = "Important",
        locations = listOf("Home", "Work"),
        personIds = listOf(1, 2),
    ),
    Tag(
        name = "Urgent",
        locations = listOf("Office"),
        personIds = listOf(1),
    )
)

val fakeLocations = listOf(
    Location(
        name = "Home",
        latitude = 37.4219983,
        longitude = -122.084,
        radius = 100f
    ),
    Location(
        name = "Work",
        latitude = 37.422,
        longitude = -122.085,
        radius = 150f
    )
)
