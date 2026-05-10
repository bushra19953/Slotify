package com.example.slotify.madness_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

class CrDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // --- THEME LOGIC START ---
        // Load and apply the saved theme BEFORE super.onCreate
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedTheme = sharedPref.getInt("THEME_MODE", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)
        // --- THEME LOGIC END ---

        super.onCreate(savedInstanceState)
        setContentView(R.layout.cr_dashboard)

        // 1. NAVIGATION HELPER (Replaces all manual bottom nav code)
        NavigationHelper.setupBottomNav(this)

        // 2. Dashboard Card Navigation (Keep this logic)
        val cardClassroom = findViewById<MaterialCardView>(R.id.cardChangeClassroom)
        cardClassroom.setOnClickListener {
            val intent = Intent(this, ClassroomChangeActivity::class.java)
            startActivity(intent)
        }

        // 3. Pending Tasks Navigation
        val btnViewTasks = findViewById<Button>(R.id.btnViewTasks)
        btnViewTasks.setOnClickListener {
            val intent = Intent(this, PendingTasksActivity::class.java)
            startActivity(intent)
        }

        val btnViewEvents = findViewById<Button>(R.id.btnViewEvents)
        btnViewEvents.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }

        // 4. Change Timeslot Navigation
        val timingCard = findViewById<MaterialCardView>(R.id.cardTimingChange)
        timingCard.setOnClickListener {
            val intent = Intent(this, ChangeTimeslotActivity::class.java)
            startActivity(intent)
        }

        // 5. Class Swap/Cancel Navigation
        val cardClassAction = findViewById<MaterialCardView>(R.id.topBarClassAction)
        cardClassAction.setOnClickListener {
            val intent = Intent(this, ClassActionActivity::class.java)
            startActivity(intent)
        }

        // 6. Timeline Setup (Keep this functionality)
        val rvTimeline = findViewById<RecyclerView>(R.id.rvTimeline)
        val layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        rvTimeline.layoutManager = layoutManager
        rvTimeline.isNestedScrollingEnabled = false
        rvTimeline.setHasFixedSize(false)

        val timelineData = listOf(
            TimelineItem("Data Structures", "10:30 AM - 12:00 PM"),
            TimelineItem("Operating Systems", "12:30 PM - 02:00 PM"),
            TimelineItem("Database Systems", "03:00 PM - 04:30 PM")
        )

        rvTimeline.adapter = TimelineAdapter(timelineData)
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("USER_NAME", "CR") // "CR" is default

        val tvWelcome = findViewById<TextView>(R.id.welcomeText)
        tvWelcome.text = "Welcome, $savedName"
    }
}