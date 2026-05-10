package com.example.slotify.madness_project

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.slotify.R

object NavigationHelper {

    fun setupBottomNav(activity: Activity) {
        val navPill = activity.findViewById<View>(R.id.bottomNavigation) ?: return

        val btnHome = navPill.findViewById<LinearLayout>(R.id.btnHome)
        val btnSettings = navPill.findViewById<LinearLayout>(R.id.btnSettings)
        val btnAlerts = navPill.findViewById<LinearLayout>(R.id.btnAlerts)
        val btnExit = navPill.findViewById<LinearLayout>(R.id.btnExit)

        // 1. Automatic Highlighting based on the Current Activity
        when (activity) {
            is CrDashboardActivity -> highlightButton(btnHome)
            is SettingsActivity -> highlightButton(btnSettings)
            is AlertsActivity -> highlightButton(btnAlerts)
            // Other activities from your screenshot can be highlighted as "Home" or "Alerts"
            // depending on which section they belong to
            is PendingTasksActivity, is ClassroomChangeActivity,
            is ChangeTimeslotActivity, is ClassActionActivity -> highlightButton(btnHome)
        }

        // 2. Navigation Logic
        btnHome.setOnClickListener {
            if (activity !is CrDashboardActivity) {
                navigateTo(activity, CrDashboardActivity::class.java)
            }
        }

        btnSettings.setOnClickListener {
            if (activity !is SettingsActivity) {
                navigateTo(activity, SettingsActivity::class.java)
            }
        }

        btnAlerts.setOnClickListener {
            if (activity !is AlertsActivity) {
                navigateTo(activity, AlertsActivity::class.java, isSingleTop = true)
            }
        }

        btnExit.setOnClickListener {
            // Logout Logic: Clear User Session but KEEP AppData (Events/Notifications)
            val prefs = activity.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            // Redirect to Onboarding (Login Flow)
            val intent = Intent(activity, com.example.slotify.semproject.OnboardingActivity::class.java)
            // Clear back stack so user can't go back to dashboard
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
            activity.finish()
        }
    }

    private fun highlightButton(layout: LinearLayout) {
        layout.setBackgroundResource(R.drawable.bg_nav_active)

        // Find the icon (first child) and text (second child)
        val icon = layout.getChildAt(0) as? ImageView
        val text = layout.getChildAt(1) as? TextView

        icon?.setColorFilter(Color.WHITE)
        text?.setTextColor(Color.WHITE)
    }

    private fun navigateTo(current: Activity, target: Class<*>, isSingleTop: Boolean = false) {
        val intent = Intent(current, target)
        if (isSingleTop || target == CrDashboardActivity::class.java) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        current.startActivity(intent)
    }
}