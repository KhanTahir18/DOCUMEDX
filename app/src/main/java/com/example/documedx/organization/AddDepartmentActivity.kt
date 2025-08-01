package com.example.documedx.organization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.Department
import com.example.documedx.databinding.OrganizationActivityAddDepartmentBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddDepartmentActivity : AppCompatActivity() {
    private lateinit var binding: OrganizationActivityAddDepartmentBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrganizationActivityAddDepartmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val licence = intent.getStringExtra("licence").toString()

        // Handle Save button click
        binding.btnSave.setOnClickListener {
            val id = binding.etDeptId.text.toString().trim()
            val name = binding.etDeptName.text.toString().trim()

            // Prepare result Intent to send back data
            if (id.isNotEmpty() && name.isNotEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("deptId", id)
                    putExtra("deptName", name)
                }

                database = FirebaseDatabase.getInstance().getReference("Organizations").child(licence).child("Departments")
                database.child(id).get().addOnSuccessListener { snapshot ->
                    if(snapshot.exists()){
                        Toast.makeText(this, "Department Id already exists", Toast.LENGTH_SHORT).show()
                        binding.etDeptId.text.clear()
                    }else{
                        database.child(id).setValue(Department(id, name))
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }


                // Set result as OK and finish this activity

            } else {
                Toast.makeText(this, "Please enter both ID and Name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
