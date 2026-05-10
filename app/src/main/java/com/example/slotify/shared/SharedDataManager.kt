package com.example.slotify.shared

import android.content.Context
import android.content.SharedPreferences

/**
 * Centralized data manager for sharing data across Student, Teacher, and CR roles.
 * This ensures notifications, events, and timetable updates are accessible to all users.
 *
 * Note: Currently uses SharedPreferences for local storage. For production use with
 * multiple devices, this should be replaced with a backend solution (Firebase/API).
 */
object SharedDataManager {

    private const val SHARED_PREFS_NAME = "SlotifySharedData"
    private const val USER_PREFS_NAME = "UserPrefs"
    private const val APP_DATA_NAME = "AppData"

    // Notification keys
    private const val KEY_NOTIFICATIONS = "notif_list"

    // Event keys
    private const val KEY_EVENTS = "event_list"

    // Timetable keys
    private const val KEY_TIMETABLE_SET = "is_timetable_set"

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getAppDataPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_DATA_NAME, Context.MODE_PRIVATE)
    }

    // ==================== NOTIFICATIONS ====================

    /**
     * Add a notification that will be visible to all roles.
     * Format: "type: title|dateTime|venue;"
     */
    fun addNotification(context: Context, type: String, title: String, dateTime: String, venue: String) {
        val prefs = getAppDataPrefs(context)
        val existingData = prefs.getString(KEY_NOTIFICATIONS, "") ?: ""
        val newEntry = "$type: $title|$dateTime|$venue;"
        val updatedData = existingData + newEntry

        prefs.edit().putString(KEY_NOTIFICATIONS, updatedData).apply()
    }

    /**
     * Get all notifications for display.
     * Returns list of notification strings in format "type: title|dateTime|venue"
     */
    fun getAllNotifications(context: Context): List<String> {
        val prefs = getAppDataPrefs(context)
        val data = prefs.getString(KEY_NOTIFICATIONS, "") ?: ""
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(";").filter { it.isNotEmpty() }
        }
    }

    /**
     * Clear all notifications
     */
    fun clearNotifications(context: Context) {
        getAppDataPrefs(context).edit().putString(KEY_NOTIFICATIONS, "").apply()
    }

    /**
     * Delete a specific notification by content
     */
    fun deleteNotification(context: Context, notificationContent: String) {
        val prefs = getAppDataPrefs(context)
        val data = prefs.getString(KEY_NOTIFICATIONS, "") ?: ""
        val updatedData = data.replace("$notificationContent;", "")
        prefs.edit().putString(KEY_NOTIFICATIONS, updatedData).apply()
    }

    // ==================== EVENTS ====================

    /**
     * Add an event that will be visible to all roles.
     */
    fun addEvent(context: Context, title: String, date: String) {
        val prefs = getSharedPrefs(context)
        val existingData = prefs.getString(KEY_EVENTS, "") ?: ""
        val newEntry = "$title|$date;"
        val updatedData = existingData + newEntry

        prefs.edit().putString(KEY_EVENTS, updatedData).apply()
    }

    /**
     * Get all events for display.
     */
    fun getAllEvents(context: Context): List<Pair<String, String>> {
        val prefs = getSharedPrefs(context)
        val data = prefs.getString(KEY_EVENTS, "") ?: ""
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(";")
                .filter { it.isNotEmpty() }
                .mapNotNull { entry ->
                    val parts = entry.split("|")
                    if (parts.size >= 2) {
                        Pair(parts[0], parts[1])
                    } else null
                }
        }
    }

    /**
     * Clear all events
     */
    fun clearEvents(context: Context) {
        getSharedPrefs(context).edit().putString(KEY_EVENTS, "").apply()
    }

    // ==================== TIMETABLE ====================

    /**
     * Check if timetable has been set by teacher
     */
    fun isTimetableSet(context: Context): Boolean {
        return context.getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
            .getBoolean(KEY_TIMETABLE_SET, false)
    }

    /**
     * Get timetable entry for a specific day and slot
     */
    fun getTimetableEntry(context: Context, day: String, slot: Int): Triple<String, String, String>? {
        val prefs = context.getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
        val subject = prefs.getString("${day}_sub$slot", null)
        val time = prefs.getString("${day}_time$slot", null)
        val venue = prefs.getString("${day}_venue$slot", null)

        return if (subject != null && time != null && venue != null) {
            Triple(subject, time, venue)
        } else null
    }

    // ==================== USER DATA ====================

    /**
     * Get current user role
     */
    fun getUserRole(context: Context): String {
        return context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE)
            .getString("role", "Student") ?: "Student"
    }

    /**
     * Get current user name
     */
    fun getUserName(context: Context): String {
        return context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE)
            .getString("name", "User") ?: "User"
    }

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)
    }
}
