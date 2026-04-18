package com.bober.locationapp.domain.model

data class Pin(
    val id: Long = 0,
    val name: String,
    val city: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long
)

data class PinMapMarker(
    val id: Long,
    val latitude: Double,
    val longitude: Double
)