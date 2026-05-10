package com.example.slotify.lab8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.slotify.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // Set up navigation item selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_timetable -> {
                    replaceFragment(ColorfulTimetableFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_notifications -> {
                    replaceFragment(NotificationsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}