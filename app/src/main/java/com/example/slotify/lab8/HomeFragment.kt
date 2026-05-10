package com.example.slotify.lab8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import com.example.slotify.R

class HomeFragment : Fragment() {

    private lateinit var classesRecyclerView: RecyclerView
    private lateinit var totalClassesCount: TextView
    private lateinit var completedClassesCount: TextView
    private lateinit var upcomingClassesCount: TextView
    private lateinit var attendanceText: TextView
    private lateinit var viewAllClasses: TextView
    private lateinit var settingsIcon: ImageView
    private lateinit var greetingText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        classesRecyclerView = view.findViewById(R.id.classes_recycler)
        totalClassesCount = view.findViewById(R.id.total_classes_count)
        completedClassesCount = view.findViewById(R.id.completed_classes_count)
        upcomingClassesCount = view.findViewById(R.id.upcoming_classes_count)
        attendanceText = view.findViewById(R.id.attendance_text)
        viewAllClasses = view.findViewById(R.id.view_all_classes)
        settingsIcon = view.findViewById(R.id.settings_icon)
        greetingText = view.findViewById(R.id.greeting_text)

        // Set greeting based on time
        setGreeting()

        // Set up RecyclerView with linear layout
        classesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Get sample data
        val classesList = getTodayClassesData()
        val stats = calculateStats(classesList)

        // Update statistics
        updateStatistics(stats)

        // Set adapter with sample data
        classesRecyclerView.adapter = DashboardClassesAdapter(classesList) { classItem ->
            // Handle class item click
            showClassDetails(classItem)
        }

        // View all classes click
        viewAllClasses.setOnClickListener {
            (activity as MainActivity).replaceFragment(ColorfulTimetableFragment())
        }

        // Settings icon click
        settingsIcon.setOnClickListener {
            (activity as MainActivity).replaceFragment(SettingsFragment())
        }
    }

    private fun setGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }

        greetingText.text = greeting
    }

    private fun calculateStats(classes: List<DashboardClass>): DashboardStats {
        val total = classes.size
        val completed = classes.count { it.status == "completed" }
        val upcoming = classes.count { it.status == "upcoming" || it.status == "ongoing" }

        return DashboardStats(
            totalClasses = total,
            completedClasses = completed,
            upcomingClasses = upcoming,
            attendance = "92%"
        )
    }

    private fun updateStatistics(stats: DashboardStats) {
        totalClassesCount.text = stats.totalClasses.toString()
        completedClassesCount.text = stats.completedClasses.toString()
        upcomingClassesCount.text = stats.upcomingClasses.toString()
        attendanceText.text = stats.attendance
    }

    // Sample data for today's classes
    private fun getTodayClassesData(): List<DashboardClass> {
        return listOf(
            DashboardClass(
                "Mobile App Development",
                "9:00 AM",
                "Room B101",
                "completed",
                "#001B48",
                R.drawable.ic_timetable
            ),
            DashboardClass(
                "Data Structures",
                "10:30 AM",
                "Room A205",
                "completed",
                "#02457A",
                R.drawable.ic_timetable
            ),
            DashboardClass(
                "Database Systems",
                "12:00 PM",
                "Lab 302",
                "ongoing",
                "#018ABE",
                R.drawable.ic_timetable
            ),
            DashboardClass(
                "Web Development",
                "2:00 PM",
                "Room C103",
                "upcoming",
                "#97CADB",
                R.drawable.ic_timetable
            ),
            DashboardClass(
                "Software Engineering",
                "3:30 PM",
                "Room B204",
                "upcoming",
                "#001B48",
                R.drawable.ic_timetable
            ),
            DashboardClass(
                "Computer Networks",
                "5:00 PM",
                "Lab 401",
                "upcoming",
                "#02457A",
                R.drawable.ic_timetable
            )
        )
    }

    private fun showClassDetails(classItem: DashboardClass) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle(classItem.subjectName)
        builder.setMessage(
            "Time: ${classItem.time}\n" +
            "Room: ${classItem.room}\n" +
            "Status: ${classItem.status.uppercase()}"
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}