package com.bober.locationapp.presentation.location_screen.map_screen

import com.bober.locationapp.domain.model.PinMapMarker

data class MapState(
    val pins: List<PinMapMarker> = emptyList()
)