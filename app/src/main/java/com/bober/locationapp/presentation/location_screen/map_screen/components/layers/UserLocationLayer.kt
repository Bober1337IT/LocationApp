package com.bober.locationapp.presentation.location_screen.map_screen.components.layers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bober.locationapp.presentation.location_screen.map_screen.components.rememberDeviceRotation
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Polygon
import org.maplibre.spatialk.geojson.Position
import kotlin.math.cos
import kotlin.math.sin

/** On-screen size in dp; multiplied by [metersPerDpAtTarget] from the map for ground distance. */
private const val ARROW_LENGTH_DP = 20.0
private const val ARROW_HALF_BASE_DP = 9.0

/** Used only before the map reports [metersPerDpAtTarget] (0). */
private const val FALLBACK_METERS_PER_DP = 1.6

/** Meters along [bearingDegrees] (clockwise from north); returns [Position] lon/lat. */
private fun offsetMeters(user: Position, bearingDegrees: Double, distanceMeters: Double): Position {
    val θ = Math.toRadians(bearingDegrees)
    val latRad = Math.toRadians(user.latitude)
    val northMeters = distanceMeters * cos(θ)
    val eastMeters = distanceMeters * sin(θ)
    val dLat = northMeters / 111_320.0
    val dLon = eastMeters / (111_320.0 * cos(latRad).coerceAtLeast(1e-6))
    return Position(user.longitude + dLon, user.latitude + dLat)
}

/** Pointy triangle: tip ahead, base perpendicular at the user (closed ring for GeoJSON). */
private fun headingTriangle(
    user: Position,
    bearingDegrees: Float,
    lengthMeters: Double,
    halfBaseMeters: Double
): Polygon {
    val b = bearingDegrees.toDouble()
    val tip = offsetMeters(user, b, lengthMeters)
    val leftBase = offsetMeters(user, b - 90.0, halfBaseMeters)
    val rightBase = offsetMeters(user, b + 90.0, halfBaseMeters)
    val ring = listOf(tip, leftBase, rightBase, tip)
    return Polygon(listOf(ring))
}

@Composable
fun UserLocationLayer(
    userPosition: Position,
    metersPerDpAtTarget: Double
) {
    val headingDegrees = rememberDeviceRotation()
    val metersPerDp =
        if (metersPerDpAtTarget > 1e-9) metersPerDpAtTarget else FALLBACK_METERS_PER_DP
    val lengthMeters = ARROW_LENGTH_DP * metersPerDp
    val halfBaseMeters = ARROW_HALF_BASE_DP * metersPerDp

    val directionFeature = remember(
        userPosition.latitude,
        userPosition.longitude,
        headingDegrees,
        lengthMeters,
        halfBaseMeters
    ) {
        Feature(
            geometry = headingTriangle(userPosition, headingDegrees, lengthMeters, halfBaseMeters),
            properties = JsonObject(emptyMap())
        )
    }

    val directionSource = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            FeatureCollection(features = listOf(directionFeature))
        )
    )

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

    FillLayer(
        id = "user-location-heading-fill",
        source = directionSource,
        color = const(Color.Blue.copy(alpha = 0.85f)),
        outlineColor = const(Color.Blue)
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