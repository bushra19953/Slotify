package com.example.slotify.semproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.example.slotify.lab8.MainActivity as StudentMainActivity
import com.example.slotify.madness_project.CrDashboardActivity
import com.example.slotify.R
import com.example.slotify.semproject.SignUpActivity

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.emailInput)
        val etPassword = findViewById<EditText>(R.id.passwordInput)
        val roleToggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.roleToggleGroup)
        val togglePassword = findViewById<ImageView>(R.id.togglePasswordLogin)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val btnSignUpLink = findViewById<TextView>(R.id.btnSignUp)
        val btnForgotPass = findViewById<TextView>(R.id.btnForgotPassword)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)


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


        btnForgotPass.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Password Recovery")

            val input = EditText(this)
            input.hint = "Enter your registered IST email"
            input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            builder.setView(input)

            builder.setPositiveButton("Find") { _, _ ->
                val enteredEmail = input.text.toString().trim()
                val savedEmail = sharedPref.getString("registered_email", "")
                val savedPass = sharedPref.getString("registered_password", "")

                if (enteredEmail == savedEmail && enteredEmail.isNotEmpty()) {

                    AlertDialog.Builder(this)
                        .setTitle("Account Found")
                        .setMessage("Your password is: $savedPass")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    Toast.makeText(this, "Email not found on this device!", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }


        loginButton.setOnClickListener {
            val inputEmail = etEmail.text.toString().trim()
            val inputPass = etPassword.text.toString().trim()

            val selectedRoleId = roleToggleGroup.checkedButtonId
            val selectedRole = when (selectedRoleId) {
                R.id.btnTeacher -> "Teacher"
                R.id.btnCR -> "CR"
                else -> "Student"
            }

            val savedEmail = sharedPref.getString("registered_email", "")
            val savedPass = sharedPref.getString("registered_password", "")
            val savedRole = sharedPref.getString("role", "")

            if (inputEmail.isEmpty() || inputPass.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (inputEmail == savedEmail && inputPass == savedPass && selectedRole == savedRole) {
                sharedPref.edit().apply {
                    putBoolean("isLoggedIn", true)
                    putString("role", selectedRole)
                    apply()
                }
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                openDashboard(selectedRole)
            } else {
                Toast.makeText(this, "Credentials or Role mismatch!", Toast.LENGTH_LONG).show()
            }
        }

        btnSignUpLink.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun openDashboard(role: String) {
        val intent = when (role) {
            "Teacher" -> Intent(this, TeacherDashboardActivity::class.java)
            "CR" -> Intent(this, CrDashboardActivity::class.java)
            else -> Intent(this, StudentMainActivity::class.java)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}