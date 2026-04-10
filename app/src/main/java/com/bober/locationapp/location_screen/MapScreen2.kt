package com.bober.locationapp.location_screen

import android.location.Location
import androidx.compose.runtime.Composable
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.spatialk.geojson.Position

@Composable
fun MapScreen2(location: Location){

    val position = Position(location.longitude, location.latitude)

    val cameraState = rememberCameraState(
        CameraPosition(
            target = position,
            zoom = 13.0
        )
    )

    MaplibreMap(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        cameraState = cameraState,
        styleState = rememberStyleState(),
    ) {

    }
}