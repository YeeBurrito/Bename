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

package com.bename.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.bename.data.PersonRepository
import com.bename.data.DefaultPersonRepository
import com.bename.data.DefaultTagRepository
import com.bename.data.TagRepository
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsPersonRepository(
        personRepository: DefaultPersonRepository
    ): PersonRepository

    @Singleton
    @Binds
    fun bindsTagRepository(
        tagRepository: DefaultTagRepository
    ): TagRepository
}

class FakePersonRepository @Inject constructor() : PersonRepository {
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
}

class FakeTagRepository @Inject constructor() : TagRepository {
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
}

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
