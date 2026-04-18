package com.bober.locationapp.di

import android.app.Application
import androidx.room.Room
import com.bober.locationapp.data.local.Database
import com.bober.locationapp.data.local.repository.PinRepositoryImpl
import com.bober.locationapp.data.local.repository.UserLocationRepositoryImpl
import com.bober.locationapp.domain.repository.PinRepository
import com.bober.locationapp.domain.repository.UserLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): Database {
        return Room.databaseBuilder(
            app,
            Database::class.java,
            Database.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePinRepository(db: Database): PinRepository {
        return PinRepositoryImpl(db.pinDao())
    }

    @Provides
    @Singleton
    fun provideUserLocationRepository(
        impl: UserLocationRepositoryImpl
    ): UserLocationRepository = impl
}