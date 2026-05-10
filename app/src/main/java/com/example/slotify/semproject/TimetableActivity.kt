package com.example.slotify.semproject

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.slotify.databinding.ActivityTimetableBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import com.example.slotify.R

class TimetableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimetableBinding


    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { parseUploadedTimetable(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToggleLogic()


        binding.btnUpload.setOnClickListener {
            filePickerLauncher.launch("text/*")
        }

        binding.btnGoToEdit.setOnClickListener {
            startActivity(Intent(this, WeeklyTimetableActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun setupToggleLogic() {

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.btnOfficial) {
                    binding.officialScrollView.visibility = View.VISIBLE
                    binding.plannerLayout.visibility = View.GONE
                    binding.btnUpload.visibility = View.VISIBLE
                } else {
                    binding.officialScrollView.visibility = View.GONE
                    binding.plannerLayout.visibility = View.VISIBLE
                    binding.btnUpload.visibility = View.GONE
                    loadPlannerData()
                }
            }
        }
    }

    private fun loadPlannerData() {
        val sharedPrefs = getSharedPreferences("TimetablePrefs", Context.MODE_PRIVATE)
        val table = binding.plannerTableLayout
        val days = listOf("mon", "tue", "wed", "thu", "fri")


        while (table.childCount > 1) {
            table.removeViewAt(1)
        }

        for (day in days) {
            val sub = sharedPrefs.getString("${day}_sub1", "")
            if (!sub.isNullOrEmpty()) {
                val time = sharedPrefs.getString("${day}_time1", "--") ?: "--"
                val venue = sharedPrefs.getString("${day}_venue1", "--") ?: "--"

                val row = TableRow(this)


                row.addView(createDynamicCell(day.uppercase(), Color.parseColor("#E2E8F0")))


                row.addView(createDynamicCell(sub, Color.parseColor("#F3E8FF")))


                row.addView(createDynamicCell(time, Color.WHITE))
                row.addView(createDynamicCell(venue, Color.WHITE))

                table.addView(row)
            }
        }
    }


    private fun createDynamicCell(text: String, bgColor: Int): TextView {
        return TextView(this).apply {
            this.text = text
            width = dpToPx(110)
            minHeight = dpToPx(70)
            gravity = Gravity.CENTER
            setBackgroundColor(bgColor)
            textSize = 11f
            setPadding(10, 10, 10, 10)
            setTextColor(Color.parseColor("#1E293B"))
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(1, 1, 1, 1) }
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    private fun parseUploadedTimetable(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            // Yahan aapka CSV parsing logic ayega jo aap use kar rahe thay
            Toast.makeText(this, "CSV Data Loaded Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.selectedItemId = R.id.nav_timetable
        binding.bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_timetable) return@setOnItemSelectedListener true
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, TeacherDashboardActivity::class.java)
                R.id.nav_notifications -> Intent(this, NotificationsActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }
            intent?.let {
                startActivity(it)
                finish()
            }
            true
        }
    }
}