package com.example.documedx.staff

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.MedicalReport
import com.example.documedx.utilis.PdfGenerator
import com.example.documedx.R
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.documedx.OrganizationReport
import com.example.documedx.databinding.ActivityPatientDetailsBinding
import com.example.documedx.organization.MyPatientsReportsAdapter
import com.example.documedx.organization.OrganizationReportAdapter
import com.example.documedx.patient.ActivityUploadFiles
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Role
import io.appwrite.Permission
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class PatientDetailsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPatientDetailsBinding

    private lateinit var reportsAdapter: MyPatientsReportsAdapter
    private val reportsList = mutableListOf<OrganizationReport>()

    private var patientId: String? = null
    private var licence: String? = null

    private var empId: String? = null

    private val bucketId = "688b111000356abeadfb"
    private val projectId = "6888c59c00344d7b9867"
    private lateinit var database: DatabaseReference
     

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientId = intent.getStringExtra("phoneNo")

        //getting licence
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        licence = sharedPref.getString("associatedHospital", null)
        empId = sharedPref.getString("empId", null)
        Toast.makeText(this, "$patientId, $licence", Toast.LENGTH_SHORT).show()

        database = FirebaseDatabase.getInstance().getReference("Users").child(patientId!!)
        database.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                binding.patientNameTV.text = "${dataSnapshot.child("firstName").value} ${dataSnapshot.child("lastName").value}"
                binding.ageTv.text = dataSnapshot.child("dob").value.toString()
                binding.genderTv.text = dataSnapshot.child("gender").value.toString()
                binding.medicalHistoryTv.text = dataSnapshot.child("medicalHistory").value.toString()
            }else{
                Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show()
            }


        }
        setupRecyclerView()
        listenForReports()
    }

    private fun setupRecyclerView() {
        reportsAdapter = MyPatientsReportsAdapter(this, reportsList)
        binding.patientReportRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@PatientDetailsActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = reportsAdapter
        }
    }

    private fun listenForReports() {
        if (licence!!.isEmpty())return
        val reportsRef = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(licence!!) // licence of org
            .child("Staffs")
            .child(empId!!)
            .child("My Patients")
            .child(patientId!!)
            .child("Reports")

        reportsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<OrganizationReport>()
                for (reportSnap in snapshot.children) {
                    val report = reportSnap.getValue(OrganizationReport::class.java)
                    report?.let { tempList.add(it) }
                }
                if (tempList.isNotEmpty()) {
                    binding.patientReportRecyclerView.visibility = View.VISIBLE
                } else {
                    binding.patientReportRecyclerView.visibility = View.GONE
                }
                reportsAdapter.updateList(tempList)
                binding.patientReportRecyclerView.addOnChildAttachStateChangeListener(
                    object : RecyclerView.OnChildAttachStateChangeListener {
                        override fun onChildViewAttachedToWindow(view: View) {
                            val shareBtn = view.findViewById<View>(R.id.iv_share_icon)
                            shareBtn?.visibility = View.GONE
                        }

                        override fun onChildViewDetachedFromWindow(view: View) {}
                    }
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PatientDetailsActivity,
                    "Failed to load reports: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}