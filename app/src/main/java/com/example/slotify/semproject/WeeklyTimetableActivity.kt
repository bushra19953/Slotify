package com.example.slotify.semproject

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slotify.databinding.ActivityWeeklyTimetableBinding
import java.util.*
import com.example.slotify.R

class WeeklyTimetableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeeklyTimetableBinding
    private var selectedDayIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUIFields()
        setupDaySelection()
        setupTimePickers()

        binding.btnSave.setOnClickListener {
            saveCurrentDayToLocal()
        }

        binding.btnCancel.setOnClickListener { finish() }
        setupBottomNav()
    }

    private fun setupDaySelection() {
        binding.dayChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedDayIndex = when (checkedIds.firstOrNull()) {
                R.id.chipMon -> 0
                R.id.chipTue -> 1
                R.id.chipWed -> 2
                R.id.chipThu -> 3
                R.id.chipFri -> 4
                else -> 0
            }
            updateUIFields()
        }
    }

    private fun updateUIFields() {
        val dayKey = getDayKey(selectedDayIndex)
        val prefs = getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
        binding.etSubject1.setText(prefs.getString("${dayKey}_sub1", ""))
        binding.etTime1.setText(prefs.getString("${dayKey}_time1", ""))
        binding.etVenue1.setText(prefs.getString("${dayKey}_venue1", ""))
    }

    private fun saveCurrentDayToLocal() {
        val dayKey = getDayKey(selectedDayIndex)
        val prefs = getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("${dayKey}_sub1", binding.etSubject1.text.toString().trim())
        editor.putString("${dayKey}_time1", binding.etTime1.text.toString().trim())
        editor.putString("${dayKey}_venue1", binding.etVenue1.text.toString().trim())

        editor.putBoolean("is_timetable_set", true)
        editor.apply()

        Toast.makeText(this, "${getDayName(selectedDayIndex)} Updated & Synced!", Toast.LENGTH_SHORT).show()
    }

    private fun showTimePicker(callback: (String) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, h, m ->
            val amPm = if (h < 12) "AM" else "PM"
            val hour = if (h % 12 == 0) 12 else h % 12
            callback(String.format("%02d:%02d %s", hour, m, amPm))
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    private fun setupTimePickers() {
        binding.etTime1.setOnClickListener {
            showTimePicker { time -> binding.etTime1.setText(time) }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_timetable
        binding.bottomNav.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, TeacherDashboardActivity::class.java)
                R.id.nav_timetable -> Intent(this, TimetableActivity::class.java)
                R.id.nav_notifications -> Intent(this, NotificationsActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it); finish(); overridePendingTransition(0, 0) }
            true
        }
    }

    private fun getDayKey(index: Int) = listOf("mon", "tue", "wed", "thu", "fri")[index]
    private fun getDayName(index: Int) = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")[index]
}