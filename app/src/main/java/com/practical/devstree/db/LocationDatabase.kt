package com.practical.devstree.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practical.devstree.db.dao.LocationDao
import com.practical.devstree.db.entity.LocationInfo

@Database(entities = [LocationInfo::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
   /* companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database" // change name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }*/
}
