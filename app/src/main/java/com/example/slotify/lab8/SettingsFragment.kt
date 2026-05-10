package com.example.slotify.lab8

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.slotify.R
import com.example.slotify.semproject.SignUpActivity
import com.google.android.material.switchmaterial.SwitchMaterial
class SettingsFragment : Fragment() {

    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchClassUpdates: SwitchMaterial
    private lateinit var switchEventReminders: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize switches
        switchNotifications = view.findViewById(R.id.switchNotifications)
        switchClassUpdates = view.findViewById(R.id.switchClassUpdates)
        switchEventReminders = view.findViewById(R.id.switchEventReminders)

        // Set up click listeners
        view.findViewById<View>(R.id.layoutAccountInfo).setOnClickListener {
            // Show student details dialog
            showStudentDetailsDialog()
        }

        view.findViewById<View>(R.id.layoutDeleteAccount).setOnClickListener {
            // Show delete account confirmation dialog
            showDeleteAccountDialog()
        }

        view.findViewById<View>(R.id.layoutLogout).setOnClickListener {
            // Show logout confirmation dialog
            showLogoutDialog()
        }

        // Set up switch listeners
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Handle notifications toggle
            Toast.makeText(requireContext(), "Notifications ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchClassUpdates.setOnCheckedChangeListener { _, isChecked ->
            // Handle class updates toggle
            Toast.makeText(requireContext(), "Class Updates ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchEventReminders.setOnCheckedChangeListener { _, isChecked ->
            // Handle event reminders toggle
            Toast.makeText(requireContext(), "Event Reminders ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showStudentDetailsDialog() {
        val message = """
            Student ID: STU2024001
            Name: Alex Johnson
            Grade: 10th Grade
            Section: A
            Roll Number: 15

            Contact: +1 234-567-8900
            Email: alex.johnson@school.edu

            Parent/Guardian: Sarah Johnson
            Parent Contact: +1 234-567-8901

            Address: 123 Main Street
            City, State 12345
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Student Information")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                // Handle account deletion
                Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                // Navigate to login screen
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                // Handle logout
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                // Navigate to login screen
                val intent = Intent(requireContext(), SignUpActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}