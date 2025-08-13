package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.documedx.databinding.ActivityIntroBinding
import com.example.documedx.organization.OrganizationSignUpActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IntroActivity: AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val phoneNo = sharedPref.getString("phoneNo", null)
        val licence = sharedPref.getString("licence", null)
        val empId = sharedPref.getString("empId", null)


        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Continue as a patient
        binding.staffBtn.setOnClickListener {
            sharedPref.edit {
                putString("role", "staff")
            }
            if (empId == null) {
            val intent = Intent(this, StaffLoginPageActivity::class.java)
            startActivity(intent)
            }else{
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
            }
//            val intent = Intent(this, StaffSignUpActivity::class.java)
//            startActivity(intent)
        }


        binding.patientBtn.setOnClickListener {
            sharedPref.edit {
                putString("role", "patient")
            }
            if (phoneNo == null) {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.organizationBtn.setOnClickListener {
            sharedPref.edit {
                putString("role", "organization")
            }
            if (licence == null) {
                val intent = Intent(this, OrganizationSignUpActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
            }
        }
    }
}