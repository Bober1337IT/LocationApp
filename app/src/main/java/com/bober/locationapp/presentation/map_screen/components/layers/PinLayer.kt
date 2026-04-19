package com.bober.locationapp.presentation.map_screen.components.layers

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
fun PinLayer(id: String, pinPosition: Position, color: Long, zoom: Double) {

    val calculatedRadius = when {
        zoom <= 7.0 -> 2.dp
        zoom >= 15.0 -> 8.dp
        else -> {
            val progress = (zoom - 7.0) / (15.0 - 7.0)
            val sizeRange = 8.dp - 2.dp
            2.dp + (sizeRange * progress.toFloat())
        }
    }

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
        id = id,
        source = pinSource,
        color = const(Color(color.toULong())),
        radius = const(calculatedRadius),
        minZoom = 5f,
    )
}