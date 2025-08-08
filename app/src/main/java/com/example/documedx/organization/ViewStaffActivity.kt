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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        licence = sharedPref.getString("licence", null)

        binding = ActivityViewStaffInDeptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deptId = intent.getStringExtra("deptId")
        Toast.makeText(this, "$deptId,$licence", Toast.LENGTH_SHORT).show()

        // Set up RecyclerView and Adapter
        adapter = StaffAdapter(staffList){department ->
            deleteStaffFromFirebase(department)
        }
        binding.staffsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.staffsRecyclerView.adapter = adapter

        loadStaffFromFirebase()

    }


    private fun loadStaffFromFirebase() {
        if (licence.isNullOrEmpty()) return

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
                    Toast.makeText(this@ViewStaffActivity, "No departments found in DB", Toast.LENGTH_SHORT).show()
                }
                //cleans the old  list so we can replace it with fresh data.
                staffList.clear()

                //Loops through each department under "Departments".
                //getValue(Department::class.java) converts the Firebase snapshot into your Department data class.
                //If it's not null, it’s added to the list.
                for (deptSnapshot in snapshot.children) {
                    val staff = deptSnapshot.getValue(Staff::class.java)
                    if (staff != null) {
                        staffList.add(staff)
                    }
                }
                adapter.notifyDataSetChanged()
                showOrHideRecycler()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewStaffActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteStaffFromFirebase(staff: Staff) {
        if (!licence.isNullOrEmpty()) {
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("Organizations")
                .child(licence!!)
                .child("Departments")
                .child(deptId.toString())
                .child("Staff")
                .child(staff.employeeId.toString())

            dbRef.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Staff deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showOrHideRecycler() {
        binding.staffsRecyclerView.visibility =
            if (staffList.isEmpty()) View.GONE else View.VISIBLE
    }
}