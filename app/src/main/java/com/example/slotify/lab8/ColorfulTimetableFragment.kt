package com.example.slotify.lab8

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import java.io.BufferedReader
import java.io.InputStreamReader
import com.example.slotify.R

class ColorfulTimetableFragment : Fragment() {

    private lateinit var timetableContainer: LinearLayout
    private lateinit var btnUploadCSV: MaterialButton

    private var uploadedTimetable: MutableMap<String, MutableList<TimeSlot>> = mutableMapOf()

    // CSV file picker launcher
    private val csvPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                parseCSVFile(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timetable_colorful, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        timetableContainer = view.findViewById(R.id.timetableContainer)
        btnUploadCSV = view.findViewById(R.id.btnUploadCSV)

        // CSV Upload button
        btnUploadCSV.setOnClickListener {
            openCSVPicker()
        }

        // Build the timetable grid
        buildTimetableGrid()
    }

    private fun buildTimetableGrid() {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
        val timeSlots = listOf("8:00", "9:00", "10:00", "11:00", "12:00")

        val cellWidth = dpToPx(110)
        val cellHeight = dpToPx(90)
        val timeColumnWidth = dpToPx(50)

        // Create header row with days
        val headerRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Empty cell for time column
        headerRow.addView(createEmptyCell(timeColumnWidth, dpToPx(40)))

        // Add day headers
        days.forEach { day ->
            headerRow.addView(createDayHeader(day, cellWidth))
        }
        timetableContainer.addView(headerRow)

        // Get timetable data
        val timetableData = getTimetableData()

        // Create rows for each time slot
        timeSlots.forEach { time ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Add time label
            row.addView(createTimeLabel(time, timeColumnWidth, cellHeight))

            // Add cells for each day
            days.forEach { day ->
                val classInfo = timetableData[time]?.get(day)
                row.addView(createClassCell(classInfo, cellWidth, cellHeight))
            }

            timetableContainer.addView(row)
        }
    }

    private fun createDayHeader(day: String, width: Int): TextView {
        return TextView(requireContext()).apply {
            text = day
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#1E40AF"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(width, dpToPx(40))
            setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8))
        }
    }

    private fun createTimeLabel(time: String, width: Int, height: Int): TextView {
        return TextView(requireContext()).apply {
            text = time
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#1E40AF"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(width, height)
            setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8))
        }
    }

    private fun createEmptyCell(width: Int, height: Int): View {
        return View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(width, height)
        }
    }

    private fun createClassCell(classInfo: ClassInfo?, width: Int, height: Int): View {
        return if (classInfo != null) {
            CardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(width - dpToPx(4), height - dpToPx(4)).apply {
                    setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
                }
                radius = dpToPx(8).toFloat()
                cardElevation = dpToPx(2).toFloat()
                setCardBackgroundColor(Color.parseColor(classInfo.color))

                val textContainer = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                }

                val subjectText = TextView(requireContext()).apply {
                    text = classInfo.subject
                    textSize = 11f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(Color.parseColor("#1F2937"))
                    gravity = Gravity.CENTER
                }

                val venueText = TextView(requireContext()).apply {
                    text = classInfo.venue
                    textSize = 9f
                    setTextColor(Color.parseColor("#374151"))
                    gravity = Gravity.CENTER
                    setPadding(0, dpToPx(2), 0, 0)
                }

                textContainer.addView(subjectText)
                textContainer.addView(venueText)
                addView(textContainer)
            }
        } else {
            View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(width - dpToPx(4), height - dpToPx(4)).apply {
                    setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun getTimetableData(): Map<String, Map<String, ClassInfo>> {
        return mapOf(
            "8:00" to mapOf(
                "Mon" to ClassInfo("Web Tech", "Room\n3141", "#1E88E5"), // Blue
                "Wed" to ClassInfo("Data Structures", "Laboratory 1", "#43A047"), // Green
                "Fri" to ClassInfo("Comp Networks", "Room\n801", "#E53935") // Red
            ),
            "9:00" to mapOf(
                "Mon" to ClassInfo("Mobile App Dev", "Room\n981", "#7CB342"), // Light Green
                "Tue" to ClassInfo("Software Eng", "Room\n1992", "#FB8C00"), // Orange
                "Wed" to ClassInfo("DBMS", "Room\n400", "#00ACC1"), // Cyan
                "Thu" to ClassInfo("Data Structures", "Laboratory 1", "#43A047"),
                "Fri" to ClassInfo("Web Tech", "Room\n3141", "#1E88E5")
            ),
            "10:00" to mapOf(
                "Mon" to ClassInfo("Comp Networks", "Room\n801", "#E53935"),
                "Tue" to ClassInfo("OS", "Room\n501", "#8E24AA"), // Purple
                "Thu" to ClassInfo("OS", "Room\n501", "#8E24AA"),
                "Fri" to ClassInfo("Mobile App Dev", "Room\n981", "#7CB342")
            ),
            "11:00" to mapOf(
                "Mon" to ClassInfo("Comp Networks", "Room\n801", "#E53935"),
                "Tue" to ClassInfo("DBMS", "Room\n400", "#00ACC1"),
                "Wed" to ClassInfo("Software Eng", "Room\n1992", "#FB8C00")
            ),
            "12:00" to mapOf()
        )
    }

    private fun openCSVPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "text/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        csvPicker.launch(intent)
    }

    private fun parseCSVFile(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()
            reader.close()

            if (lines.isEmpty()) {
                Toast.makeText(context, "CSV file is empty", Toast.LENGTH_SHORT).show()
                return
            }

            Toast.makeText(context, "CSV parsing not yet implemented for grid view", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Error reading CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

data class ClassInfo(
    val subject: String,
    val venue: String,
    val color: String
)
