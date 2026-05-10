package com.example.slotify.madness_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.slotify.databinding.ActivityEventsBinding
import com.google.android.material.card.MaterialCardView
import com.example.slotify.R

data class EventItem(
    var name: String,
    var venue: String,
    var time: String,
    var isCompleted: Boolean = false
)

class EventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventsBinding
    private val eventsList = mutableListOf<EventItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.fabAddEvent.setOnClickListener {
            if (eventsList.size < 4) {
                showEventInputDialog(eventsList.size)
            } else {
                Toast.makeText(this, "Maximum 4 events allowed for Nicer UI!", Toast.LENGTH_SHORT).show()
            }
        }

        setupTiles()
        setupNotifyButtons()
        loadInitialData()

        NavigationHelper.setupBottomNav(this)
    }

    private fun setupTiles() {
        val tiles = arrayOf(binding.tile1, binding.tile2, binding.tile3, binding.tile4)
        tiles.forEachIndexed { index, tile ->
            tile.setOnClickListener {
                if (index < eventsList.size) {
                    Toast.makeText(this, "Long press to Manage", Toast.LENGTH_SHORT).show()
                } else if (index == eventsList.size) {
                    showEventInputDialog(index)
                }
            }
            tile.setOnLongClickListener {
                if (index < eventsList.size) {
                    showManagementDialog(index)
                    true
                } else false
            }
        }
    }

    private fun loadInitialData() {
        // Load from SharedDataManager
        eventsList.clear()
        val allNotifs = com.example.slotify.shared.SharedDataManager.getAllNotifications(this)
        
        // Filter for "Event" type
        allNotifs.forEach { notif ->
            if (notif.startsWith("Event:", ignoreCase = true)) {
                 val content = notif.substringAfter(":").trim()
                 val parts = content.split("|")
                 if (parts.size >= 3) {
                     // parts[0]=name, parts[1]=date/time, parts[2]=venue
                     // EventItem(name, venue, time)
                     eventsList.add(EventItem(parts[0], parts[2], parts[1]))
                 }
            }
        }
        
        // If empty, add some defaults but DONT save them to shared prefs automatically to avoid spam, 
        // OR save them if you want them to be initial seed. 
        // The original code added them to list. Let's just show them if list is empty, 
        // but maybe not save them to avoid cluttering other users unless user interacts?
        // For consistency with original code, we just show them.
        if (eventsList.isEmpty()) {
             // Optional: seeding
        }
        
        // Trim to max 6 for UI limitations mentioned in original code?
        // Original code: if (eventsList.size < 4) ... 
        
        updateUI()
    }

    private fun saveEventToShared(name: String, venue: String, time: String) {
        com.example.slotify.shared.SharedDataManager.addNotification(this, "Event", name, time, venue)
    }

    private fun deleteEventFromShared(item: EventItem) {
        // Construct the string to match
        // "Event: name|time|venue;" (approximate, need to match logic in SharedDataManager)
        // SharedDataManager stores: "$type: $title|$dateTime|$venue;"
        // So: "Event: ${item.name}|${item.time}|${item.venue}"
        val target = "Event: ${item.name}|${item.time}|${item.venue}"
        com.example.slotify.shared.SharedDataManager.deleteNotification(this, target)
    }
    
    // Helper to refresh data
    private fun reloadData() {
        loadInitialData()
    }

    // ==========================================
    // Modifying existing methods to use new helpers
    // ==========================================

    private fun showEventInputDialog(index: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val etN = view.findViewById<EditText>(R.id.etEventName)
        val etV = view.findViewById<EditText>(R.id.etVenue)
        val etT = view.findViewById<EditText>(R.id.etTime)

        var isEdit = false
        var oldItem: EventItem? = null

        if (index < eventsList.size) {
            isEdit = true
            oldItem = eventsList[index]
            etN.setText(oldItem.name)
            etV.setText(oldItem.venue)
            etT.setText(oldItem.time)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEdit) "Edit Event" else "Add New Event")
            .setView(view)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = etN.text.toString().trim()
            val venue = etV.text.toString().trim()
            val time = etT.text.toString().trim()

            // Basic validation (relaxed for demo)
            if (name.isNotEmpty() && venue.isNotEmpty() && time.isNotEmpty()) {
                if (isEdit && oldItem != null) {
                    deleteEventFromShared(oldItem)
                }
                saveEventToShared(name, venue, time)
                
                reloadData() // Reloads from shared prefs
                dialog.dismiss()
            } else {
                 Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showManagementDialog(index: Int) {
        val options = arrayOf("Edit", "Delete", "Mark as Completed")
        AlertDialog.Builder(this)
            .setTitle("Manage Event")
            .setItems(options) { _, which ->
                if (index < eventsList.size) {
                     val item = eventsList[index]
                     when (which) {
                        0 -> showEventInputDialog(index)
                        1 -> { 
                            deleteEventFromShared(item)
                            reloadData()
                        }
                        2 -> {
                            deleteEventFromShared(item)
                            reloadData()
                            Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.show()
    }

    private fun updateUI() {
        val tiles = arrayOf(binding.tile1, binding.tile2, binding.tile3, binding.tile4)
        val names = arrayOf(binding.tvName1, binding.tvName2, binding.tvName3, binding.tvName4)
        val venues = arrayOf(binding.tvVenue1, binding.tvVenue2, binding.tvVenue3, binding.tvVenue4)
        val times = arrayOf(binding.tvTime1, binding.tvTime2, binding.tvTime3, binding.tvTime4)

        // 1. Update the first 4 static tiles
        for (i in tiles.indices) {
            if (i < eventsList.size) {
                tiles[i].visibility = View.VISIBLE
                names[i].text = eventsList[i].name
                venues[i].text = eventsList[i].venue
                times[i].text = eventsList[i].time
            } else {
                tiles[i].visibility = View.GONE
            }
        }

        // 2. Clear dynamic slots (5th and 6th) to redraw correctly
        val container = binding.containerLayout
        val staticChildCount = 4
        if (container.childCount > staticChildCount) {
            container.removeViews(staticChildCount, container.childCount - staticChildCount)
        }

        // 3. Add 5th and 6th slot if data exists
        if (eventsList.size > 4) {
            for (i in 4 until eventsList.size) {
                if (i < 6) { // Ensure logic only goes up to 6 slots total
                    addDynamicSlot(eventsList[i], i)
                }
            }
        }
    }

    private fun addDynamicSlot(event: EventItem, index: Int) {
        // Simple Logic: Inflate activity layout just to steal the tile1 design
        val inflater = LayoutInflater.from(this)
        val layoutCopy = inflater.inflate(R.layout.activity_events, null)
        val dynamicTile = layoutCopy.findViewById<MaterialCardView>(R.id.tile1)

        (dynamicTile.parent as? ViewGroup)?.removeView(dynamicTile)

        // Set data using existing sub-IDs
        dynamicTile.findViewById<TextView>(R.id.tvName1).text = event.name
        dynamicTile.findViewById<TextView>(R.id.tvVenue1).text = event.venue
        dynamicTile.findViewById<TextView>(R.id.tvTime1).text = event.time

        // Basic Positioning Logic:
        val params = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
            binding.tile1.width,
            binding.tile1.height
        )

        // Constraints: Put 5th under Tile 3, Put 6th under Tile 4
        if (index == 4) { // Slot 5
            params.topToBottom = binding.tile3.id
            params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            params.endToStart = binding.tile2.id // Horizontal packed style simulation
            params.horizontalChainStyle = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.CHAIN_PACKED
        } else if (index == 5) { // Slot 6
            params.topToBottom = binding.tile4.id
            params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            params.startToEnd = binding.tile1.id // Anchor to simulate position
        }

        // Add padding/margins
        val margin = (8 * resources.displayMetrics.density).toInt()
        params.setMargins(margin, margin, margin, margin)
        dynamicTile.layoutParams = params

        // Functionality: Notify button and management
        dynamicTile.setOnClickListener { Toast.makeText(this, "Long press to Manage", Toast.LENGTH_SHORT).show() }
        dynamicTile.setOnLongClickListener { showManagementDialog(index); true }
        dynamicTile.findViewById<Button>(R.id.btnNotify1).setOnClickListener {
            Toast.makeText(this, "Notified: ${event.name}", Toast.LENGTH_SHORT).show()
        }

        binding.containerLayout.addView(dynamicTile)
    }

    private fun setupNotifyButtons() {
        val notifyButtons = arrayOf(binding.btnNotify1, binding.btnNotify2, binding.btnNotify3, binding.btnNotify4)
        notifyButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (index < eventsList.size) {
                    Toast.makeText(this, "Notified: ${eventsList[index].name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}