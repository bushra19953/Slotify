package com.example.slotify.madness_project

data class AlertItem(
    val title: String,
    val description: String,
    val time: String,
    val type: String // "Urgent", "Info", "Reminder"
)