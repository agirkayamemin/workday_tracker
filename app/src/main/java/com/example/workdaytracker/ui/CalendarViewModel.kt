package com.example.workdaytracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.workdaytracker.data.CalendarDatabase
import com.example.workdaytracker.data.CalendarDayEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = CalendarDatabase.getDatabase(app).calendarDayDao()
    // Cache for all loaded months: key = Pair(year, month)
    private val _allMonthsData = MutableStateFlow<Map<Pair<Int, Int>, List<CalendarDayEntity>>>(emptyMap())
    val allMonthsData: StateFlow<Map<Pair<Int, Int>, List<CalendarDayEntity>>> = _allMonthsData

    fun loadMonth(year: Int, month: Int) {
        viewModelScope.launch {
            val monthList = dao.getDaysForMonth(year, month)
            _allMonthsData.value = _allMonthsData.value.toMutableMap().apply {
                put(year to month, monthList)
            }
        }
    }

    fun upsertDay(day: CalendarDayEntity) {
        viewModelScope.launch {
            dao.upsert(day)
            loadMonth(day.year, day.month)
        }
    }
} 