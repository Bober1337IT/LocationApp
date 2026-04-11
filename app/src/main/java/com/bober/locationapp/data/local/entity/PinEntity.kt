package com.bober.locationapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pins")
data class PinEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val description: String?,

    val latitude: Double,
    val longitude: Double,

    val createdAt: Long = System.currentTimeMillis(),

)

data class PinLocation(
    val id: Long,
    val latitude: Double,
    val longitude: Double
)