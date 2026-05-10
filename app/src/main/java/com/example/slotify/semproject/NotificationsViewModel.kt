package com.example.slotify.semproject

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.slotify.semproject.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotificationHistory()
    }

    fun loadNotificationHistory() {
        viewModelScope.launch {
            // Use SharedDataManager to get all notifications
            // Note: SharedDataManager needs Context, but we are in ViewModel.
            // Using getApplication<Application>() as context.
            val rawList = com.example.slotify.shared.SharedDataManager.getAllNotifications(getApplication())

            val historyList = rawList.mapNotNull { entry ->
                 // Format: "type: title|dateTime|venue"
                 // We need to extract title, dateTime, venue.
                 // The current adapter/model might expect specific things.
                 // entry e.g. "Event: Title|Date|Venue"
                 val content = entry.substringAfter(":").trim()
                 val parts = content.split("|")
                 if (parts.size >= 3) {
                     NotificationItem(title = parts[0], dateTime = parts[1], venue = parts[2])
                 } else null
            }
            _uiState.value = NotificationsUiState(notifications = historyList, isLoading = false)
        }
    }

    fun deleteNotification(item: NotificationItem) {
        // SharedDataManager requires the exact string to delete.
        // We need to reconstruct it or find it.
        // Ideally, NotificationItem should hold the full original string or ID.
        // Since it doesn't, we'll try to reconstruct the suffix and find the matching entry.
        
        viewModelScope.launch {
            val context = getApplication<Application>()
            val all = com.example.slotify.shared.SharedDataManager.getAllNotifications(context)
            
            // Find the one that contains our title|date|venue
            // This is a bit "fuzzy" but matches the current structure.
            val targetSuffix = "${item.title}|${item.dateTime}|${item.venue}"
            val match = all.find { it.contains(targetSuffix) }
            
            if (match != null) {
                com.example.slotify.shared.SharedDataManager.deleteNotification(context, match)
                loadNotificationHistory() // Reload to update UI
            }
        }
    }

    private fun updateNotifications(modification: (MutableList<NotificationItem>) -> Unit) {
        // Deprecated/Unused helper for manual list manipulation.
        // Keeping it empty or removing logical implementation as we rely on SharedDataManager now.
    }
}