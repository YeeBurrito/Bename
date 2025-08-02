package com.bename.data

import com.bename.data.local.database.Location
import com.bename.data.local.database.LocationDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocationRepository {
    val locations: Flow<List<Location>>
    suspend fun add(location: Location)
    suspend fun remove(location: Location)
}

class DefaultLocationRepository @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {
    override val locations: Flow<List<Location>> = locationDao.getLocations()
    override suspend fun add(location: Location) = locationDao.insertLocation(location)
    override suspend fun remove(location: Location) = locationDao.deleteLocation(location)
}