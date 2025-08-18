package com.example.documedx.organization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.Department
import com.example.documedx.R
import com.example.documedx.Staff
import com.example.documedx.databinding.AtivityAddStaffOrganizationPageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddStaffToDeptActivity: AppCompatActivity() {
    private lateinit var binding: AtivityAddStaffOrganizationPageBinding
    private lateinit var database: DatabaseReference
    private var licence: String? = null
    private var deptId: String? = null
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AtivityAddStaffOrganizationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deptId = intent.getStringExtra("deptId")
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        licence = sharedPref.getString("licence", null)

        Toast.makeText(this, "$licence,$deptId ", Toast.LENGTH_SHORT).show()

        database = FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!).child("Departments").child(deptId!!).child("Staffs")



        binding.btnSave.setOnClickListener {
            val firstName = binding.firstNameInputField.text.toString().trim()
            val lastName = binding.lastNameInputField.text.toString().trim()
            val empId = binding.empIdField.text.toString().trim()

            if(firstName.isEmpty() || lastName.isEmpty() || empId.isEmpty()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbEmpId = FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!).child("Staffs")
            dbEmpId.child(empId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setTitle("Confirm Save")
                    builder.setMessage("Are you sure you want to save these changes?")

                    builder.setPositiveButton("Save") { _, _ ->
                        // Prepare result Intent to send back data
                        if (firstName.isNotEmpty() && lastName.isNotEmpty() && empId.isNotEmpty()) {
//                    val resultIntent = Intent().apply {
//                        putExtra("firstName", firstName)
//                        putExtra("lastName", lastName)
//                        putExtra("empId", empId)
//
//                    }

                            database =
                                FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!)
                                    .child("Departments").child(deptId!!).child("Staff")
                            database.child(empId).get().addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    Toast.makeText(this, "Emp Id already exists", Toast.LENGTH_SHORT)
                                        .show()
                                    binding.empIdField.text.clear()
                                } else {
                                    database.child(empId).setValue(true)
                                    dbEmpId.child(empId).child("Departments").child(deptId!!).setValue(true)
//                            setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                }
                            }


                            // Set result as OK and finish this activity

                        } else {
                            Toast.makeText(this, "Please enter both ID and Name", Toast.LENGTH_SHORT).show()
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss() // Do nothing, stay on the page
                    }

                    builder.create().show()

                }else{
                    Toast.makeText(this, "Emp Id does not exist", Toast.LENGTH_SHORT).show()
                    binding.empIdField.text.clear()
                    return@addOnSuccessListener
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}