package com.bober.locationapp.location_screen.map_screen

import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bober.locationapp.location_screen.map_screen.components.UserLocationLayer
import com.bober.locationapp.location_screen.map_screen.components.PinLayer
import com.bober.locationapp.location_screen.map_screen.components.rotation.rememberDeviceRotation
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
fun MapScreen2(location: Location?) {

    // Current user position from the location object
    val userPosition = remember(location) {
        location?.let { Position(it.longitude, it.latitude) } ?: Position(0.0, 0.0)
    }

    // State for tracking if camera is locked to user
    var fixedCamera by remember { mutableStateOf(true) }
    var isProgrammaticMovement by remember { mutableStateOf(false) }

    // State for the dropped marker
    var droppedPinPosition by remember { mutableStateOf<Position?>(null) }

    val rotation = rememberDeviceRotation()

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
                onMapClick = { position, _ ->
                    droppedPinPosition = position
                    ClickResult.Pass
                }
            ) {
                location?.let {
                    UserLocationLayer(userPosition = userPosition)
                }

                droppedPinPosition?.let { pinPos ->
                    PinLayer(pinPosition = pinPos)
                }
            }

            location?.let {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 32.dp)
                        .size(48.dp)
                        .rotate(rotation)
                )
            }
        }
    }
}