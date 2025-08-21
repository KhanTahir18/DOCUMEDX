package com.example.documedx.organization

import com.example.documedx.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.documedx.OrganizationReport
import com.example.documedx.databinding.ActivityOrganizationAddPatientsReportBinding
import com.example.documedx.databinding.ItemReportCardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.RecyclerView

class OrganizationPatientsDetailsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOrganizationAddPatientsReportBinding
    private var patientPhoneNo: String? = null
    private lateinit var userDatabase: DatabaseReference
    private lateinit var    reportAdapter: OrganizationReportAdapter
    private val reportsList = mutableListOf<OrganizationReport>()


    private var proPic:String? = null

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

        setupRecyclerView()
        listenForReports()
        if(proPic== null){
            //doNothing
        }else{
            Glide.with(this@OrganizationPatientsDetailsActivity).load(proPic).into(binding.profileImg)
        }
    }
    private fun setupRecyclerView() {
        reportAdapter = OrganizationReportAdapter(this, reportsList)
        binding.patientReportRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@OrganizationPatientsDetailsActivity,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = reportAdapter
        }
    }

    private fun listenForReports() {
        val reportsRef = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(licence!!) // licence of org
            .child("Patients")
            .child(patientPhoneNo!!)
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
                reportAdapter.updateList(tempList)
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
                Toast.makeText(this@OrganizationPatientsDetailsActivity,
                    "Failed to load reports: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}