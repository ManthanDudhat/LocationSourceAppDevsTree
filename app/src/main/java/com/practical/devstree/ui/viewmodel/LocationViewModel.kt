package com.practical.devstree.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practical.devstree.db.entity.LocationInfo
import com.practical.devstree.ui.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _addLocation = MutableSharedFlow<Unit?>()
    private val _updateLocation = MutableSharedFlow<Unit?>()
    private val _deleteLocation = MutableSharedFlow<Unit?>()

    private val _getAllLocations = MutableSharedFlow<List<LocationInfo>?>()
    val getAllLocationsFlow: SharedFlow<List<LocationInfo>?> = _getAllLocations.asSharedFlow()

    private val _getLocationsDistanceAsc = MutableSharedFlow<List<LocationInfo>?>()
    val getLocationsDistanceAscFlow: SharedFlow<List<LocationInfo>?> = _getLocationsDistanceAsc.asSharedFlow()

    private val _getLocationsDistanceDes = MutableSharedFlow<List<LocationInfo>?>()
    val getLocationsDistanceDesFlow: SharedFlow<List<LocationInfo>?> = _getLocationsDistanceDes.asSharedFlow()

    fun addLocation(locationInfo: LocationInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.addLocation(locationInfo).collect { response ->
                _addLocation.emit(response)
            }
        }
    }

    fun updateLocation(
        id: Int,
        primaryAddress: String,
        city: String,
        latitude: Double,
        longitude: Double,
        distance : Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.updateLocation(id, primaryAddress, city, latitude, longitude, distance)
                .collect { response ->
                    _updateLocation.emit(response)
                }
        }
    }

      fun deleteLocation(locationInfo: LocationInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.deleteLocation(locationInfo).collect { response ->
                _deleteLocation.emit( response)
            }
        }
    }

    fun getAllLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getAllLocations().collect { response ->
                _getAllLocations.emit(response)
            }
        }
    }

    fun getLocationsDistanceAscending() {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getLocationsDistanceAscending().collect { response ->
                _getLocationsDistanceAsc.emit(response)
            }
        }
    }

    fun getLocationsDistanceDescending() {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getLocationsDistanceDescending().collect { response ->
                _getLocationsDistanceDes.emit(response)
            }
        }
    }

    fun markAsPrimary(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.markAsPrimary(id)
        }
    }

    fun removePrimaryMark() {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.removePrimaryMark()
        }
    }
}