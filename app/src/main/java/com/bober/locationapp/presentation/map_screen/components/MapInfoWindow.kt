package com.bober.locationapp.presentation.map_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.CameraState
import org.maplibre.spatialk.geojson.Position

/**
 * This implementation is based on the article: "A Practical Guide to MapLibre Compose".
 * Source: https://medium.com/@joy458963214/a-practical-guide-to-maplibre-compose-94a8cb6f79c4
 */

// Helper functions to convert between Pixels and DP
@Composable
@ReadOnlyComposable
internal fun Offset.toDpOffset(): DpOffset = with(LocalDensity.current) { DpOffset(x.toDp(), y.toDp()) }

@Composable
@ReadOnlyComposable
internal fun DpOffset.toOffset(): Offset = with(LocalDensity.current) { Offset(x.toPx(), y.toPx()) }

@Composable
fun MapInfoWindow(
    cameraState: CameraState,
    targetPosition: Position,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable (BoxScope.() -> Unit),
) {
    // 1. Project the geographic position to screen pixels
    val dpTarget = remember(targetPosition, cameraState.position) {
        cameraState.projection?.screenLocationFromPosition(targetPosition)
    }

    // If projection isn't ready, don't render anything
    val target = dpTarget?.toOffset() ?: return
    // 2. Position the content using absolute offset
    Box(modifier = modifier.fillMaxSize()) {
        target.let { offset ->
            val dpOffset = offset.toDpOffset()
            Column(
                modifier = modifier.absoluteOffset(dpOffset.x, dpOffset.y),
            ) {
                Box(modifier = Modifier.padding(contentPadding)) {
                    content()
                }
            }
        }
    }
}
