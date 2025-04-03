package com.practical.devstree.di

import android.content.Context
import androidx.room.Room
import com.practical.devstree.db.LocationDatabase
import com.practical.devstree.db.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLocationDatabase(context: Context): LocationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocationDatabase::class.java,
            "location-database"
        ).build()
    }
    @Singleton
    @Provides
    fun provideLocationDao(database: LocationDatabase): LocationDao {
        return database.locationDao()
    }

}
