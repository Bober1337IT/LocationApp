package com.bober.locationapp.location_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.locationapp.location_tracker.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationTracker: LocationTracker
): ViewModel() {

    private val _state = mutableStateOf(LocationState())
    val state: State<LocationState> = _state

    fun loadLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val location = locationTracker.getCurrentLocation()

            if (location != null) {
                val city = locationTracker.getCurrentCity(location.latitude, location.longitude)

                _state.value = _state.value.copy(
                    location = location,
                    cityName = city ?: "Unknown City",
                    isLoading = false
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Could not retrieve location. Check GPS or Network."
                )
            }
        }
    }
}