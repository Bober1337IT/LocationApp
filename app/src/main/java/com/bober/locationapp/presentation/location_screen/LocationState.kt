package com.bober.locationapp.presentation.location_screen

import com.bober.locationapp.domain.model.GeoCoordinate

data class LocationState(
    val location: GeoCoordinate? = null,
    val cityName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
