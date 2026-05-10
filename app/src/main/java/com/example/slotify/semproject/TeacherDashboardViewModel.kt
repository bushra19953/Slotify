package com.example.slotify.semproject

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.slotify.semproject.model.TimetableEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TeacherDashboardUiState(
    val greeting: String = "Good Morning",
    val todaysClass: List<TimetableEntry> = emptyList(),
    val totalClassesCount: Int = 0,
    val totalEventsCount: Int = 0,
    val isWeekend: Boolean = false,
    val isLoading: Boolean = true
)

class TeacherDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<TeacherDashboardUiState>()
    val uiState: LiveData<TeacherDashboardUiState> = _uiState

    fun loadDashboardData() {
        viewModelScope.launch {
            val context = getApplication<Application>()

            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)


            val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
            val currentDay = sdf.format(Date()).lowercase()
            val isWeekend = currentDay == "sat" || currentDay == "sun"

            val timePrefs = context.getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
            val todayClasses = mutableListOf<TimetableEntry>()

            for (i in 1..3) {
                val sub = timePrefs.getString("${currentDay}_sub$i", null)
                if (!sub.isNullOrBlank()) {
                    todayClasses.add(TimetableEntry(
                        day = currentDay,
                        subject = sub,
                        time = timePrefs.getString("${currentDay}_time$i", "--") ?: "--",
                        venue = timePrefs.getString("${currentDay}_venue$i", "--") ?: "--"
                    ))
                }
            }

            val appPrefs = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
            val eventList = appPrefs.getString("notif_list", "")?.split(";")?.filter { it.isNotBlank() } ?: emptyList()

            _uiState.value = TeacherDashboardUiState(
                todaysClass = todayClasses,
                totalClassesCount = todayClasses.size,
                totalEventsCount = eventList.size,
                isWeekend = isWeekend,
                isLoading = false
            )
        }
    }

    fun markEventDone(eventData: String) {
        val context = getApplication<Application>()
        val appPrefs = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val currentList = appPrefs.getString("notif_list", "") ?: ""
        val newList = currentList.replace("$eventData;", "")
        appPrefs.edit().putString("notif_list", newList).apply()
        loadDashboardData()
    }
}