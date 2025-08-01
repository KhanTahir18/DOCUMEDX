package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityIntroBinding
import com.example.documedx.organization.OrganizationSignUpActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IntroActivity: AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Continue as a patient
        binding.staffBtn.setOnClickListener {
            val intent = Intent(this, StaffSignUpActivity::class.java)
            startActivity(intent)
        }

        binding.patientBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.organizationBtn.setOnClickListener {
            val intent = Intent(this, OrganizationSignUpActivity::class.java)
            startActivity(intent)
        }
    }
}