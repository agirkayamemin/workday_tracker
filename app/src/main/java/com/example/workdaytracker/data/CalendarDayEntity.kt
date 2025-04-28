package com.example.workdaytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_day")
data class CalendarDayEntity(
    val year: Int,
    val month: Int,
    val day: Int,
    val option: String?,
    val gidildiHour: String?,
    val mesaiHour: String?,
    @PrimaryKey(autoGenerate = false)
    val id: String = "${'$'}year-${'$'}month-${'$'}day"
) 