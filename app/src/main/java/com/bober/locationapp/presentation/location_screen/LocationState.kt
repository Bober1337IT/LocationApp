package com.bober.locationapp.presentation.location_screen

import android.location.Location

data class LocationState(
    val location: Location? = null,
    val cityName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
