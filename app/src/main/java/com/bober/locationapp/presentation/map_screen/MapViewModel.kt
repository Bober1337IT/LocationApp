package com.bober.locationapp.presentation.map_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.locationapp.domain.model.Pin
import com.bober.locationapp.domain.repository.PinRepository
import com.bober.locationapp.domain.repository.UserLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: PinRepository,
    private val userLocationRepository: UserLocationRepository,
) : ViewModel() {

    private val _state = mutableStateOf(MapState())
    val state: State<MapState> = _state

    private var trackingJob: Job? = null

    init {
        observePins()
    }

    fun startTracking() {
        trackingJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            userLocationRepository.observeLocationUpdates().collectLatest { coordinate ->
                if (coordinate != null) {
                    _state.value = _state.value.copy(
                        location = coordinate,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "GPS is off or no permissions."
                    )
                }
            }
        }
    }
    fun stopTracking() {
        trackingJob?.cancel()
        _state.value = _state.value.copy(isLoading = false)
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }

    private fun observePins() {
        viewModelScope.launch {
            repository.getAllPinLocations().collectLatest { pinMarkers ->
                _state.value = _state.value.copy(pins = pinMarkers)
            }
        }
    }

    fun addRemovePin(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val pins = _state.value.pins
            val existingPin = pins.find {
                Math.abs(it.latitude - latitude) < 0.0005 &&
                    Math.abs(it.longitude - longitude) < 0.0005
            }

            if (existingPin != null) {
                repository.deletePinById(existingPin.id)
            } else {
                val newPin = Pin(
                    name = "",
                    city = userLocationRepository.resolveCityName(latitude, longitude) ?: "Unknown",
                    description = null,
                    color = Color.Blue.toColorLong(),
                    latitude = latitude,
                    longitude = longitude,
                    createdAt = System.currentTimeMillis(),
                )
                val generatedId = repository.insertPin(newPin)
                _state.value = _state.value.copy(
                    pin = newPin.copy(id = generatedId),
                    isEditingPin = true
                )
            }
        }
    }

    fun showPinDetails(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val pins = _state.value.pins
            val existingPinMarker = pins.find {
                Math.abs(it.latitude - latitude) < 0.0005 &&
                        Math.abs(it.longitude - longitude) < 0.0005
            }
            if (existingPinMarker != null) {
                val fullPin = repository.getPinDetailsById(existingPinMarker.id)
                _state.value = _state.value.copy(pin = fullPin)
            } else {
                _state.value = _state.value.copy(pin = null, isEditingPin = false)
            }
        }
    }

    fun updatePin(name: String, description: String?) {

        val currentPin = _state.value.pin ?: return
        val updatedPin = currentPin.copy(name = name.ifBlank { "Unnamed Point" }, description = description)

        viewModelScope.launch {
            repository.insertPin(updatedPin)
            _state.value = _state.value.copy(pin = updatedPin, isEditingPin = false)
        }
    }

    fun dismissPinDetails() {
        _state.value = _state.value.copy(pin = null, isEditingPin = false)
    }


    fun toggleEditMode() {
        _state.value = _state.value.copy(isEditingPin = !_state.value.isEditingPin)
    }
}