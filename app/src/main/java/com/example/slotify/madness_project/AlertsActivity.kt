package com.example.slotify.madness_project

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.slotify.R

class AlertsActivity : AppCompatActivity() {

    private lateinit var adapter: AlertsAdapter
    private val alertList = mutableListOf(
        AlertItem("Exam Postponed", "The Mid-term for DBMS is shifted to next Monday.", "Just now", "Urgent"),
        AlertItem("Room Change", "Data Structures class will be held in Room 302.", "1h ago", "Info"),
        AlertItem("Lab Submission", "Don't forget to submit your OS Lab reports by 5 PM.", "3h ago", "Reminder")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rvAlerts = findViewById<RecyclerView>(R.id.rvAlerts)
        rvAlerts.layoutManager = LinearLayoutManager(this)

        // Pass the mutable list to the adapter
        adapter = AlertsAdapter(alertList)
        rvAlerts.adapter = adapter

        findViewById<Button>(R.id.btnNotifyStudents).setOnClickListener {
            showAddEventDialog()
        }

        findViewById<FloatingActionButton>(R.id.fabAddAlert).setOnClickListener {
            showAddEventDialog()
        }

        NavigationHelper.setupBottomNav(this)
        
        loadAlertsFromShared()
    }
    
    private fun loadAlertsFromShared() {
        alertList.clear()
        val all = com.example.slotify.shared.SharedDataManager.getAllNotifications(this)
        
        // We want to show "Events" here, or maybe just things that look like Alerts.
        // For now, let's show ALL "Event" items, and try to parse mapped types.
        // Or if we want to be specific, we could have used a different type key.
        // Given the goal "cr and teacher set a makeup or any upcoming event... notification is directly visible",
        // showing all events here is good.
        
        all.forEach { entry ->
            if (entry.startsWith("Event:", ignoreCase = true)) {
                // Format: Event: Title|Date|Venue
                val content = entry.substringAfter(":").trim()
                val parts = content.split("|")
                if (parts.size >= 3) {
                    val fullTitle = parts[0]
                    val date = parts[1]
                    val venue = parts[2] // We used venue as description in some cases, or venue.
                    
                    // Try to extract type from title if we put it there like "[Urgent] Title"
                    var type = "Info"
                    var title = fullTitle
                    
                    if (fullTitle.startsWith("[Urgent]")) {
                        type = "Urgent"
                        title = fullTitle.removePrefix("[Urgent]").trim()
                    } else if (fullTitle.startsWith("[Reminder]")) {
                        type = "Reminder"
                        title = fullTitle.removePrefix("[Reminder]").trim()
                    } else if (fullTitle.startsWith("[Info]")) {
                        type = "Info"
                        title = fullTitle.removePrefix("[Info]").trim()
                    }
                    
                    alertList.add(AlertItem(title, venue, date, type))
                }
            }
        }
        
        // Show demo data if empty?
        if (alertList.isEmpty()) {
             // alertList.add(AlertItem("Welcome", "No alerts yet.", "Now", "Info"))
        }
        
        adapter.notifyDataSetChanged()
    }

    // --- NEW: REMOVE FUNCTION (Called by Adapter) ---
    fun removeItem(position: Int) {
        if (position >= 0 && position < alertList.size) {
            val item = alertList[position]
            alertList.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, alertList.size)
            
            // Delete from SharedDataManager
            // Reconstruct full title with prefix
            val prefix = if (item.type != "Info") "[${item.type}] " else "" // Note: Logic below saves [Info] too? 
            // In save function below, I will standardize.
            
            // Since we can't be 100% sure of the exact string without ID, we search.
            // Target: "Event: [${item.type}] ${item.title}|${item.time}|${item.description}" 
            // OR "Event: ${item.title}|${item.time}|${item.description}"
            
            val targetSuffix = "${item.time}|${item.description}" // Matches date|venue
            val context = this
            val all = com.example.slotify.shared.SharedDataManager.getAllNotifications(context)
            val match = all.find { it.contains(targetSuffix) && it.contains(item.title) }
            
            if (match != null) {
                com.example.slotify.shared.SharedDataManager.deleteNotification(context, match)
            }
            
            Toast.makeText(this, "Event cleared", Toast.LENGTH_SHORT).show()
        }
    }

    // --- NEW: EDIT FUNCTION (Called by Adapter) ---
    fun showEditDialog(position: Int) {
        showEventDialog(position)
    }

    private fun showAddEventDialog() {
        showEventDialog(null)
    }

    // --- REUSABLE DIALOG FOR BOTH ADD & EDIT ---
    private fun showEventDialog(position: Int?) {
        val isEditing = position != null
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (isEditing) "Edit Notice" else "Add New Notice")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 40, 60, 10)

        val inputTitle = EditText(this)
        inputTitle.hint = "Event Title"

        val inputNote = EditText(this)
        inputNote.hint = "Note Details"

        val labelSpinner = Spinner(this)
        val labels = arrayOf("Urgent", "Info", "Reminder")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        labelSpinner.adapter = spinnerAdapter

        // Pre-fill data if we are editing
        if (isEditing) {
            val item = alertList[position!!]
            inputTitle.setText(item.title)
            inputNote.setText(item.description) // or item.desc depending on your AlertItem property name
            val spinnerPosition = labels.indexOf(item.type)
            if (spinnerPosition != -1) labelSpinner.setSelection(spinnerPosition)
        }

        layout.addView(inputTitle)
        layout.addView(inputNote)
        layout.addView(labelSpinner)
        builder.setView(layout)

        builder.setPositiveButton(if (isEditing) "Update" else "Add") { _, _ ->
            val title = inputTitle.text.toString().trim()
            val note = inputNote.text.toString().trim()
            val label = labelSpinner.selectedItem.toString()

            if (title.isNotEmpty() && note.isNotEmpty()) {
                val fullTitle = "[$label] $title"
                val time = "Just now" // Or current time formatting
                
                if (isEditing) {
                    val oldItem = alertList[position!!]
                    // Remove old from shared
                    // Logic similar to removeItem but inline
                     val context = this
                     val all = com.example.slotify.shared.SharedDataManager.getAllNotifications(context)
                     val targetSuffix = "${oldItem.time}|${oldItem.description}"
                     val match = all.find { it.contains(targetSuffix) && it.contains(oldItem.title) }
                     if (match != null) {
                         com.example.slotify.shared.SharedDataManager.deleteNotification(context, match)
                     }
                    
                    // Add new
                    com.example.slotify.shared.SharedDataManager.addNotification(this, "Event", fullTitle, oldItem.time, note) // Keep old time? Or update?
                    
                    // Update local list
                    alertList[position!!] = AlertItem(title, note, oldItem.time, label)
                    adapter.notifyItemChanged(position)
                    Toast.makeText(this, "Notice Updated", Toast.LENGTH_SHORT).show()
                } else {
                    // Add new item
                    // Use SharedDataManager
                    // Date Format: yyyy-MM-dd HH:mm or just "Just now" for demo?
                    // Teachers use: "2025..."
                    // Alerts used "Just now". 
                    // Let's use a simple date string
                    val dateStr = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                    
                    com.example.slotify.shared.SharedDataManager.addNotification(this, "Event", fullTitle, dateStr, note)
                    
                    // Reload or add locally
                    loadAlertsFromShared() // Safest to reload
                    
                    findViewById<RecyclerView>(R.id.rvAlerts).scrollToPosition(0)
                    Toast.makeText(this, "Notice Added", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}