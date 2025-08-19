package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityOrganizationAddPatientsReportBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrganizationPatientsDetailsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOrganizationAddPatientsReportBinding
    private var patientPhoneNo: String? = null
    private lateinit var userDatabase: DatabaseReference

    private var licence: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationAddPatientsReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //geting phone no
        patientPhoneNo = intent.getStringExtra("patientPhoneNo")
        Toast.makeText(this, "phoneNO: ${patientPhoneNo}", Toast.LENGTH_SHORT).show()


        //getting licence
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        licence = sharedPref.getString("licence", null)

        //loading data from fire base
        userDatabase = FirebaseDatabase.getInstance().getReference("Users").child(patientPhoneNo!!)
        userDatabase.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                binding.patientNameTV.text = "${dataSnapshot.child("firstName").value} ${dataSnapshot.child("lastName").value}"
                binding.ageTv.text = dataSnapshot.child("dob").value.toString()
                binding.genderTv.text = dataSnapshot.child("gender").value.toString()
                binding.medicalHistoryTv.text = dataSnapshot.child("medicalHistory").value.toString()
            }else{
                Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadBtn.setOnClickListener {
            val intent = Intent(this, OrganizationUploadPatientReport::class.java)
            intent.putExtra("patientPhoneNo", patientPhoneNo)
            intent.putExtra("licence", licence)
            startActivity(intent)
        }

    }
}