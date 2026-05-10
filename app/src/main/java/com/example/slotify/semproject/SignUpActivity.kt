package com.example.slotify.semproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.example.slotify.R

class SignUpActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val roleGroup = findViewById<MaterialButtonToggleGroup>(R.id.signUpRoleGroup)
        val btnSignUp = findViewById<Button>(R.id.signUpButton)
        val etName = findViewById<EditText>(R.id.nameInput)
        val etEmail = findViewById<EditText>(R.id.signUpEmailInput)
        val etPassword = findViewById<EditText>(R.id.signUpPasswordInput)
        val togglePassword = findViewById<ImageView>(R.id.togglePassword)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)


        togglePassword.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePassword.setImageResource(R.drawable.ic_eye_closed)
            } else {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePassword.setImageResource(R.drawable.ic_eye_open)
            }
            isPasswordVisible = !isPasswordVisible
            etPassword.setSelection(etPassword.text.length)
        }

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val selectedRoleId = roleGroup.checkedButtonId
            val selectedRole = when (selectedRoleId) {
                R.id.btnTeacherSignup -> "Teacher"
                R.id.btnCRSignup -> "CR"
                else -> "Student"
            }


            if (name.isEmpty()) {
                etName.error = "Name is required"
                return@setOnClickListener
            }


            val studentRegex = Regex("^[0-9]{9}@ist\\.edu\\.pk$")

            val teacherRegex = Regex("^[A-Za-z0-9._%+-]+@ist\\.edu\\.pk$")

            if (selectedRole == "Student" || selectedRole == "CR") {
                if (!studentRegex.matches(email)) {
                    etEmail.error = "Format: 123456789@ist.edu.pk"
                    return@setOnClickListener
                }
            } else if (selectedRole == "Teacher") {
                if (!teacherRegex.matches(email)) {
                    etEmail.error = "Teacher email must end with @ist.edu.pk"
                    return@setOnClickListener
                }
            }

            if (password.length < 6) {
                etPassword.error = "Minimum 6 characters required"
                return@setOnClickListener
            }

            // --- SAVING DATA LOCALLY ---
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().apply {
                putString("name", name)
                putString("role", selectedRole)
                putString("registered_email", email)
                putString("registered_password", password)
                putBoolean("isLoggedIn", false)
                apply()
            }

            Toast.makeText(this, "Account Created! Please Login", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}