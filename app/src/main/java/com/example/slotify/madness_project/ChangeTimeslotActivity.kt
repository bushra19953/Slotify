package com.example.slotify.madness_project

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.example.slotify.R

class ChangeTimeslotActivity : AppCompatActivity() {
    private var selectedSlotCard: MaterialCardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_timeslot)

        // 1. Initialize Header Views
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
        }

        // 2. Initialize Form Views
        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirm)
        val etSubject = findViewById<EditText>(R.id.etSubjectName)
        val etProfessor = findViewById<EditText>(R.id.etProfessorName)

        // 3. Initialize Slots
        val slots = listOfNotNull(
            findViewById<MaterialCardView>(R.id.slot1),
            findViewById<MaterialCardView>(R.id.slot2),
            findViewById<MaterialCardView>(R.id.slot3),
            findViewById<MaterialCardView>(R.id.slot4),
            findViewById<MaterialCardView>(R.id.slot5),
            findViewById<MaterialCardView>(R.id.slot6)
        )

        slots.forEach { slot ->
            slot.setOnClickListener { handleSlotSelection(slot) }
        }

        // 4. Confirm Button Logic
        btnConfirm.setOnClickListener {
            val subject = etSubject.text.toString().trim()
            val professor = etProfessor.text.toString().trim()

            when {
                subject.isEmpty() || professor.isEmpty() -> {
                    Toast.makeText(this, "Please enter Subject and Professor names", Toast.LENGTH_SHORT).show()
                }
                selectedSlotCard == null -> {
                    Toast.makeText(this, "Please select a timeslot", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Schedule updated for $subject", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        // 5. ONE LINE NAVIGATION LOGIC
        // This handles Home, Settings, Alerts, and Exit automatically
        NavigationHelper.setupBottomNav(this)
    }

    private fun handleSlotSelection(clickedCard: MaterialCardView) {
        // Reset previous selection
        selectedSlotCard?.let {
            it.setCardBackgroundColor(Color.WHITE)
            it.strokeColor = Color.parseColor("#E5E7EB")
            findTextViewInside(it)?.setTextColor(Color.parseColor("#1F2937"))
        }

        // Highlight new selection
        clickedCard.setCardBackgroundColor(Color.parseColor("#2F5D8C"))
        clickedCard.strokeColor = Color.parseColor("#2F5D8C")
        findTextViewInside(clickedCard)?.setTextColor(Color.WHITE)

        selectedSlotCard = clickedCard
    }

    private fun findTextViewInside(view: View): TextView? {
        if (view is TextView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val result = findTextViewInside(view.getChildAt(i))
                if (result != null) return result
            }
        }
        return null
    }
}