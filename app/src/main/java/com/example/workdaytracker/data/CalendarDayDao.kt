package com.example.workdaytracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalendarDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(day: CalendarDayEntity)

    @Query("SELECT * FROM calendar_day WHERE year = :year AND month = :month")
    suspend fun getDaysForMonth(year: Int, month: Int): List<CalendarDayEntity>

    @Query("SELECT * FROM calendar_day WHERE id = :id LIMIT 1")
    suspend fun getDayById(id: String): CalendarDayEntity?
} 