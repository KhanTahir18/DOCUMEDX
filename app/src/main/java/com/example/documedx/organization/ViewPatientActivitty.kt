package com.example.documedx.organization

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.Staff
import com.example.documedx.User
import com.example.documedx.databinding.ActivityOrganizationViewPatientBinding
import com.example.documedx.databinding.ActivityViewStaffInDeptBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.UTC

class ViewPatientActivitty: AppCompatActivity() {
    private lateinit var binding: ActivityOrganizationViewPatientBinding
    private val patientList = mutableListOf<User>()
    private lateinit var adapter: PatientInOrgAdapter
    private var licence: String? = null

    private lateinit var database: DatabaseReference

    private var deptName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrganizationViewPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        licence = intent.getStringExtra("licence")
        // Set up RecyclerView and Adapter
        adapter = PatientInOrgAdapter(patientList){ patient ->
            deleteStaffFromFirebase(patient)
        }
        binding.patientRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.patientRecyclerView.adapter = adapter
        loadStaffFromFirebase()

    }


    private fun loadStaffFromFirebase() {
        if (licence.isNullOrEmpty()) return

        database = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(licence!!)
            .child("Patients")

        //Whenever the data at the "Departments" node changes, this callback is triggered automatically.
        //This includes when data is added, updated, or deleted
        database.addValueEventListener(object : ValueEventListener {
            //This function runs whenever the data at that path is changed.
            override fun onDataChange(snapshot: DataSnapshot) {
                patientList.clear()
                if (!snapshot.exists()) {
                    showOrHideRecycler()
                    Toast.makeText(this@ViewPatientActivitty, "No staff found in this department", Toast.LENGTH_SHORT).show()
                    return
                }

                val userRef = FirebaseDatabase.getInstance()
                    .getReference("Users")

                val tempList = mutableListOf<User>()
                // Loop through empIds inside this dept
                for (patientSnapshot in snapshot.children) {
                    val phoneNo = patientSnapshot.key ?: continue

                    userRef.child(phoneNo).get().addOnSuccessListener { patientData ->
                        val user = patientData.getValue(User::class.java)
                        if (user != null) {
                            //check if the empId already exists in the staffList
                            if (user != null && !tempList.any { it.phoneNo == user.phoneNo }) {
                                tempList.add(user)
                            }

                            // when last child finishes loading, update adapter
                            if (phoneNo == snapshot.children.last().key) {
                                patientList.clear()
                                patientList.addAll(tempList)
                                adapter.notifyDataSetChanged()
                                showOrHideRecycler()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewPatientActivitty, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteStaffFromFirebase(user: User) {
        if (!licence.isNullOrEmpty()  && !user.phoneNo.isNullOrEmpty()) {
            val orgRef = FirebaseDatabase.getInstance()
                .getReference("Organizations")
                .child(licence!!)
                .child("Patients")
                .child(user.phoneNo)

            val userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(user.phoneNo)
                .child("Organizations")
                .child(licence!!)

            orgRef.removeValue()
            userRef.removeValue()
            Toast.makeText(this, "Patient Removed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showOrHideRecycler() {
        binding.patientRecyclerView .visibility =
            if (patientList.isEmpty()) View.GONE else View.VISIBLE
    }
}