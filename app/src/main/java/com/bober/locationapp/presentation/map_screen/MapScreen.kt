package com.bober.locationapp.presentation.map_screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bober.locationapp.presentation.map_screen.components.layers.UserLocationLayer
import com.bober.locationapp.presentation.map_screen.components.layers.PinLayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bober.locationapp.presentation.map_screen.components.PinDetailsSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {

    val state by viewModel.state
    val sheetState = rememberModalBottomSheetState()

    var editedName by remember(state.pin) { mutableStateOf(state.pin?.name ?: "") }
    var editedDescription by remember(state.pin) { mutableStateOf(state.pin?.description ?: "") }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.startTracking()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startTracking()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopTracking()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val userPosition = remember(state.location) {
        state.location?.let { Position(it.longitude, it.latitude) } ?: Position(0.0, 0.0)
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
        if (fixedCamera && state.location != null) {
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

            state.pin?.let { currentPin ->
                PinDetailsSheet(
                    pin = currentPin,
                    isEditing = state.isEditingPin,
                    sheetState = sheetState,
                    onDismissRequest = { viewModel.dismissPinDetails() },
                    onToggleEdit = { viewModel.toggleEditMode() },
                    onSave = { name, description, color ->
                        viewModel.updatePin(name, description, color)
                    }
                )
            }
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
                    viewModel.addRemovePin(position.latitude, position.longitude)
                    ClickResult.Pass
                },
                onMapClick = { position, _ ->
                    viewModel.showPinDetails(position.latitude, position.longitude)
                    ClickResult.Pass
                }
            ) {
                state.location?.let {
                    UserLocationLayer(
                        userPosition = userPosition
                    )
                }

                state.pins.forEach { pin ->
                    key(pin.id) {
                        PinLayer(
                            id = pin.id.toString(),
                            pinPosition = Position(pin.longitude, pin.latitude),
                            color = pin.color,
                            zoom = cameraState.position.zoom
                        )
                    }
                }
            }
        }
    }
}