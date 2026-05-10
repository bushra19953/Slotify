package com.example.slotify.madness_project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slotify.R

data class RoomData(
    val number: String,
    val desc: String,
    val tags: String,
    val isConflict: Boolean = false
)

class ClassroomChangeActivity : AppCompatActivity() {

    private lateinit var rvRooms: RecyclerView
    private lateinit var listTitle: TextView
    private lateinit var emptyState: LinearLayout
    private var fullList = mutableListOf<RoomData>() // Stores all data for filtering
    private var displayedList = mutableListOf<RoomData>() // List actually shown on screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom_change)

        rvRooms = findViewById(R.id.rvRooms)
        listTitle = findViewById(R.id.listTitle)
        emptyState = findViewById(R.id.emptyState)
        val etSearch = findViewById<EditText>(R.id.etSearchRoom)

        rvRooms.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        NavigationHelper.setupBottomNav(this)

        // SEARCH LOGIC: Real apps filter results live
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRooms(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Initial Icon Listeners
        findViewById<ImageView>(R.id.btnViewRooms).setOnClickListener {
            loadNewCategory("Available Now", mutableListOf(
                RoomData("6403", "Vacant for 2 hours", "AC • Projector"),
                RoomData("5102", "Available now", "High-speed WiFi"),
                RoomData("1105", "Vacant till 4 PM", "Projector Only")
            ))
        }

        findViewById<ImageView>(R.id.btnViewEquip).setOnClickListener {
            loadNewCategory("Equipment Status", mutableListOf(
                RoomData("1201", "Checking Speakers...", "Working Projector"),
                RoomData("6405", "All Functional", "AC • Mic • Speakers")
            ))
        }

        findViewById<ImageView>(R.id.btnViewConflicts).setOnClickListener {
            loadNewCategory("Schedule Conflicts", mutableListOf(
                RoomData("4301", "MAD vs OS Conflict", "Urgent Action Required", true)
            ))
        }
    }

    private fun loadNewCategory(title: String, data: MutableList<RoomData>) {
        fullList = data
        displayedList = data.toMutableList()
        listTitle.text = title
        updateUI()
    }

    private fun filterRooms(query: String) {
        displayedList = if (query.isEmpty()) {
            fullList.toMutableList()
        } else {
            fullList.filter { it.number.contains(query) }.toMutableList()
        }
        updateUI()
    }

    private fun updateUI() {
        if (displayedList.isEmpty()) {
            rvRooms.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            rvRooms.visibility = View.VISIBLE
            emptyState.visibility = View.GONE

            if (rvRooms.adapter == null) {
                rvRooms.adapter = RoomAdapter(displayedList) { room, position ->
                    handleBooking(room, position)
                }
            } else {
                (rvRooms.adapter as RoomAdapter).updateData(displayedList)
            }
        }
    }
    private fun handleBooking(room: RoomData, position: Int) {
        val actionMessage = if (room.isConflict) "Conflict Resolved" else "Room ${room.number} Booked"
        Toast.makeText(this, "$actionMessage!", Toast.LENGTH_SHORT).show()

        // Remove from both lists
        fullList.removeAll { it.number == room.number }
        displayedList.removeAt(position)

        rvRooms.adapter?.notifyItemRemoved(position)
        if (displayedList.isEmpty()) updateUI()
    }
}