package com.bober.locationapp.data.local.repository

import com.bober.locationapp.data.location.LocationTracker
import com.bober.locationapp.domain.model.GeoCoordinate
import com.bober.locationapp.domain.repository.UserLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocationRepositoryImpl @Inject constructor(
    private val locationTracker: LocationTracker
) : UserLocationRepository {

    override fun observeLocationUpdates(): Flow<GeoCoordinate?> =
        locationTracker.getLocationUpdates().map { location ->
            location?.let { GeoCoordinate(it.latitude, it.longitude) }
        }

    override suspend fun resolveCityName(latitude: Double, longitude: Double): String? =
        locationTracker.getCurrentCity(latitude, longitude)
}