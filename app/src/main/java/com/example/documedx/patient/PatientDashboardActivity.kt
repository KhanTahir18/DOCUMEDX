package com.example.documedx.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.documedx.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var profileCard: CardView
    private lateinit var editIcon: ImageView
    private lateinit var viewReportsCard: CardView
    private lateinit var uploadReportsCard: CardView
    private lateinit var appointmentsCard: CardView
    private lateinit var searchHospitalsCard: CardView
    private lateinit var settingsCard: CardView
    private lateinit var database: DatabaseReference

    private var phoneNo: String? = null
    private var profilePic: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)
        phoneNo = intent.getStringExtra("phoneNo")
        initViews()
        setupClickListeners()
        database = FirebaseDatabase.getInstance().getReference("Users").child(phoneNo!!)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadDataToProfileCard()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PatientDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
        loadDataToProfileCard()
        onBackPressedDispatcher.addCallback(this) {
            finishAffinity() // Or whatever you want to do on back press
        }
    }

    private fun loadDataToProfileCard() {
        database.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val patientName = "${dataSnapshot.child("firstName").getValue(String::class.java)} ${dataSnapshot.child("lastName").getValue(String::class.java)}"
                    val genderAndGender = "${dataSnapshot.child("gender").getValue(String::class.java)} / ${dataSnapshot.child("dob").getValue(String::class.java)}"
                    val phoneNumber = dataSnapshot.child("phoneNo").getValue(String::class.java)
                    val email = dataSnapshot.child("emailId").getValue(String::class.java)
                    val address = dataSnapshot.child("address").getValue(String::class.java)
                    val medicalHistory = dataSnapshot.child("medicalHistory").getValue(String::class.java)

                    findViewById<TextView>(R.id.tv_patient_name).text = patientName
                    findViewById<TextView>(R.id.tv_gender_age).text = genderAndGender
                    findViewById<TextView>(R.id.tv_phone_number).text = phoneNumber
                    findViewById<TextView>(R.id.tv_email).text = email
                    findViewById<TextView>(R.id.tv_address).text = address
                    findViewById<TextView>(R.id.tv_medical_history).text = medicalHistory
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            val name = data.getStringExtra("Patientname")
            val last = data.getStringExtra("Patientlastname")
            val address = data.getStringExtra("address")
            val phone = data.getStringExtra("phoneNo")
            val email = data.getStringExtra("emailId")
            val medicalHistory = data.getStringExtra("medicalHistory")
            val age = data.getStringExtra("age")
            val gender = data.getStringExtra("gender")
            if (name?.isNotEmpty() ?: false) {
                database.child("firstName").setValue(name)
            }
            if (last?.isNotEmpty() ?: false) {
                database.child("lastName").setValue(last)
            }
            if (address?.isNotEmpty() ?: false) {
                database.child("address").setValue(address)
            }
            if (phone?.isNotEmpty() ?: false) {
                database.child("phoneNo").setValue(phone)
            }
            if (email?.isNotEmpty() ?: false) {
                database.child("emailId").setValue(email)
            }
            if (medicalHistory?.isNotEmpty() ?: false) {
                database.child("medicalHistory").setValue(medicalHistory)
            }
            if (age?.isNotEmpty() ?: false) {
                database.child("dob").setValue(age)
            }
            if (gender?.isNotEmpty() ?: false) {
                database.child("gender").setValue(gender)
            }
        }
    }
    private fun setupClickListeners() {
        editIcon.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("phoneNo", phoneNo)
            startActivityForResult(intent,101)
        }

        viewReportsCard.setOnClickListener {
            val intent = Intent(this, ViewReportsActivity::class.java)
            intent.putExtra("phoneNo",phoneNo)
            startActivity(intent)
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