// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data

import com.bename.data.local.database.Location
import com.bename.data.local.database.LocationDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Interface for location repository operations
interface LocationRepository {
    val locations: Flow<List<Location>> // Flow of all locations
    suspend fun add(location: Location) // Add a new location
    suspend fun remove(location: Location) // Remove a location
}

// Default implementation of LocationRepository using Room DAO
class DefaultLocationRepository @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {
    override val locations: Flow<List<Location>> = locationDao.getLocations()
    override suspend fun add(location: Location) = locationDao.insertLocation(location)
    override suspend fun remove(location: Location) = locationDao.deleteLocation(location)
}