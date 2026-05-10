package com.example.slotify.semproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlin.jvm.java
import com.example.slotify.R

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val btnContinue = findViewById<MaterialButton>(R.id.btnContinue)

        btnContinue.setOnClickListener {

            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)


            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}