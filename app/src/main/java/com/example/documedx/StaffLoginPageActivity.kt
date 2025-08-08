package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.documedx.databinding.LoginPageStaffActivityBinding
import com.example.documedx.staff.StaffDashboardActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StaffLoginPageActivity: AppCompatActivity()  {
    //creating the database reference
    private lateinit var binding: LoginPageStaffActivityBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageStaffActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isPasswordVisible = false
        val setPasswordEditTextIc = binding.setPassNameInputField

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

        //creating the database
        database = FirebaseDatabase.getInstance().getReference("Organizations")

        //adding on click listener on login btn
        binding.loginBtn.setOnClickListener {
            val hospitalName = binding.hospitalNameInputField.text.toString()
            val deptId = binding.deptIDInputField.text.toString()
            val empId = binding.empIdInputField.text.toString().trim()
            val password = binding.setPassNameInputField.text.toString()

            //Checking all fields are Filled
            if(empId.isEmpty() || password.isEmpty() || hospitalName.isEmpty() || deptId.isEmpty()){
                Toast.makeText(this, "Fill all the Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking the password
            database.child(hospitalName).child("Departments").child(deptId).child("Staff").child(empId).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    val dbpassword = snapshot.child("password").value.toString()
                    //checks if the user pass and db pass are same
                    if(password == dbpassword){
                        val name = snapshot.child("firstName").value.toString()
                        Toast.makeText(this, "Welcome ${name}", Toast.LENGTH_SHORT).show()
                        // Shared pref
                        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("empId", empId)
                            putString("empLicence", hospitalName)
                            putString("empDeptId", deptId)
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("empId", empId)
                        intent.putExtra("empLicence", hospitalName)
                        intent.putExtra("empDeptId", deptId)
                        startActivity(intent)
                        clearAllFields()
                    }else{
                        Toast.makeText(this, "Incorrct Password", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }



//        //accessing the staff
//        database = FirebaseDatabase.getInstance().getReference("Staffs")
//        binding.LoginBtn.setOnClickListener {
//            val empId = binding.employeeIdInputField.text.toString().trim()
//            val password = binding.passInputField.text.toString()
//
//            //checking if the the fields are filled
//            if(empId.isEmpty() || password.isEmpty()){
//                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            //Checking if the password is correct
//            database.child(empId).get().addOnSuccessListener { snapshot ->
//                if(snapshot.exists()){
//                    val dbpass = snapshot.child("password").value.toString()
//                    //checks if the user pass and database pass are equal
//                    if(password == dbpass){
//                        val name = snapshot.child("firstName").value.toString()
//                        Toast.makeText(this, "Welcome, $name!", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, StaffDashboardActivity::class.java)
//                        intent.putExtra("empId",empId)
//                        startActivity(intent)
//                        clearAllFields()
//                    }else{
//                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
//                    }
//                }else{
//                    Toast.makeText(this, "Employee do not exist", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        //If user does'nt have an acc
//        binding.createAnAccText.setOnClickListener {
//            val intent = Intent(this, StaffSignUpActivity::class.java)
//            startActivity(intent)
//        }
//
    }
//
//
    private fun clearAllFields() {
        binding.empIdInputField.text.clear()
        binding.setPassNameInputField.text.clear()
        binding.hospitalNameInputField.text.clear()
        binding.deptIDInputField.text.clear()
    }

}