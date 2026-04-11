package com.bober.locationapp.domain.repository

import com.bober.locationapp.domain.model.GeoCoordinate
import kotlinx.coroutines.flow.Flow

interface UserLocationRepository {
    fun observeLocationUpdates(): Flow<GeoCoordinate?>
    suspend fun resolveCityName(latitude: Double, longitude: Double): String?
}
