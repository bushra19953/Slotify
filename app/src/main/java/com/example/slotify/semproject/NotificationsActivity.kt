package com.example.slotify.semproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slotify.databinding.ActivityNotificationsBinding
import com.example.slotify.semproject.model.NotificationItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.slotify.R

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Using ViewBinding to link with activity_notifications.xml
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setupBottomNav()
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter { item -> showOptionsDialog(item) }
        // Accessing the ID defined in your XML: @id/notificationRecyclerView
        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = notificationAdapter
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    notificationAdapter.submitList(state.notifications)
                }
            }
        }
    }

    private fun showOptionsDialog(item: NotificationItem) {
        val options = arrayOf("Mark as Done", "Edit Activity", "Delete", "Cancel")

        AlertDialog.Builder(this)
            .setTitle("Manage Activity")
            .setItems(options) { dialog, which ->
                when (which) {
                    0, 2 -> {
                        viewModel.deleteNotification(item)
                        Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
                    }
                    1 -> editItem(item)
                    3 -> dialog.dismiss()
                }
            }.show()
    }

    private fun editItem(item: NotificationItem) {
        val intent = Intent(this, AddActivity::class.java).apply {
            putExtra("is_edit", true)
            putExtra("old_title", item.title)
            putExtra("old_datetime", item.dateTime)
            putExtra("old_venue", item.venue)
        }
        startActivity(intent)
    }

    private fun setupBottomNav() {
        // Accessing the bottom_nav from your XML
        binding.bottomNav.selectedItemId = R.id.nav_notifications
        binding.bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_notifications) return@setOnItemSelectedListener true

            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, TeacherDashboardActivity::class.java)
                R.id.nav_timetable -> Intent(this, TimetableActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }

            intent?.let {
                startActivity(it)
                finish()
                overridePendingTransition(0, 0)
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadNotificationHistory()
    }
}