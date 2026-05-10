package com.example.slotify.madness_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.slotify.databinding.PendingTasksBinding
import com.example.slotify.R


class PendingTasksActivity : AppCompatActivity() {

    private lateinit var binding: PendingTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PendingTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationHelper.setupBottomNav(this)

        setupTaskLogic(binding.cardTask1, binding.tvSub1, binding.tvTask1)
        setupTaskLogic(binding.cardTask2, binding.tvSub2, binding.tvTask2)
        setupTaskLogic(binding.cardTask3, binding.tvSub3, binding.tvTask3)
        setupTaskLogic(binding.cardTask4, binding.tvSub4, binding.tvTask4)
        setupTaskLogic(binding.cardTask5, binding.tvSub5, binding.tvTask5)

        binding.ivBack.setOnClickListener { finish() }

        binding.fabAddTask.setOnClickListener {
            if (binding.cardTask5.visibility == View.VISIBLE) {
                Toast.makeText(this, "Task limit reached for this demo!", Toast.LENGTH_SHORT).show()
            } else {
                showAddNewTaskDialog()
            }
        }
    }

    private fun showAddNewTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etSub = dialogView.findViewById<EditText>(R.id.etSubject)
        val etName = dialogView.findViewById<EditText>(R.id.etTaskName)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add", null) // Set to null first to override later
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // LOGIC: Override the button click to keep dialog open if fields are empty
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val subject = etSub.text.toString().trim()
            val taskName = etName.text.toString().trim()

            if (subject.isNotEmpty() && taskName.isNotEmpty()) {
                binding.tvSub5.text = subject
                binding.tvTask5.text = taskName
                binding.cardTask5.visibility = View.VISIBLE
                Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Only close when data is correct
            } else {
                if (subject.isEmpty()) etSub.error = "Required"
                if (taskName.isEmpty()) etName.error = "Required"
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTaskLogic(card: CardView, subTextView: TextView, taskTextView: TextView) {
        card.setOnLongClickListener {
            val options = arrayOf("Delete Task", "Edit Task", "Mark as Complete")

            AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(options) { _, which ->
                    when (which) {
                        0, 2 -> {
                            card.visibility = View.GONE
                            Toast.makeText(this, "Task Done!", Toast.LENGTH_SHORT).show()
                        }
                        1 -> openEditDialog(subTextView, taskTextView)
                    }
                }.show()
            true
        }
    }

    private fun openEditDialog(subTextView: TextView, taskTextView: TextView) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etSub = dialogView.findViewById<EditText>(R.id.etSubject)
        val etName = dialogView.findViewById<EditText>(R.id.etTaskName)

        etSub.setText(subTextView.text)
        etName.setText(taskTextView.text)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update", null) // Set to null first
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // LOGIC: Override the update button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val subject = etSub.text.toString().trim()
            val taskName = etName.text.toString().trim()

            if (subject.isNotEmpty() && taskName.isNotEmpty()) {
                subTextView.text = subject
                taskTextView.text = taskName
                dialog.dismiss() // Close only if valid
            } else {
                if (subject.isEmpty()) etSub.error = "Required"
                if (taskName.isEmpty()) etName.error = "Required"
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}