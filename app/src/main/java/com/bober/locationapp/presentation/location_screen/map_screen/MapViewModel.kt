package com.bober.locationapp.presentation.location_screen.map_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.locationapp.domain.model.Pin
import com.bober.locationapp.domain.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.maplibre.spatialk.geojson.Position
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: PinRepository
) : ViewModel() {

    private val _state = mutableStateOf(MapState())
    val state: State<MapState> = _state

    init {
        observePins()
    }

    private fun observePins() {
        viewModelScope.launch {
            repository.getAllPinLocations().collectLatest { pinMarkers ->
                _state.value = _state.value.copy(pins = pinMarkers)
            }
        }
    }

    fun onMapLongClick(position: Position) {
        viewModelScope.launch {
            val pins = _state.value.pins
            val existingPin = pins.find {
                Math.abs(it.latitude - position.latitude) < 0.0005 &&
                        Math.abs(it.longitude - position.longitude) < 0.0005
            }

            if (existingPin != null) {
                repository.deletePinById(existingPin.id)
            } else {
                val newPin = Pin(
                    name = "New Point",
                    description = null,
                    latitude = position.latitude,
                    longitude = position.longitude,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertPin(newPin)
            }
        }
    }
}