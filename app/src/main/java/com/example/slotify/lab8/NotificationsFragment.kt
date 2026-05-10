package com.example.slotify.lab8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.example.slotify.shared.SharedDataManager
import com.example.slotify.R

class NotificationsFragment : Fragment() {

    private lateinit var makeupRecycler: RecyclerView
    private lateinit var eventsRecycler: RecyclerView
    private lateinit var btnMakeupClasses: MaterialButton
    private lateinit var btnEvents: MaterialButton
    private lateinit var makeupCountBadge: TextView
    private lateinit var eventsCountBadge: TextView
    private lateinit var totalCountBadge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        makeupRecycler = view.findViewById(R.id.makeupRecycler)
        eventsRecycler = view.findViewById(R.id.eventsRecycler)
        btnMakeupClasses = view.findViewById(R.id.btnMakeupClasses)
        btnEvents = view.findViewById(R.id.btnEvents)
        makeupCountBadge = view.findViewById(R.id.makeupCountBadge)
        eventsCountBadge = view.findViewById(R.id.eventsCountBadge)
        totalCountBadge = view.findViewById(R.id.totalCountBadge)

        // Set up RecyclerViews
        makeupRecycler.layoutManager = LinearLayoutManager(context)
        eventsRecycler.layoutManager = LinearLayoutManager(context)

        // Load data
        loadMakeupClasses()
        loadEvents()

        // Set default tab
        showMakeupClasses()

        // Set up click listeners
        btnMakeupClasses.setOnClickListener {
            showMakeupClasses()
        }

        btnEvents.setOnClickListener {
            showEvents()
        }

        // Check if a specific tab should be shown based on arguments
        arguments?.getString("tab")?.let { tab ->
            when (tab) {
                "makeup" -> showMakeupClasses()
                "events" -> showEvents()
            }
        }
    }

    private fun loadMakeupClasses() {
        val makeupClasses = mutableListOf<MakeupClass>()

        // Load real notifications from SharedDataManager
        context?.let { ctx ->
            val notifications = SharedDataManager.getAllNotifications(ctx)

            // Filter for makeup classes (notifications starting with "Makeup")
            notifications.forEach { notification ->
                if (notification.startsWith("Makeup", ignoreCase = true)) {
                    // Parse format: "Makeup: title|dateTime|venue;"
                    val parts = notification.substringAfter(":").trim().split("|")
                    if (parts.size >= 3) {
                        makeupClasses.add(
                            MakeupClass(
                                subject = parts[0],
                                time = parts[1],
                                venue = parts[2]
                            )
                        )
                    }
                }
            }

            // Add demo data if no real data exists
            if (makeupClasses.isEmpty()) {
                makeupClasses.addAll(listOf(
                    MakeupClass("Mathematics", "Saturday, 9:00 AM - 10:30 AM", "Room: A201 • Ms. Davis"),
                    MakeupClass("Physics", "Saturday, 11:00 AM - 12:30 PM", "Room: B105 • Mr. Johnson")
                ))
            }
        }

        makeupRecycler.adapter = MakeupClassesAdapter(makeupClasses)
        makeupCountBadge.text = "${makeupClasses.size}"
        updateTotalCount()
    }

    private fun loadEvents() {
        val events = mutableListOf<Event>()

        // Load real events from SharedDataManager
        context?.let { ctx ->
            val notifications = SharedDataManager.getAllNotifications(ctx)

            // Filter for events (notifications starting with "Event")
            notifications.forEach { notification ->
                if (notification.startsWith("Event", ignoreCase = true)) {
                    // Parse format: "Event: title|dateTime|venue;"
                    val parts = notification.substringAfter(":").trim().split("|")
                    if (parts.size >= 3) {
                        events.add(
                            Event(
                                name = parts[0],
                                date = "${parts[1]} • ${parts[2]}",
                                iconResId = R.drawable.ic_event
                            )
                        )
                    }
                }
            }

            // Add demo data if no real data exists
            if (events.isEmpty()) {
                events.addAll(listOf(
                    Event("Science Fair", "Tomorrow, 10:00 AM • Main Auditorium", R.drawable.ic_event),
                    Event("Sports Day", "Friday, 9:00 AM • Sports Ground", R.drawable.ic_event)
                ))
            }
        }

        eventsRecycler.adapter = EventsAdapter(events)
        eventsCountBadge.text = "${events.size}"
        updateTotalCount()
    }

    private fun updateTotalCount() {
        val makeupCount = makeupCountBadge.text.toString().toIntOrNull() ?: 0
        val eventsCount = eventsCountBadge.text.toString().toIntOrNull() ?: 0
        totalCountBadge.text = "${makeupCount + eventsCount}"
    }

    private fun showMakeupClasses() {
        makeupRecycler.visibility = View.VISIBLE
        eventsRecycler.visibility = View.GONE

        // Update button styles
        btnMakeupClasses.setBackgroundColor(resources.getColor(R.color.subject_blue_primary, null))
        btnMakeupClasses.setTextColor(resources.getColor(R.color.text_on_primary, null))

        btnEvents.setBackgroundColor(resources.getColor(R.color.border, null))
        btnEvents.setTextColor(resources.getColor(R.color.text_secondary, null))
    }

    private fun showEvents() {
        makeupRecycler.visibility = View.GONE
        eventsRecycler.visibility = View.VISIBLE

        // Update button styles
        btnEvents.setBackgroundColor(resources.getColor(R.color.subject_blue_primary, null))
        btnEvents.setTextColor(resources.getColor(R.color.text_on_primary, null))

        btnMakeupClasses.setBackgroundColor(resources.getColor(R.color.border, null))
        btnMakeupClasses.setTextColor(resources.getColor(R.color.text_secondary, null))
    }

    companion object {
        fun newInstance(tab: String? = null): NotificationsFragment {
            val fragment = NotificationsFragment()
            tab?.let {
                val args = Bundle()
                args.putString("tab", it)
                fragment.arguments = args
            }
            return fragment
        }
    }
}