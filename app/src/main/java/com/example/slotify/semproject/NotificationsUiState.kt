package com.example.slotify.semproject

import com.example.slotify.semproject.model.NotificationItem

data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false
)