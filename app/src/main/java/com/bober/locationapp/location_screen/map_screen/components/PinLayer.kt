package com.bober.locationapp.location_screen.map_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

@Composable
fun PinLayer(pinPosition: Position) {

    val pinSource = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            FeatureCollection(
                features = listOf(
                    Feature(
                        geometry = Point(pinPosition),
                        properties = JsonObject(emptyMap())
                    )
                )
            )
        )
    )

    CircleLayer(
        id = "pin-layer",
        source = pinSource,
        color = const(Color.Blue),
        radius = const(5.dp),
        minZoom = 12f,
    )
}