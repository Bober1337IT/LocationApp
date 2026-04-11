package com.bober.locationapp.location_screen.map_screen.components.layers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.spatialk.geojson.FeatureCollection
import kotlinx.serialization.json.JsonObject

@Composable
fun UserLocationLayer(userPosition: Position) {

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

    CircleLayer(
        id = "user-location-halo",
        source = userSource,
        color = const(Color.Blue.copy(alpha = 0.2f)),
        radius = const(15.dp)
    )

    CircleLayer(
        id = "user-location-dot",
        source = userSource,
        color = const(Color.White),
        radius = const(7.dp),
        strokeColor = const(Color.Blue),
        strokeWidth = const(2.dp)
    )

}