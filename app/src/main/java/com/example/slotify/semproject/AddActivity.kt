package com.example.slotify.semproject

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.slotify.databinding.ActivityAddInfoBinding
import java.util.*
import com.example.slotify.R
import com.example.slotify.shared.SharedDataManager

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddInfoBinding
    private var isEditMode = false
    private var oldEntryString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra("is_edit", false)
        val type = intent.getStringExtra("type") ?: "Event"

        if (isEditMode) {
            val title = intent.getStringExtra("old_title") ?: ""
            val dateTime = intent.getStringExtra("old_datetime") ?: ""
            val venue = intent.getStringExtra("old_venue") ?: ""

            binding.etTitle.setText(title)
            binding.etDateTime.setText(dateTime)
            binding.etVenue.setText(venue)
            binding.btnSave.text = "Update $type"

            oldEntryString = "$title|$dateTime|$venue;"
        }

        binding.etDateTime.setOnClickListener { showDateTimePicker() }
        binding.btnSave.setOnClickListener { saveEvent(type) }
        binding.btnCancel.setOnClickListener { finish() }

        setupBottomNav()
    }

    private fun saveEvent(type: String) {
        val title = binding.etTitle.text.toString().trim()
        val dateTime = binding.etDateTime.text.toString().trim()
        val venue = binding.etVenue.text.toString().trim()

        if (title.isEmpty() || dateTime.isEmpty() || venue.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Use SharedDataManager to add notification
        if (isEditMode) {
            // Note: SharedDataManager doesn't support edit easily without full replace.
            // For now, we will delete the old one and add the new one.
            // Assuming oldEntryString matches what SharedDataManager expects (full formatted string)
             SharedDataManager.deleteNotification(this, oldEntryString.removeSuffix(";"))
        }
        
        SharedDataManager.addNotification(this, type, title, dateTime, venue)

        // Notify Students logic
        sendNotificationToStudents("New $type Scheduled", "$title at $dateTime ($venue)")

        Toast.makeText(this, "Success! Students Notified.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun sendNotificationToStudents(title: String, message: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "student_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "IST Student Updates", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun showDateTimePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            TimePickerDialog(this, { _, h, min ->
                val formattedDate = String.format("%02d/%02d/%d %02d:%02d", d, m + 1, y, h, min)
                binding.etDateTime.setText(formattedDate)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_home
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
}