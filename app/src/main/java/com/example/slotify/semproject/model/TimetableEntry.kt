package com.example.slotify.semproject.model

data class TimetableEntry(
    val day: String = "",
    val subject: String = "",
    val time: String = "",
    val venue: String = "",
    val isHoliday: Boolean = false
)
