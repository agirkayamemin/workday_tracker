package com.example.workdaytracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workdaytracker.data.CalendarDayEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

val turkishMonths = listOf(
    "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
    "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
)

val turkishDays = mapOf(
    DayOfWeek.MONDAY to "Pazartesi",
    DayOfWeek.TUESDAY to "Salı",
    DayOfWeek.WEDNESDAY to "Çarşamba",
    DayOfWeek.THURSDAY to "Perşembe",
    DayOfWeek.FRIDAY to "Cuma",
    DayOfWeek.SATURDAY to "Cumartesi",
    DayOfWeek.SUNDAY to "Pazar"
)

val dayOptions = listOf("Gidildi", "Gidilmedi", "Mesai", "G+M")

@Composable
fun CalendarView() {
    val context = LocalContext.current
    val viewModel: CalendarViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return CalendarViewModel(context.applicationContext as android.app.Application) as T
            }
        }
    )
    val currentYear = LocalDate.now().year
    val months = (1..12).map { month ->
        YearMonth.of(currentYear, month)
    }
    val allMonthsData by viewModel.allMonthsData.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(months) { yearMonth ->
            val monthData = allMonthsData[yearMonth.year to yearMonth.monthValue] ?: emptyList()
            MonthCardRoom(yearMonth, viewModel, monthData)
        }
    }
}

@Composable
fun MonthCardRoom(yearMonth: YearMonth, viewModel: CalendarViewModel, monthData: List<CalendarDayEntity>) {
    val daysInMonth = yearMonth.lengthOfMonth()
    LaunchedEffect(yearMonth.year, yearMonth.monthValue) {
        viewModel.loadMonth(yearMonth.year, yearMonth.monthValue)
    }
    var expanded by remember { mutableStateOf(false) }
    val dayMap = monthData.associateBy { it.day }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            MonthHeader(yearMonth, expanded, dayMap) { expanded = !expanded }
            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                MonthDaysRoom(yearMonth, dayMap) { day, option, gidildi, mesai ->
                    viewModel.upsertDay(
                        CalendarDayEntity(
                            id = "${yearMonth.year}-${yearMonth.monthValue}-$day",
                            year = yearMonth.year,
                            month = yearMonth.monthValue,
                            day = day,
                            option = option,
                            gidildiHour = gidildi,
                            mesaiHour = mesai
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MonthHeader(yearMonth: YearMonth, expanded: Boolean, dayMap: Map<Int, CalendarDayEntity>, onClick: () -> Unit) {
    val daysInMonth = yearMonth.lengthOfMonth()
    var totalGidildi = 0
    var totalMesai = 0
    for (day in 1..daysInMonth) {
        totalGidildi += dayMap[day]?.gidildiHour?.toIntOrNull() ?: 0
        totalMesai += dayMap[day]?.mesaiHour?.toIntOrNull() ?: 0
    }
    val summary = "($totalGidildi G) + ($totalMesai M)"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = turkishMonths[yearMonth.monthValue - 1],
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "  $summary",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MonthDaysRoom(
    yearMonth: YearMonth,
    dayMap: Map<Int, CalendarDayEntity>,
    onSave: (Int, String, String, String) -> Unit
) {
    var dropdownDay by remember { mutableStateOf<Int?>(null) }
    var showHourDialog by remember { mutableStateOf(false) }
    var hourInput by rememberSaveable { mutableStateOf("") }
    var pendingDay by remember { mutableStateOf<Int?>(null) }
    var pendingOption by remember { mutableStateOf<String?>(null) }
    var showMesaiDialog by remember { mutableStateOf(false) }
    var mesaiInput by rememberSaveable { mutableStateOf("") }
    val daysInMonth = yearMonth.lengthOfMonth()
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        for (day in 1..daysInMonth) {
            val entity = dayMap[day]
            val selectedOption = entity?.option
            val gidildiVal = entity?.gidildiHour ?: ""
            val mesaiVal = entity?.mesaiHour ?: ""
            val date = yearMonth.atDay(day)
            val dayName = turkishDays[date.dayOfWeek] ?: ""
            val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
            val backgroundColor = when (selectedOption) {
                "Gidildi", "Mesai", "G+M" -> Color(0xFFB9F6CA)
                "Gidilmedi" -> Color(0xFFFFCDD2)
                else -> Color.Transparent
            }
            val hourValue = when (selectedOption) {
                "Gidildi" -> gidildiVal
                "Mesai" -> mesaiVal
                "G+M" -> if (gidildiVal.isNotBlank() || mesaiVal.isNotBlank()) "(${gidildiVal.ifBlank { "0" }} + ${mesaiVal.ifBlank { "0" }})" else null
                else -> null
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor, shape = MaterialTheme.shapes.small)
                    .clickable { dropdownDay = day }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$day ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isWeekend) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isWeekend) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Box {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedOption ?: "Seçim Yap",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedOption != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        if (hourValue != null && hourValue.isNotBlank()) {
                            Text(
                                text = " $hourValue saat",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = dropdownDay == day,
                        onDismissRequest = { dropdownDay = null }
                    ) {
                        dayOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    when (option) {
                                        "Gidildi" -> {
                                            pendingDay = day
                                            pendingOption = option
                                            hourInput = gidildiVal
                                            showHourDialog = true
                                        }
                                        "Mesai" -> {
                                            pendingDay = day
                                            pendingOption = option
                                            hourInput = mesaiVal
                                            showHourDialog = true
                                        }
                                        "G+M" -> {
                                            pendingDay = day
                                            pendingOption = option
                                            hourInput = gidildiVal
                                            showHourDialog = true
                                        }
                                        else -> {
                                            onSave(day, option, gidildiVal, mesaiVal)
                                        }
                                    }
                                    dropdownDay = null
                                }
                            )
                        }
                    }
                }
            }
            if (day < daysInMonth) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            }
        }
    }
    // Dialog for entering hours
    if (showHourDialog && pendingDay != null && pendingOption != null) {
        AlertDialog(
            onDismissRequest = { showHourDialog = false },
            title = { Text(
                when (pendingOption) {
                    "Gidildi", "G+M" -> "Kaç saat çalıştın?"
                    "Mesai" -> "Kaç saat mesai yaptın?"
                    else -> "Kaç saat?"
                }
            ) },
            text = {
                OutlinedTextField(
                    value = hourInput,
                    onValueChange = { hourInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Saat") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (pendingDay != null && pendingOption != null) {
                            when (pendingOption) {
                                "Gidildi" -> {
                                    onSave(pendingDay!!, "Gidildi", hourInput, dayMap[pendingDay!!]?.mesaiHour ?: "")
                                }
                                "Mesai" -> {
                                    onSave(pendingDay!!, "Mesai", dayMap[pendingDay!!]?.gidildiHour ?: "", hourInput)
                                }
                                "G+M" -> {
                                    onSave(pendingDay!!, "G+M", hourInput, dayMap[pendingDay!!]?.mesaiHour ?: "")
                                    mesaiInput = dayMap[pendingDay!!]?.mesaiHour ?: ""
                                    showHourDialog = false
                                    showMesaiDialog = true
                                    return@TextButton
                                }
                            }
                        }
                        if (pendingOption != "G+M") showHourDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showHourDialog = false }) { Text("İptal") }
            }
        )
    }
    // Second dialog for 'G+M' mesai
    if (showMesaiDialog && pendingDay != null) {
        AlertDialog(
            onDismissRequest = { showMesaiDialog = false },
            title = { Text("Kaç saat mesai yaptın?") },
            text = {
                OutlinedTextField(
                    value = mesaiInput,
                    onValueChange = { mesaiInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Saat") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSave(pendingDay!!, "G+M", dayMap[pendingDay!!]?.gidildiHour ?: "", mesaiInput)
                        showMesaiDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showMesaiDialog = false }) { Text("İptal") }
            }
        )
    }
} 