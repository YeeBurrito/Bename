// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data

import com.bename.data.local.database.Tag
import com.bename.data.local.database.TagDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Interface for tag repository operations
interface TagRepository {
    val tags: Flow<List<Tag>> // Flow of all tags

    suspend fun add(tag: Tag) // Add a new tag

    suspend fun update(tag: Tag) // Update an existing tag

    fun searchTags(query: String): Flow<List<Tag>> // Search tags by query

    suspend fun getAllTags(): List<Tag> // Get all tags as a list

    suspend fun delete(tag: Tag) // Delete a tag
}

// Default implementation of TagRepository using Room DAO
class DefaultTagRepository @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    // Flow of all tags from the database
    override val tags: Flow<List<Tag>> =
        tagDao.getTags()

    // Insert a new tag into the database
    override suspend fun add(tag: Tag) {
        tagDao.insertTag(tag)
    }

    // Update an existing tag in the database
    override suspend fun update(tag: Tag) {
        tagDao.updateTag(tag)
    }

    // Search for tags matching the query
    override fun searchTags(query: String): Flow<List<Tag>> {
        return tagDao.searchTags(query)
    }

    // Get all tags as a list (not a Flow)
    override suspend fun getAllTags(): List<Tag> = tags.first()

    // Delete a tag from the database
    override suspend fun delete(tag: Tag) {
        tagDao.deleteTag(tag)
    }
}