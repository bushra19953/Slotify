package com.example.slotify.semproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.slotify.databinding.ActivityTeacherDashboardBinding
import com.example.slotify.R

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private val viewModel: TeacherDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClicks()
        setupBottomNav()
        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            if (state.isLoading) return@observe

            val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val timePrefs = getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)

            val isFirstTime = userPrefs.getBoolean("is_first_time_user", true)
            if (isFirstTime) {
                binding.tvGreetingMain.text = "Welcome!"
                userPrefs.edit().putBoolean("is_first_time_user", false).apply()
            } else {
                binding.tvGreetingMain.text = "Welcome Back!"
            }

            binding.tvGreetingName.text = "Hi ${userPrefs.getString("name", "Teacher")}!"
            binding.tvTotalClasses.text = String.format("%02d", state.totalClassesCount)
            binding.tvTotalEvents.text = String.format("%02d", state.totalEventsCount)

            val isTimetableSet = timePrefs.getBoolean("is_timetable_set", false)
            if (isTimetableSet) {
                binding.cardYellowReminder.visibility = View.GONE
            } else {
                binding.cardYellowReminder.visibility = View.VISIBLE
            }

            if (state.isWeekend || state.todaysClass.isEmpty()) {
                binding.tvMonSub.text = "No Classes Today"
                binding.tvMonTime.text = "--"
            } else {
                val firstClass = state.todaysClass[0]
                binding.tvMonSub.text = firstClass.subject
                binding.tvMonTime.text = firstClass.time
            }
        }
    }

    private fun setupClicks() {
        binding.cardTodayClass.setOnClickListener {
            startActivity(Intent(this, WeeklyTimetableActivity::class.java))
        }

        binding.cardYellowReminder.setOnClickListener {
            startActivity(Intent(this, WeeklyTimetableActivity::class.java))
        }

        binding.ivLogo.setOnClickListener {
            showSignOutDialog()
        }

        binding.cardAddClass.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java).apply {
                putExtra("type", "makeup")
            })
        }

        binding.cardAddEvent.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(this, TimetableActivity::class.java))
        }
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to sign out? This will clear all local data.")
            .setPositiveButton("Logout") { _, _ ->
                performSignOut()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performSignOut() {
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("AppData", Context.MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(this, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_home
        binding.bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_home) return@setOnItemSelectedListener true
            val intent = when (item.itemId) {
                R.id.nav_timetable -> Intent(this, TimetableActivity::class.java)
                R.id.nav_notifications -> Intent(this, NotificationsActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }
            intent?.let {
                startActivity(it)
                overridePendingTransition(0, 0)
                finish()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardData()
    }
}