    package com.example.documedx

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
    import androidx.core.content.edit
    import com.example.documedx.databinding.LoginPageStaffActivityBinding
    import com.example.documedx.databinding.SignUpForStaffActivityBinding
    import com.example.documedx.staff.StaffDashboardActivity
    import com.google.firebase.Firebase
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase

    class StaffSignUpActivity: AppCompatActivity() {
        private lateinit var binding: SignUpForStaffActivityBinding
        private lateinit var database: DatabaseReference
        private lateinit var orgDatabase: DatabaseReference

        private lateinit var spinner: Spinner
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = SignUpForStaffActivityBinding.inflate(layoutInflater)
            setContentView(binding.root)
//
            //we are creating the database
            database = FirebaseDatabase.getInstance().getReference("Staffs")
            var isPasswordVisible = false
            val setPasswordEditTextIc = binding.setPassNameInputField
            val conPasswordEditTextIc = binding.conPassNameInputField

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

                }
            }

            binding.signUpBtn.setOnClickListener {
                var hospitalExists = false
                val firstName = binding.firstNameInputField.text.toString().trim()
                val lastName = binding.lastNameInputField.text.toString().trim()
                val phone = binding.phoneInputField.text.toString().trim()
                val setPass = binding.setPassNameInputField.text.toString().trim()
                val conPass = binding.conPassNameInputField.text.toString().trim()
                val email = binding.emailNameInputField.text.toString().trim()
                val gender = spinner.selectedItem.toString()
                val address = binding.addressNameInputField.text.toString()
                val dob = "${binding.dayDOBNameInputField.text}/${binding.monthDOBNameInputField.text}/${binding.yearDOBNameInputField.text}"
                val empId = binding.empIdInputField.text.toString().trim()
                val hospitalLicence = binding.hospitalLicenceField.text.toString().trim()

                orgDatabase = FirebaseDatabase.getInstance().getReference("Organizations").child(hospitalLicence)

                if (gender.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || setPass.isEmpty() || conPass.isEmpty() || email.isEmpty() || address.isEmpty() || dob.isEmpty() || empId.isEmpty() || hospitalLicence.isEmpty()) {
                    Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (setPass != conPass) {
                    Toast.makeText(this, "The password are not the same", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                orgDatabase.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(this, "Welcome $hospitalLicence", Toast.LENGTH_SHORT).show()
                        hospitalExists = true
                        database.child(empId).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                Toast.makeText(
                                    this,
                                    "Employee ID already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{
                                val staff = Staff(
                                    firstName = firstName,
                                    lastName = lastName,
                                    sex = gender,
                                    phoneNo = phone,
                                    emailId = email,
                                    address = address,
                                    dateOfBirth = dob,
                                    employeeId = empId,
                                    associatedHospital = hospitalLicence,
                                    password = conPass,
                                    designation = null,
                                    department = null,
                                    qualification = null

                                )
                                database.child(empId).setValue(staff).addOnSuccessListener {
                                    Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                    val sharedPref = getSharedPreferences("StaffData", MODE_PRIVATE)
                                    sharedPref.edit {
                                        putString("empId", empId)
                                        putString("associatedHospital", hospitalLicence)
                                    }

                                    val intent = Intent(this, StaffDashboardActivity::class.java)
                                    intent.putExtra("empId", empId)
                                    intent.putExtra("associatedHospital", hospitalLicence)
                                    startActivity(intent)
                                    clearAllFields()

                                }.addOnFailureListener {
                                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(this, "Hospital does not exist", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                }
            }
            binding.alreadyAnAccText.setOnClickListener {
                val intent = Intent(this, StaffLoginPageActivity::class.java)
                startActivity(intent)
            }
        }
        private fun clearAllFields() {
            binding.firstNameInputField.text.clear()
            binding.lastNameInputField.text.clear()
            binding.phoneInputField.text.clear()
            binding.setPassNameInputField.text.clear()
            binding.conPassNameInputField.text.clear()
            binding.emailNameInputField.text.clear()
            binding.dayDOBNameInputField.text.clear()
            binding.monthDOBNameInputField.text.clear()
            binding.yearDOBNameInputField.text.clear()
            binding.addressNameInputField.text.clear()
            binding.empIdInputField.text.clear()
            binding.hospitalLicenceField.text.clear()
            binding.genderSpinner.setSelection(0)

        }
    }






