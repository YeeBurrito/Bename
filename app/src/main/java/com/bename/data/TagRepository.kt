package com.bename.data

import com.bename.data.local.database.Tag
import com.bename.data.local.database.TagDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface TagRepository {
    val tags: Flow<List<Tag>>

    suspend fun add(tag: Tag)

    suspend fun update(tag: Tag)

    fun searchTags(query: String): Flow<List<Tag>>

    suspend fun getAllTags(): List<Tag>

    suspend fun delete(tag: Tag)
}

class DefaultTagRepository @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override val tags: Flow<List<Tag>> =
        tagDao.getTags()

    override suspend fun add(tag: Tag) {
        tagDao.insertTag(tag)
    }

    override suspend fun update(tag: Tag) {
        tagDao.updateTag(tag)
    }

    override fun searchTags(query: String): Flow<List<Tag>> {
        return tagDao.searchTags(query)
    }

    override suspend fun getAllTags(): List<Tag> = tags.first()

    override suspend fun delete(tag: Tag) {
        tagDao.deleteTag(tag)
    }
}