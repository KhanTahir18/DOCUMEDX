package com.example.documedx.staff

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.documedx.R

class StaffDashboardActivity : AppCompatActivity() {

    private lateinit var profileCard: CardView
    private lateinit var patientsCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        profileCard = findViewById(R.id.card_profile)
        patientsCard = findViewById(R.id.card_patients)
    }

    private fun setupClickListeners() {
        profileCard.setOnClickListener {
            // TODO: Implement profile functionality later
            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
        val empId = intent.getStringExtra("empId")
        patientsCard.setOnClickListener {
            val intent = Intent(this, MyPatientsActivity::class.java)
            intent.putExtra("empId", empId)
            startActivity(intent)
        }
    }
}