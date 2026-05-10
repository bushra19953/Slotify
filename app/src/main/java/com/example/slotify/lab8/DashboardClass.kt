package com.example.slotify.lab8
import com.example.slotify.R
data class DashboardClass(
    val subjectName: String,
    val time: String,
    val room: String,
    val status: String, // "ongoing", "upcoming", "completed"
    val backgroundColor: String,
    val iconResource: Int = R.drawable.ic_timetable
)
