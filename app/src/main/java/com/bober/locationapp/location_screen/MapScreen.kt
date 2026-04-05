package com.bober.locationapp.location_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.gestures.MoveGestureDetector
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(latitude: Double?, longitude: Double?) {

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isFirstUpdate by remember { mutableStateOf(true) }
    var fixedCamera by remember { mutableStateOf(true) }

    remember {
        MapLibre.getInstance(context)
    }

    val mapView = remember {
        MapView(context)
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
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
                            contentDescription = "Center"
                        )
                    }
                }
            }
    ) { paddingValues ->
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { map ->

                        var newPoint: Marker? = null

                        map.setStyle("https://basemaps.cartocdn.com/gl/positron-gl-style/style.json") { style ->
                            val locationComponent = map.locationComponent

                            val activationOptions = LocationComponentActivationOptions
                                .builder(context, style)
                                .useDefaultLocationEngine(true)
                                .build()

                            locationComponent.activateLocationComponent(activationOptions)
                            locationComponent.isLocationComponentEnabled = true
                            locationComponent.renderMode = RenderMode.COMPASS
                        }

                        map.addOnMoveListener(object : MapLibreMap.OnMoveListener {
                            override fun onMoveBegin(detector: MoveGestureDetector) {
                                fixedCamera = false
                            }
                            override fun onMove(detector: MoveGestureDetector) {}
                            override fun onMoveEnd(detector: MoveGestureDetector) {}
                        })

                        map.addOnMapClickListener { point ->
                            if (newPoint != null) {
                                newPoint?.position = point
                            } else {
                                newPoint = map.addMarker(
                                    MarkerOptions().position(point)
                                )
                            }
                            true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { mv ->
                if (latitude != null && longitude != null) {
                    mv.getMapAsync { map ->
                        val userLatLng = LatLng(latitude, longitude)

                        if (isFirstUpdate) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13.0))
                            isFirstUpdate = false
                        } else if(fixedCamera) {
                            val currentZoom = map.cameraPosition.zoom
                            map.easeCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, currentZoom))
                        }


                    }
                }
            }
        )
    }
}

