package com.example.documedx.staff

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import com.example.documedx.R
import com.example.documedx.databinding.ActivityStaffDashboardBinding

class StaffDashboardActivity : AppCompatActivity() {

    private lateinit var profileCard: CardView
    private lateinit var patientsCard: CardView
    private var empId: String? = null
    private  var associatedHospital: String? = null

    private lateinit var binding: ActivityStaffDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        empId = intent.getStringExtra("empId").toString()
        associatedHospital = intent.getStringExtra("associatedHospital").toString()
        Toast.makeText(this,"${empId}, ${associatedHospital}", Toast.LENGTH_SHORT).show()

//        initViews()
//        setupClickListeners()

        onBackPressedDispatcher.addCallback(this) {
            finishAffinity() // Or whatever you want to do on back press
        }
    }

//    private fun initViews() {
//        profileCard = findViewById(R.id.card_profile)
//        patientsCard = findViewById(R.id.card_patients)
//    }
//
//    private fun setupClickListeners() {
//        profileCard.setOnClickListener {
//            // TODO: Implement profile functionality later
//            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show()
//        }
//        val empId = intent.getStringExtra("empId")
//        patientsCard.setOnClickListener {
//            val intent = Intent(this, MyPatientsActivity::class.java)
//            intent.putExtra("empId", empId)
//            startActivity(intent)
//        }
//    }
}