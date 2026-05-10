package com.example.slotify.semproject

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.slotify.semproject.model.TimetableEntry
import kotlinx.coroutines.launch

data class WeeklyTimetableUiState(
    val timetable: List<TimetableEntry> = emptyList(),
    val isLoading: Boolean = true,
    val showSaveConfirmation: Boolean = false
)

class WeeklyTimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<WeeklyTimetableUiState>()
    val uiState: LiveData<WeeklyTimetableUiState> = _uiState

    private val daysKeys = listOf("mon", "tue", "wed", "thu", "fri")
    private val daysFullNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    fun loadTimetable() {
        viewModelScope.launch {
            _uiState.value = WeeklyTimetableUiState(isLoading = true)
            val prefs = getApplication<Application>().getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
            val timetable = daysKeys.mapIndexed { index, key ->
                TimetableEntry(
                    day = daysFullNames[index],
                    subject = prefs.getString("${key}_sub1", "") ?: "",
                    time = prefs.getString("${key}_time1", "") ?: "",
                    venue = prefs.getString("${key}_venue1", "") ?: "",
                    isHoliday = false
                )
            }
            _uiState.value = WeeklyTimetableUiState(timetable = timetable, isLoading = false)
        }
    }

    fun saveTimetable(timetable: List<TimetableEntry>) {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            timetable.forEachIndexed { index, entry ->
                val key = daysKeys[index]
                editor.putString("${key}_sub1", entry.subject)
                editor.putString("${key}_time1", entry.time)
                editor.putString("${key}_venue1", entry.venue)
            }
            // Auto-sync flag for Dashboard
            editor.putBoolean("is_timetable_set", true)
            editor.apply()
            _uiState.value = _uiState.value?.copy(showSaveConfirmation = true)
        }
    }

    fun onConfirmationShown() {
        _uiState.value = _uiState.value?.copy(showSaveConfirmation = false)
    }
}