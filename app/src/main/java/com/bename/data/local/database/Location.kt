package com.bename.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float = 500f // meters, for geofencing
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface LocationDao {
    @Query("SELECT * FROM location ORDER BY name")
    fun getLocations(): Flow<List<Location>>

    @Insert
    suspend fun insertLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)
}