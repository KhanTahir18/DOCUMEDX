package com.example.documedx.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.documedx.R

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var profileCard: CardView
    private lateinit var editIcon: ImageView
    private lateinit var viewReportsCard: CardView
    private lateinit var uploadReportsCard: CardView
    private lateinit var appointmentsCard: CardView
    private lateinit var searchHospitalsCard: CardView
    private lateinit var settingsCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        profileCard = findViewById(R.id.card_profile)
        editIcon = findViewById(R.id.iv_edit_profile)
        viewReportsCard = findViewById(R.id.card_view_reports)
        uploadReportsCard = findViewById(R.id.card_upload_reports)
        appointmentsCard = findViewById(R.id.card_appointments)
        searchHospitalsCard = findViewById(R.id.card_search_hospitals)
        settingsCard = findViewById(R.id.card_settings)
    }

    private fun setupClickListeners() {
        editIcon.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        viewReportsCard.setOnClickListener {
            startActivity(Intent(this, ViewReportsActivity::class.java))
        }

        uploadReportsCard.setOnClickListener {
            Toast.makeText(this, "Upload Reports - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        appointmentsCard.setOnClickListener {
            Toast.makeText(this, "Appointments - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        searchHospitalsCard.setOnClickListener {
            Toast.makeText(this, "Search Hospitals - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        settingsCard.setOnClickListener {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }
}