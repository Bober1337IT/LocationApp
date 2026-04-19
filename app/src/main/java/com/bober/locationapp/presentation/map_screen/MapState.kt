package com.bober.locationapp.presentation.map_screen

import com.bober.locationapp.domain.model.GeoCoordinate
import com.bober.locationapp.domain.model.Pin
import com.bober.locationapp.domain.model.PinMapMarker
import com.bober.locationapp.presentation.map_screen.components.pin_sheet.PinSheetMode

data class MapState(
    val location: GeoCoordinate? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pins: List<PinMapMarker> = emptyList(),
    val pin: Pin? = null,
    val pinSheetMode: PinSheetMode = PinSheetMode.DETAILS,
)