package com.practical.devstree.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practical.devstree.db.entity.LocationInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(locationInfo: LocationInfo)

    @Query("UPDATE location SET primaryAddress = :primaryAddress, city = :city,latitude = :latitude ,longitude = :longitude, distance = :distance WHERE id = :id")
    suspend fun updateLocation(
        id: Int,
        primaryAddress: String,
        city: String,
        latitude: Double,
        longitude: Double,
        distance: Double
    )

    @Delete
    fun deleteLocation(location: LocationInfo)

    @Query("SELECT * FROM location")
    fun getAllLocations(): Flow<List<LocationInfo>>

    @Query("SELECT * FROM location ORDER BY distance ASC")
    fun getLocationsDistanceAscending(): Flow<List<LocationInfo>>

    @Query("SELECT * FROM location ORDER BY distance DESC")
    fun getLocationsDistanceDescending(): Flow<List<LocationInfo>>

    @Query("UPDATE location SET isPrimary = 1 WHERE id = :id")
    fun markAsPrimary(id: Int): Int

    @Query("UPDATE location SET isPrimary = 0")
    suspend fun removePrimaryMark()
}