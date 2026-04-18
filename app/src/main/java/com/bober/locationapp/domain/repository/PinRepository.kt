package com.bober.locationapp.domain.repository

import com.bober.locationapp.domain.model.Pin
import com.bober.locationapp.domain.model.PinMapMarker
import kotlinx.coroutines.flow.Flow

interface PinRepository {

    fun getAllPinLocations(): Flow<List<PinMapMarker>>

    suspend fun getPinDetailsById(id: Long): Pin?

    suspend fun insertPin(pin: Pin): Long

    suspend fun deletePin(pin: Pin)

    suspend fun deletePinById(id: Long)
}