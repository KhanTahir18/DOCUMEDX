    package com.example.documedx

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.example.documedx.databinding.LoginPageStaffActivityBinding
    import com.example.documedx.databinding.SignUpForStaffActivityBinding
    import com.google.firebase.Firebase
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase

    class StaffSignUpActivity: AppCompatActivity() {
        private lateinit var binding: SignUpForStaffActivityBinding
        private lateinit var database: DatabaseReference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = SignUpForStaffActivityBinding.inflate(layoutInflater)
            setContentView(binding.root)

            //we are creating the database
            database = FirebaseDatabase.getInstance().getReference("Staffs")

            //setting on click listener
            binding.signUpBtn.setOnClickListener {
                val firstName = binding.firstNameInputField.text.toString().trim()
                val lastName = binding.lastNameInputField.text.toString().trim()
                val phoneNo = binding.phoneNoInputField.text.toString().trim()
                val setPass = binding.setPasswordInputField.text.toString()
                val confirmPass = binding.confirmPasswordInputField.text.toString()
                val emailId = binding.userAddressInputField.text.toString().trim()
                val sex = binding.sexInputField.text.toString().trim()
                val dateOfBirth = binding.DOBInputField.text.toString().trim()
                val empId = binding.employeeIdInputField.text.toString().trim()
                val department = binding.departmentInputField.text.toString().trim()
                val role = binding.employeeRoleInputField.text.toString().trim()
                val associatedHospital = binding.associatedHospitalInputField.text.toString()

                //checking if any fields are empty
                if(firstName.isEmpty() || associatedHospital.isEmpty() || lastName.isEmpty() || phoneNo.isEmpty() || setPass.isEmpty() || confirmPass.isEmpty() || emailId.isEmpty() || sex.isEmpty() || dateOfBirth.isEmpty() || empId.isEmpty() || department.isEmpty() || role.isEmpty()){
                    Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //checking if the passwords are matching
                if(setPass != confirmPass){
                    Toast.makeText(this, "The password are not correct", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //To make sure there are no staff with duplicate phone numbers
                database.child(empId).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()){
                        //msg when duplicate numbers are detected
                        Toast.makeText(this, "EmpId already exists", Toast.LENGTH_SHORT).show()
                    }else{
                        //set values in data class
                        val staff = Staff(
                            firstName = firstName,
                            secondName = lastName,
                            phoneNo = phoneNo,
                            password = setPass,
                            emailId = emailId,
                            sex = sex,
                            dateOfBirth = dateOfBirth,
                            employeeId = empId,
                            department = department,
                            designation = role,
                            associatedHospital = associatedHospital)
                        //passing the value to database
                        database.child(empId).setValue(staff).addOnSuccessListener {
                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                            clearAllFields()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            binding.alreadyAnAccText.setOnClickListener {
                val intent = Intent(this, StaffLoginPageActivity::class.java)
                startActivity(intent)
            }
        }//clearing all fields
        private fun clearAllFields() {
            binding.firstNameInputField.text.clear()
            binding.lastNameInputField.text.clear()
            binding.phoneNoInputField.text.clear()
            binding.setPasswordInputField.text.clear()
            binding.confirmPasswordInputField.text.clear()
            binding.userAddressInputField.text.clear()
            binding.sexInputField.text.clear()
            binding.DOBInputField.text.clear()
            binding.employeeIdInputField.text.clear()
            binding.departmentInputField.text.clear()
            binding.employeeRoleInputField.text.clear()
            binding.associatedHospitalInputField.text.clear()
        }
    }




