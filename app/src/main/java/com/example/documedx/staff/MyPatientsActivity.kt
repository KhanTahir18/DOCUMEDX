package com.example.documedx.staff

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.OrganizationReport
import com.example.documedx.User
import com.example.documedx.databinding.ActivityMyPatientsBinding
import com.example.documedx.organization.MyPatientAdapter
import com.google.firebase.database.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyPatientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPatientsBinding
    private var empId: String? = null
    private var associatedHospital: String? = null
    private lateinit var adapter: MyPatientAdapter
    private lateinit var orgDB: DatabaseReference
    private val patientList = mutableListOf<User>()

    // Values from QR
    private var reportId: String? = null
    private var patientId: String? = null

    private var fromOrg: String? = null

    private var reportUrl: String? = null
    private var prescriptionUrl: String? = null

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedData = result.contents  // Example: "reportId=123;patientId=456"
            val dataMap = scannedData.split(";").associate {
                val parts = it.split("=", limit = 2)
                parts[0] to parts.getOrElse(1) { "" }
            }
            reportId = dataMap["reportId"]
            patientId = dataMap["patientId"]
            fromOrg = dataMap["fromOrg"]
            reportUrl = dataMap["reportUrl"]

            Toast.makeText(this, "Scan Successful $reportId, $patientId, $fromOrg, $reportUrl", Toast.LENGTH_SHORT).show()

            if (reportId != null && patientId != null) {
                savePatientToFirebase(patientId!!, reportId!!, reportUrl!!, fromOrg!!)
            }
        } else {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        empId = intent.getStringExtra("empId")
        associatedHospital = intent.getStringExtra("associatedHospital")

        binding.scannerImgView.setOnClickListener {
            startQRCodeScanner()
        }

        adapter = MyPatientAdapter(patientList) { patient ->
            deletePatientFromFirebase(patient)
        }
        binding.patientRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.patientRecyclerView.adapter = adapter

        // Initial load
        loadPatientsFromFirebase()
    }

    private fun savePatientToFirebase(patientId: String, reportId: String, reportUrl: String, fromOrg: String) {
        if (empId.isNullOrEmpty() || associatedHospital.isNullOrEmpty()) return

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val report = OrganizationReport(
            id = reportId,
            fromOrg = fromOrg,
            toPatient = patientId,
            reportUrl = reportUrl,
            dateWhenSharred = currentDate,
            timeWhenShared = currentTime
        )
        // Staff path: Staff > My Patients > patientId > Reports > reportId
        val staffRef = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(associatedHospital!!)
            .child("Staffs")
            .child(empId!!)
            .child("My Patients")
            .child(patientId)
            .child("Reports")
            .child(reportId)

        staffRef.setValue(report)

        // User path: Users > patientId > Doctors > empId = true
        val userRef = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(patientId)
            .child("Staffs")
            .child(empId!!)

        userRef.setValue(true)

        Toast.makeText(this, "Patient Added Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun deletePatientFromFirebase(user: User) {
        if (empId.isNullOrEmpty() || associatedHospital.isNullOrEmpty()) return

        // Remove from Staff node
        val staffRef = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(associatedHospital!!)
            .child("Staffs")
            .child(empId!!)
            .child("My Patients")
            .child(user.phoneNo!!)

        staffRef.removeValue()

        // Remove from Users node
        val userRef = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(user.phoneNo!!)
            .child("Staffs")
            .child(empId!!)

        userRef.removeValue()

        Toast.makeText(this, "Patient Removed", Toast.LENGTH_SHORT).show()
    }

    private fun loadPatientsFromFirebase() {
        if (empId.isNullOrEmpty() || associatedHospital.isNullOrEmpty()) return

        orgDB = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(associatedHospital!!)
            .child("Staffs")
            .child(empId!!)
            .child("My Patients")

        orgDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                patientList.clear()
                if (!snapshot.exists()) {
                    showOrHideRecycler()
                    return
                }

                val userRef = FirebaseDatabase.getInstance().getReference("Users")
                val tempList = mutableListOf<User>()

                val patients = snapshot.children.map { it.key!! }
                patients.forEach { patientId ->
                    userRef.child(patientId).get().addOnSuccessListener { patientData ->
                        val user = patientData.getValue(User::class.java)
                        if (user != null && !tempList.any { it.phoneNo == user.phoneNo }) {
                            tempList.add(user)
                        }

                        // When last one is done, update adapter
                        if (patientId == patients.last()) {
                            patientList.clear()
                            patientList.addAll(tempList)
                            adapter.notifyDataSetChanged()
                            showOrHideRecycler()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyPatientsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showOrHideRecycler() {
        binding.patientRecyclerView.visibility =
            if (patientList.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun startQRCodeScanner() {
        val options = ScanOptions()
        options.setPrompt("Scan the QR code")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }
}
