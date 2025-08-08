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
        var isPasswordVisible = false
        val setPasswordEditTextIc = binding.setPassNameInputField
        val conPasswordEditTextIc = binding.conPassNameInputField

        database = FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!)

        //Toggle fuction for set pass
        setPasswordEditTextIc.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (setPasswordEditTextIc.right - setPasswordEditTextIc.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        setPasswordEditTextIc.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        setPasswordEditTextIc.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_eye_open,
                            0
                        )
                    } else {
                        setPasswordEditTextIc.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        setPasswordEditTextIc.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_eye_closed,
                            0
                        )
                    }
                    // Move cursor to end
                    setPasswordEditTextIc.setSelection(setPasswordEditTextIc.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
        //Toggle fuction for confirm pass
        conPasswordEditTextIc.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (conPasswordEditTextIc.right - conPasswordEditTextIc.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        conPasswordEditTextIc.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        conPasswordEditTextIc.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_eye_open,
                            0
                        )
                    } else {
                        conPasswordEditTextIc.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        conPasswordEditTextIc.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_eye_closed,
                            0
                        )
                    }
                    // Move cursor to end
                    conPasswordEditTextIc.setSelection(conPasswordEditTextIc.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }


        //Gender spinner code
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
            val setPass = binding.setPassNameInputField.text.toString()
            val confirmPass = binding.conPassNameInputField.text.toString()
            val emailId = binding.emailNameInputField.text.toString().trim()
            val address = binding.addressNameInputField.text.toString()
            val qualification = binding.QualificationField.text.toString()
            val empId = binding.empIdField.text.toString().trim()
            val role = binding.roleField.text.toString()

            if(firstName.isEmpty() || lastName.isEmpty() || dOB.isEmpty() || gender.isEmpty() || phoneNo.isEmpty() || setPass.isEmpty() || confirmPass.isEmpty() || emailId.isEmpty() || address.isEmpty() || qualification.isEmpty() || empId.isEmpty() || role.isEmpty()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(setPass != confirmPass){
                Toast.makeText(this, "Pass not Matching", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Confirm Save")
            builder.setMessage("Are you sure you want to save these changes?")

            builder.setPositiveButton("Save") { _, _ ->
                // Prepare result Intent to send back data
                if (firstName.isNotEmpty() && lastName.isNotEmpty() && dOB.isNotEmpty() && gender.isNotEmpty() && phoneNo.isNotEmpty() && setPass.isNotEmpty() && confirmPass.isNotEmpty() && emailId.isNotEmpty() && address.isNotEmpty() && qualification.isNotEmpty() && empId.isNotEmpty() && role.isNotEmpty()) {
                    val resultIntent = Intent().apply {
                        putExtra("firstName", firstName)
                        putExtra("lastName", lastName)
                        putExtra("dOB", dOB)
                        putExtra("gender", gender)
                        putExtra("phoneNo", phoneNo)
                        putExtra("password", confirmPass)
                        putExtra("emailId", emailId)
                        putExtra("address", address)
                        putExtra("qualification", qualification)
                        putExtra("empId", empId)
                        putExtra("role", role)

                    }

                    database =
                        FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!)
                            .child("Departments").child(deptId!!).child("Staff")
                    database.child(empId).get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            Toast.makeText(this, "Emp Id already exists", Toast.LENGTH_SHORT)
                                .show()
                            binding.empIdField.text.clear()
                        } else {
                            database.child(empId).setValue(
                                Staff(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phoneNo = phoneNo,
                                    password = confirmPass,
                                    emailId = emailId,
                                    sex = gender,
                                    dateOfBirth = dOB,
                                    employeeId = empId,
                                    department = deptId,
                                    designation = role,
                                    qualification = qualification,
                                    address = address
                                )
                            )
                            setResult(Activity.RESULT_OK, resultIntent)
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
        }
        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}