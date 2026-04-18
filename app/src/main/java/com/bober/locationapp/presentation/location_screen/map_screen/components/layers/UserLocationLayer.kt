package com.bober.locationapp.presentation.location_screen.map_screen.components.layers

import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bober.locationapp.R
import com.bober.locationapp.presentation.location_screen.map_screen.components.rememberDeviceRotation
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

@Composable
fun UserLocationLayer(
    userPosition: Position
) {
    val headingDegrees = rememberDeviceRotation()

    val userSource = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            FeatureCollection(
                features = listOf(
                    Feature(
                        geometry = Point(userPosition),
                        properties = JsonObject(emptyMap())
                    )
                )
            )
        )
    )

//    CircleLayer(
//        id = "user-location-halo",
//        source = userSource,
//        color = const(Color.Blue.copy(alpha = 0.2f)),
//        radius = const(15.dp)
//    )

    SymbolLayer(
        id = "user-location-heading-icon",
        source = userSource,
        iconImage = image(
            painterResource(R.drawable.baseline_navigation_24),
            drawAsSdf = true
        ),
        iconColor = const(Color.White),
        iconOpacity = const(0.85f),
        iconSize = const(0.9f),
        iconRotate = const(headingDegrees),
        iconAllowOverlap = const(true),
    )

    SymbolLayer(
        id = "user-location-heading-icon-outline",
        source = userSource,
        iconImage = image(
            painterResource(R.drawable.outline_navigation_24),
            drawAsSdf = true
        ),
        iconColor = const(Color.Blue),
        iconOpacity = const(0.85f),
        iconSize = const(1.0f),
        iconRotate = const(headingDegrees),
        iconAllowOverlap = const(true),
    )
}