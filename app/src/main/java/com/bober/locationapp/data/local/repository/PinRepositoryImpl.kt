package com.bober.locationapp.data.local.repository

import com.bober.locationapp.data.local.dao.PinDao
import com.bober.locationapp.data.local.mapper.toPin
import com.bober.locationapp.data.local.mapper.toPinEntity
import com.bober.locationapp.domain.model.Pin
import com.bober.locationapp.domain.model.PinMapMarker
import com.bober.locationapp.domain.repository.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PinRepositoryImpl(
    private val dao: PinDao
): PinRepository {
    override fun getAllPinLocations(): Flow<List<PinMapMarker>> {
        return dao.getAllPinLocations().map { entities ->
            entities.map { it.toPin() }
        }
    }

    override suspend fun getPinDetailsById(id: Long): Pin? {
        return dao.getPinDetails(id)?.toPin()
    }

    override suspend fun insertPin(pin: Pin) {
        dao.insertPin(pin.toPinEntity())
    }

    override suspend fun deletePin(pin: Pin) {
        dao.deletePin(pin.toPinEntity())
    }

    override suspend fun deletePinById(id: Long) {
        dao.deletePinById(id)
    }
}