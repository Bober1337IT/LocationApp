package com.bober.locationapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bober.locationapp.data.local.entity.PinEntity
import com.bober.locationapp.data.local.entity.PinLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface PinDao {

    @Query("SELECT id, color, latitude, longitude FROM pins")
    fun getAllPinLocations(): Flow<List<PinLocation>>

    @Query("SELECT * FROM pins WHERE id = :pinId")
    suspend fun getPinDetails(pinId: Long): PinEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPin(pin: PinEntity): Long

    @Delete
    suspend fun deletePin(pin: PinEntity)

    @Query("DELETE FROM pins WHERE id = :pinId")
    suspend fun deletePinById(pinId: Long)
}