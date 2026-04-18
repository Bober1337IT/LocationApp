package com.bober.locationapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bober.locationapp.data.local.dao.PinDao
import com.bober.locationapp.data.local.entity.PinEntity

@Database(
    entities = [PinEntity::class],
    version = 2,
    exportSchema = false
)
abstract class Database: RoomDatabase() {

    abstract fun pinDao(): PinDao

    companion object{
        const val DATABASE_NAME = "database"
    }
}