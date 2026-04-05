package com.bober.locationapp.location_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.data.position
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun MapScreen(latitude: Double?, longitude: Double?) {

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

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
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    ) { mv ->
        mv.getMapAsync { map ->
            map.setStyle("https://basemaps.cartocdn.com/gl/positron-gl-style/style.json")

            var newPoint: Marker? = null

            if (latitude != null && longitude != null) {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng = LatLng(latitude, longitude),
                        zoom = 13.0
                    )
                )
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(latitude, longitude))
                )
                map.addOnMapClickListener { point ->

                    if (newPoint != null) {
                        newPoint?.position = point
                    } else {
                        newPoint = map.addMarker(
                            MarkerOptions()
                                .position(point)
                        )
                    }
                    true
                }
            }
        }
    }
}