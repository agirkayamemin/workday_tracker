package com.example.workdaytracker

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object CalendarDataStore {
    private const val DATASTORE_NAME = "calendar_prefs"
    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

    // Key format: year_month_day_option (e.g., 2024_5_12_Gidildi)
    private fun keyFor(year: Int, month: Int, day: Int, option: String) = stringPreferencesKey("${'$'}year_${'$'}month_${'$'}day_${'$'}option")
    private fun optionKeyFor(year: Int, month: Int, day: Int) = stringPreferencesKey("${'$'}year_${'$'}month_${'$'}day_option")

    suspend fun setDayOption(context: Context, year: Int, month: Int, day: Int, option: String) {
        context.dataStore.edit { prefs ->
            prefs[optionKeyFor(year, month, day)] = option
        }
    }

    fun getDayOption(context: Context, year: Int, month: Int, day: Int): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[optionKeyFor(year, month, day)]
        }

    suspend fun setDayHour(context: Context, year: Int, month: Int, day: Int, option: String, hour: String) {
        context.dataStore.edit { prefs ->
            prefs[keyFor(year, month, day, option)] = hour
        }
    }

    fun getDayHour(context: Context, year: Int, month: Int, day: Int, option: String): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[keyFor(year, month, day, option)]
        }

    // For loading all data for a month (optional, for summary)
    fun getMonthData(context: Context, year: Int, month: Int): Flow<Map<Triple<Int, String, String>, String>> =
        context.dataStore.data.map { prefs ->
            prefs.asMap().entries
                .filter { it.key.name.startsWith("${year}_${month}_") }
                .mapNotNull { entry ->
                    val parts = entry.key.name.split('_')
                    if (parts.size == 5) {
                        val day = parts[2].toIntOrNull() ?: return@mapNotNull null
                        val option = parts[3]
                        val value = entry.value.toString()
                        Triple(day, option, value) to value
                    } else null
                }.toMap()
        }
} 