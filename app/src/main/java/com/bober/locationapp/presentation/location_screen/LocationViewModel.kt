package com.bober.locationapp.presentation.location_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.locationapp.domain.repository.UserLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val userLocationRepository: UserLocationRepository
) : ViewModel() {

    private val _state = mutableStateOf(LocationState())
    val state: State<LocationState> = _state

    private var trackingJob: Job? = null

    fun startTracking() {
        trackingJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            userLocationRepository.observeLocationUpdates().collectLatest { coordinate ->
                if (coordinate != null) {
                    val city = userLocationRepository.resolveCityName(
                        coordinate.latitude,
                        coordinate.longitude
                    )
                    _state.value = _state.value.copy(
                        location = coordinate,
                        cityName = city ?: "Unknown City",
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
}