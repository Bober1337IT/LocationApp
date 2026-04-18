package com.bober.locationapp.presentation.location_screen.map_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bober.locationapp.domain.model.GeoCoordinate
import com.bober.locationapp.presentation.location_screen.map_screen.components.layers.UserLocationLayer
import com.bober.locationapp.presentation.location_screen.map_screen.components.layers.PinLayer
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MapScreen(
    location: GeoCoordinate?,
    viewModel: MapViewModel = hiltViewModel()
) {

    val state by viewModel.state

    // Current user position from the location object
    val userPosition = remember(location) {
        location?.let { Position(it.longitude, it.latitude) } ?: Position(0.0, 0.0)
    }

    // State for tracking if camera is locked to user
    var fixedCamera by remember { mutableStateOf(true) }
    var isProgrammaticMovement by remember { mutableStateOf(false) }

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = userPosition,
            zoom = 15.0
        )
    )

    // Effect to handle "Fixed Camera" logic (animates when location changes)
    LaunchedEffect(userPosition, fixedCamera) {
        if (fixedCamera && location != null) {
            isProgrammaticMovement = true
            cameraState.animateTo(
                finalPosition = CameraPosition(target = userPosition, zoom = 15.0),
                duration = 1000.milliseconds
            )
            isProgrammaticMovement = false
        }
    }

    // Effect to detect manual camera movement to unlock the FAB
    LaunchedEffect(cameraState.isCameraMoving) {
        if (cameraState.isCameraMoving && !isProgrammaticMovement) {
            fixedCamera = false
        }
    }
    Box(Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                if (!fixedCamera) {
                    FloatingActionButton(
                        onClick = { fixedCamera = true },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Center Map"
                        )
                    }
                }
            }
        ) { paddingValues ->
            MaplibreMap(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),

                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
                cameraState = cameraState,
                styleState = rememberStyleState(),
                options = MapOptions(
                    ornamentOptions = OrnamentOptions(
                        isLogoEnabled = false,
                        isAttributionEnabled = false,
                        isScaleBarEnabled = false
                    ),
                ),
                onMapLongClick = { position, _ ->
                    viewModel.onMapLongClick(position.latitude, position.longitude)
                    ClickResult.Pass
                },
            ) {
                location?.let {
                    UserLocationLayer(
                        userPosition = userPosition
                    )
                }

                state.pins.forEach { pin ->
                    key(pin.id) {
                        PinLayer(
                            id = pin.id.toString(),
                            pinPosition = Position(pin.longitude, pin.latitude)
                        )
                    }
                }
            }
        }
    }
}