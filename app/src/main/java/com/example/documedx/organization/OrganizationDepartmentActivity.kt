//package com.example.documedx.organization
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.documedx.Department
//import com.example.documedx.databinding.OrganizationDeptActivityBinding
//import com.google.firebase.database.DatabaseReference
//
//class OrganizationDepartmentActivity : AppCompatActivity() {
//    private lateinit var binding: OrganizationDeptActivityBinding
//    private val departmentList = mutableListOf<Department>()
//    private lateinit var adapter: DepartmentAdapter
//
//
//    private lateinit var database: DatabaseReference
//
//    companion object {
//        const val ADD_DEPARTMENT_REQUEST = 101
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = OrganizationDeptActivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        adapter = DepartmentAdapter(departmentList)
//        binding.departmentsRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.departmentsRecyclerView.adapter = adapter
//
//        showOrHideRecycler()
//
//        val licence = intent.getStringExtra("licence")
//
//        binding.addDeptBtn.setOnClickListener {
//            val intent = Intent(this, AddDepartmentActivity::class.java)
//            intent.putExtra("licence", licence)
//            startActivityForResult(intent, ADD_DEPARTMENT_REQUEST)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == ADD_DEPARTMENT_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            val deptId = data.getStringExtra("deptId")
//            val deptName = data.getStringExtra("deptName")
//
//            if (!deptId.isNullOrEmpty() && !deptName.isNullOrEmpty()) {
//                val department = Department(deptId = deptId, deptName = deptName)
//                departmentList.add(department)
//                adapter.notifyItemInserted(departmentList.size - 1)
//                showOrHideRecycler()
//            }
//        }
//    }
//
//    private fun showOrHideRecycler() {
//        if (departmentList.isEmpty()) {
//            binding.departmentsRecyclerView.visibility = android.view.View.GONE
//        } else {
//            binding.departmentsRecyclerView.visibility = android.view.View.VISIBLE
//        }
//    }
//}

//new code
package com.example.documedx.organization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.Department
import com.example.documedx.databinding.OrganizationDeptActivityBinding
import com.google.firebase.database.*

class OrganizationDepartmentActivity : AppCompatActivity() {
    private lateinit var binding: OrganizationDeptActivityBinding
    private val departmentList = mutableListOf<Department>()
    private lateinit var adapter: DepartmentAdapter
    private lateinit var database: DatabaseReference
    private var licence: String? = null

    companion object {
        const val ADD_DEPARTMENT_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrganizationDeptActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView and Adapter
        adapter = DepartmentAdapter(departmentList){department ->
            deleteDepartmentFromFirebase(department)
        }
        binding.departmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.departmentsRecyclerView.adapter = adapter

        // Get licence value from intent
        licence = intent.getStringExtra("licence")

        // Load departments from Firebase
        loadDepartmentsFromFirebase()

        // Add department button click
        binding.addDeptBtn.setOnClickListener {
            val intent = Intent(this, AddDepartmentActivity::class.java)
            intent.putExtra("licence", licence)
            startActivityForResult(intent, ADD_DEPARTMENT_REQUEST)
        }
    }

    private fun loadDepartmentsFromFirebase() {
        if (licence.isNullOrEmpty()) return

        database = FirebaseDatabase.getInstance()
            .getReference("Organizations")
            .child(licence!!)
            .child("Departments")

        //Whenever the data at the "Departments" node changes, this callback is triggered automatically.
        //This includes when data is added, updated, or deleted
        database.addValueEventListener(object : ValueEventListener {

            //This function runs whenever the data at that path is changed.
            override fun onDataChange(snapshot: DataSnapshot) {

                //cleans the old  list so we can replace it with fresh data.
                departmentList.clear()

                //Loops through each department under "Departments".
                //getValue(Department::class.java) converts the Firebase snapshot into your Department data class.
                //If it's not null, it’s added to the list.
                for (deptSnapshot in snapshot.children) {
                    val department = deptSnapshot.getValue(Department::class.java)
                    if (department != null) {
                        departmentList.add(department)
                    }
                }
                adapter.notifyDataSetChanged()
                showOrHideRecycler()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrganizationDepartmentActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteDepartmentFromFirebase(department: Department) {
        if (!licence.isNullOrEmpty()) {
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("Organizations")
                .child(licence!!)
                .child("Departments")
                .child(department.deptId.toString())

            dbRef.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Department deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Firebase listener already handles live updates, so nothing needed here
    }

    private fun showOrHideRecycler() {
        binding.departmentsRecyclerView.visibility =
            if (departmentList.isEmpty()) View.GONE else View.VISIBLE
    }
}
