package com.example.slotify.madness_project

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.example.slotify.R

class ClassActionActivity : AppCompatActivity() {

    private var currentMode = "SWAP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_action)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSubmit = findViewById<MaterialButton>(R.id.btnSubmitAction)
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        val swapFields = findViewById<LinearLayout>(R.id.swapFields)

        val etCurrentClass = findViewById<EditText>(R.id.etCurrentClass)
        val etTargetClass = findViewById<EditText>(R.id.etTargetClass)
        val etReason = findViewById<EditText>(R.id.etReason)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.btnActionSwap) {
                    currentMode = "SWAP"
                    swapFields.visibility = View.VISIBLE
                    btnSubmit.text = "Submit Swap Request"
                } else {
                    currentMode = "CANCEL"
                    swapFields.visibility = View.GONE
                    btnSubmit.text = "Submit Cancellation"
                }
            }
        }

        btnBack?.setOnClickListener { finish() }

        btnSubmit.setOnClickListener {
            // 1. Reset errors
            etCurrentClass.error = null
            etTargetClass.error = null
            etReason.error = null

            val className = etCurrentClass.text.toString().trim()
            val targetName = etTargetClass.text.toString().trim()
            val reason = etReason.text.toString().trim()

            // 2. Comprehensive Checks
            var isValid = true

            // Check Current Class
            if (className.isEmpty()) {
                etCurrentClass.error = "Current class name is required"
                isValid = false
            }

            // Check Reason (added a minimum length check for better logic)
            if (reason.isEmpty()) {
                etReason.error = "Reason is required"
                isValid = false
            } else if (reason.length < 10) {
                etReason.error = "Please provide a more detailed reason (min 10 chars)"
                isValid = false
            }

            // Check Target Class ONLY if in SWAP mode
            if (currentMode == "SWAP") {
                if (targetName.isEmpty()) {
                    etTargetClass.error = "Target class name is required"
                    isValid = false
                } else if (targetName.equals(className, ignoreCase = true)) {
                    etTargetClass.error = "Target class cannot be the same as current class"
                    isValid = false
                }
            }

            // 3. Final Action
            if (isValid) {
                val message = if (currentMode == "SWAP") "Swap request submitted!" else "Cancellation submitted!"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Please correct the errors above", Toast.LENGTH_SHORT).show()
            }
        }

        NavigationHelper.setupBottomNav(this)
    }
}