package com.example.slotify.madness_project

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.slotify.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 1. Navigation & Header
        NavigationHelper.setupBottomNav(this)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // 2. Setting Item Buttons
        val btnAccount = findViewById<LinearLayout>(R.id.btnAccount)
        val btnAppearance = findViewById<LinearLayout>(R.id.btnAppearance)
        val btnNotifications = findViewById<LinearLayout>(R.id.btnNotifications)

        // ACCOUNT LOGIC: Save name for Dashboard
        btnAccount.setOnClickListener {
            showNameChangeDialog()
        }

        // APPEARANCE LOGIC: Switch Light/Dark Mode app-wide
        btnAppearance.setOnClickListener {
            showThemeSelectionDialog()
        }

        // NOTIFICATIONS LOGIC: Functional toggle list
        btnNotifications.setOnClickListener {
            showNotificationDialog()
        }
    }

    private fun showNameChangeDialog() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentName = sharedPref.getString("USER_NAME", "CR")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Dashboard Name")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(currentName) // Pre-fill with existing name
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                // Save to SharedPreferences
                sharedPref.edit().putString("USER_NAME", newName).apply()
                Toast.makeText(this, "Name updated to $newName", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Light Mode", "Dark Mode", "System Default")
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        AlertDialog.Builder(this)
            .setTitle("Appearance")
            .setItems(themes) { _, which ->
                val mode = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }

                // Apply immediately
                AppCompatDelegate.setDefaultNightMode(mode)

                // Save choice for next app launch
                sharedPref.edit().putInt("THEME_MODE", mode).apply()
            }
            .show()
    }

    private fun showNotificationDialog() {
        val items = arrayOf("Class Cancellations", "Room Changes", "Exam Reminders")
        val checkedItems = booleanArrayOf(true, true, false) // Default states

        AlertDialog.Builder(this)
            .setTitle("Filter Notifications")
            .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Done", null)
            .show()
    }
}