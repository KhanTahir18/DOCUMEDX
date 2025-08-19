package com.example.documedx.organization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityViewStaffInDeptBinding
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.Department
import com.example.documedx.Staff
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewStaffActivity: AppCompatActivity() {
    private lateinit var binding:ActivityViewStaffInDeptBinding
    private val staffList = mutableListOf<Staff>()
    private lateinit var adapter: StaffAdapter
    private var licence: String? = null

    private lateinit var database: DatabaseReference

    private var deptId: String? = null
    private var deptName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        licence = sharedPref.getString("licence", null)

        binding = ActivityViewStaffInDeptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deptId = intent.getStringExtra("deptId")
        Toast.makeText(this, "$deptId,$licence", Toast.LENGTH_SHORT).show()
        deptName = intent.getStringExtra("deptName")
        // Set up RecyclerView and Adapter
        adapter = StaffAdapter(staffList){department ->
            deleteStaffFromFirebase(department)
        }
        binding.staffsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.staffsRecyclerView.adapter = adapter
        binding.Heading.setText(deptName)
        binding.subHeading.setText(deptId)
        loadStaffFromFirebase()

    }


    private fun loadStaffFromFirebase() {
        if (licence.isNullOrEmpty() || deptId.isNullOrEmpty()) return

        database = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(licence!!)
            .child("Departments")
            .child(deptId.toString())
            .child("Staff")

        //Whenever the data at the "Departments" node changes, this callback is triggered automatically.
        //This includes when data is added, updated, or deleted
        database.addValueEventListener(object : ValueEventListener {
            //This function runs whenever the data at that path is changed.
            override fun onDataChange(snapshot: DataSnapshot) {
                staffList.clear()
                if (!snapshot.exists()) {
                    showOrHideRecycler()
                    Toast.makeText(this@ViewStaffActivity, "No staff found in this department", Toast.LENGTH_SHORT).show()
                    return
                }

                val orgStaffRef = FirebaseDatabase.getInstance()
                    .getReference("Organizations")
                    .child(licence!!)
                    .child("Staffs")

                val tempList = mutableListOf<Staff>()
                // Loop through empIds inside this dept
                for (staffSnapshot in snapshot.children) {
                    val empId = staffSnapshot.key ?: continue

                    orgStaffRef.child(empId).get().addOnSuccessListener { staffData ->
                        val staff = staffData.getValue(Staff::class.java)
                        if (staff != null) {
                            //check if the empId already exists in the staffList
                            if (staff != null && !tempList.any { it.employeeId == staff.employeeId }) {
                                tempList.add(staff)
                            }

                            // when last child finishes loading, update adapter
                            if (empId == snapshot.children.last().key) {
                                staffList.clear()
                                staffList.addAll(tempList)
                                adapter.notifyDataSetChanged()
                                showOrHideRecycler()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewStaffActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteStaffFromFirebase(staff: Staff) {
        if (!licence.isNullOrEmpty() && !deptId.isNullOrEmpty()) {
            val deptRef = FirebaseDatabase.getInstance()
                .getReference("Organizations")
                .child(licence!!)
                .child("Departments")
                .child(deptId!!)
                .child("Staff")
                .child(staff.employeeId!!)

            val orgStaffRef = FirebaseDatabase.getInstance()
                .getReference("Organizations")
                .child(licence!!)
                .child("Staffs")
                .child(staff.employeeId)
                .child("Departments")
                .child(deptId!!)

            deptRef.removeValue()
            orgStaffRef.removeValue()
            Toast.makeText(this, "Staff removed from department", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showOrHideRecycler() {
        binding.staffsRecyclerView.visibility =
            if (staffList.isEmpty()) View.GONE else View.VISIBLE
    }
}