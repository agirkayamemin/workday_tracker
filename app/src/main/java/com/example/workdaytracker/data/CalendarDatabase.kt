package com.example.workdaytracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CalendarDayEntity::class], version = 1)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarDayDao(): CalendarDayDao

    companion object {
        @Volatile
        private var INSTANCE: CalendarDatabase? = null

        fun getDatabase(context: Context): CalendarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalendarDatabase::class.java,
                    "calendar_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 