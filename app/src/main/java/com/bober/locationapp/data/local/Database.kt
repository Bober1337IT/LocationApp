package com.bober.locationapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    exportSchema = false
)
abstract class Database: RoomDatabase() {

    companion object{
        const val DATABASE_NAME = "database"
    }
}