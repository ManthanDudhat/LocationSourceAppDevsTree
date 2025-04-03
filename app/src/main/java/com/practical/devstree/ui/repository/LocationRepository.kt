package com.practical.devstree.ui.repository

import com.practical.devstree.db.dao.LocationDao
import com.practical.devstree.db.entity.LocationInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private var locationDao: LocationDao,
) {

    fun addLocation(locationInfo: LocationInfo): Flow<Unit> = flow {
        locationDao.addLocation(locationInfo)
    }

    fun updateLocation(
        id: Int,
        primaryAddress: String,
        city: String,
        latitude: Double,
        longitude: Double,
        distance:Double
    ): Flow<Unit> = flow {
        locationDao.updateLocation(id, primaryAddress, city, latitude, longitude, distance)
    }

    fun deleteLocation(locationInfo: LocationInfo): Flow<Unit> = flow {
        locationDao.deleteLocation(locationInfo)
    }

    fun getAllLocations(): Flow<List<LocationInfo>> {
        return locationDao.getAllLocations()
    }

    fun getLocationsDistanceAscending(): Flow<List<LocationInfo>> {
        return locationDao.getLocationsDistanceAscending()
    }

    fun getLocationsDistanceDescending(): Flow<List<LocationInfo>> {
        return locationDao.getLocationsDistanceDescending()
    }

    fun markAsPrimary(id: Int): Int {
        return locationDao.markAsPrimary(id)
    }

    suspend fun removePrimaryMark() {
        return locationDao.removePrimaryMark()
    }

}