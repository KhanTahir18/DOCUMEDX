package com.example.documedx.organization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.Staff
import com.example.documedx.databinding.ActivityAddStaffInOrgBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddStaffInOrganizationActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAddStaffInOrgBinding
    private var licence: String? = null
    private var password: String? = null
    private lateinit var spinner: Spinner

    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStaffInOrgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        licence = intent.getStringExtra("licence")

        //setting database reference
        database = FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!).child("Staffs")

        binding.generatePassBtn.setOnClickListener {
            password = generateRandomPassword()
            binding.generatePassText.text = password
        }
        spinner = binding.genderSpinner
        val listItems = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }
        }
        binding.btnSave.setOnClickListener {
            val firstName = binding.firstNameInputField.text.toString().trim()
            val lastName = binding.lastNameInputField.text.toString().trim()
            val dOB = "${
                binding.dayDOBNameInputField.text.toString().trim()
            }/${
                binding.monthDOBNameInputField.text.toString().trim()
            }/${binding.yearDOBNameInputField.text.toString().trim()}"
            val gender = binding.genderSpinner.selectedItem.toString()
            val phoneNo = binding.phoneInputField.text.toString().trim()
            val emailId = binding.emailNameInputField.text.toString().trim()
            val address = binding.addressNameInputField.text.toString()
            val qualification = binding.QualificationField.text.toString()
            val empId = binding.empIdField.text.toString().trim()
            val role = binding.roleField.text.toString()

            if(firstName.isEmpty() || lastName.isEmpty() || dOB.isEmpty() || gender.isEmpty() || phoneNo.isEmpty() || emailId.isEmpty() || address.isEmpty() || qualification.isEmpty() || empId.isEmpty() || role.isEmpty()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password?.isEmpty() == true ){
                Toast.makeText(this, "Password is not generate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Confirm Save")
            builder.setMessage("Are you sure you want to save these changes?")

            builder.setPositiveButton("Save") { _, _ ->
                // Prepare result Intent to send back data
                if (firstName.isNotEmpty() && lastName.isNotEmpty() && dOB.isNotEmpty() && gender.isNotEmpty() && phoneNo.isNotEmpty() && emailId.isNotEmpty() && address.isNotEmpty() && qualification.isNotEmpty() && empId.isNotEmpty() && role.isNotEmpty() && (password?.isNotEmpty() == true)) {
//                    val resultIntent = Intent().apply {
//                        putExtra("firstName", firstName)
//                        putExtra("lastName", lastName)
//                        putExtra("dOB", dOB)
//                        putExtra("gender", gender)
//                        putExtra("phoneNo", phoneNo)
//                        putExtra("password", password)
//                        putExtra("emailId", emailId)
//                        putExtra("address", address)
//                        putExtra("qualification", qualification)
//                        putExtra("empId", empId)
//                        putExtra("role", role)
//                    }

                    database.child(empId).get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            Toast.makeText(this, "Emp Id already exists", Toast.LENGTH_SHORT).show()
                            binding.empIdField.text.clear()
                        } else {
                            database.child(empId).setValue(
                                Staff(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phoneNo = phoneNo,
                                    password = password,
                                    emailId = emailId,
                                    sex = gender,
                                    dateOfBirth = dOB,
                                    employeeId = empId,
                                    department = null,
                                    designation = role,
                                    qualification = qualification,
                                    address = address
                                )
                            )
//                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }
//
//
//                    // Set result as OK and finish this activity

                } else {
                    Toast.makeText(this, "Enter atleast one field", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Do nothing, stay on the page
            }

            builder.create().show()
        }

        //when cancel is clicked
        binding.btnCancel.setOnClickListener {
            finish()
        }

    }

    private fun generateRandomPassword(length: Int = 10): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#\$%^&*"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}