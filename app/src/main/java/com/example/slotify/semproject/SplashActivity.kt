package com.example.slotify.semproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.slotify.lab8.MainActivity as StudentMainActivity
import com.example.slotify.madness_project.CrDashboardActivity
import com.example.slotify.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // 3 seconds delay for splash animation
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 3000)
    }

    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Route to appropriate dashboard based on user role
            val role = sharedPref.getString("role", "Student") ?: "Student"
            val intent = when (role) {
                "Teacher" -> Intent(this, TeacherDashboardActivity::class.java)
                "CR" -> Intent(this, CrDashboardActivity::class.java)
                else -> Intent(this, StudentMainActivity::class.java)
            }
            startActivity(intent)
        } else {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
        finish()
    }
}