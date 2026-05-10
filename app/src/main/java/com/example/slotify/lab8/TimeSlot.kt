package com.example.slotify.lab8

data class TimeSlot(
    val startTime: String,
    val endTime: String,
    val subject: String,
    val room: String,
    val teacher: String,
    val color: String,
    val isBreak: Boolean = false
)
