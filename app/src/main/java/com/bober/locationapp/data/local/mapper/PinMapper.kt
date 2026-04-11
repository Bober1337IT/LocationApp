package com.bober.locationapp.data.local.mapper

import com.bober.locationapp.data.local.entity.PinEntity
import com.bober.locationapp.data.local.entity.PinLocation
import com.bober.locationapp.domain.model.Pin
import com.bober.locationapp.domain.model.PinMapMarker

fun PinEntity.toPin(): Pin {
    return Pin(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt
    )
}

fun PinLocation.toPin(): PinMapMarker {
    return PinMapMarker(
        id = id,
        latitude = latitude,
        longitude = longitude
    )
}

fun Pin.toPinEntity(): PinEntity {
    return PinEntity(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt
    )
}