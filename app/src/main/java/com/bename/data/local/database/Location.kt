// Created by Ethan Temple ("YeeBurrito") 2026
package com.bename.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Room entity representing a location for geofencing
@Entity
data class Location(
    val name: String,           // Name of the location
    val latitude: Double,       // Latitude coordinate
    val longitude: Double,      // Longitude coordinate
    val radius: Float = 500f    // Geofence radius in meters
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0           // Unique ID for the location
}

// Data Access Object for Location entity
@Dao
interface LocationDao {
    // Returns all locations ordered by name as a Flow
    @Query("SELECT * FROM location ORDER BY name")
    fun getLocations(): Flow<List<Location>>

    // Inserts a new location into the database
    @Insert
    suspend fun insertLocation(location: Location)

    // Deletes a location from the database
    @Delete
    suspend fun deleteLocation(location: Location)
}