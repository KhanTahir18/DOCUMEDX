package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.documedx.Organization
import com.example.documedx.R
import com.example.documedx.databinding.OrganizationSignUpPageActivityBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrganizationSignUpActivity: AppCompatActivity() {
    private lateinit var binding: OrganizationSignUpPageActivityBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = OrganizationSignUpPageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        database = FirebaseDatabase.getInstance().getReference("Organizations")

        //When sign up button is clicked
        binding.signUpBtn.setOnClickListener {
            val orgName = binding.hospitalNameInputField.text.toString()
            val address = binding.addressNameInputField.text.toString()
            val phoneNo = binding.phoneInputField.text.toString()
            val emailId = binding.emailNameInputField.text.toString().trim()
            val setPass = binding.setPassNameInputField.text.toString()
            val confirmPass = binding.conPassNameInputField.text.toString()
            val licence = binding.licenceInputField.text.toString().trim()
            val orgType = binding.orgTypeInputField.text.toString().trim()

            //All fields are filled
            if(orgName.isEmpty() || address.isEmpty() || phoneNo.isEmpty() || emailId.isEmpty() || setPass.isEmpty() || confirmPass.isEmpty() || licence.isEmpty() || orgType.isEmpty()){
                Toast.makeText(this, "Fill all Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Same pass
            if(confirmPass != setPass){
                Toast.makeText(this, "Enter same pass", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Checks if already registered
            database.child(licence).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    Toast.makeText(this, "Licence Already Exists", Toast.LENGTH_SHORT).show()
                }else{
                    val organization = Organization(
                        organizationName = orgName,
                        organizationType = orgType,
                        password = confirmPass,
                        phoneNo = phoneNo,
                        emailId = emailId,
                        address = address,
                        licence = licence
                    )
                    database.child(licence).setValue(organization).addOnSuccessListener {

                        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("licence", licence)
                        }

                        val intent = Intent(this, OrganizationDashboardActivity::class.java)
                        intent.putExtra("licence", licence)
                        startActivity(intent)

                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()

                        clearAllFields()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //navigates to login page
        binding.alreadyAnAccText.setOnClickListener {
            val intent = Intent(this, OrganizationLoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun clearAllFields() {
        binding.hospitalNameInputField.text.clear()
        binding.phoneInputField.text.clear()
        binding.addressNameInputField.text.clear()
        binding.emailNameInputField.text.clear()
        binding.setPassNameInputField.text.clear()
        binding.conPassNameInputField.text.clear()
        binding.licenceInputField.text.clear()
        binding.orgTypeInputField.text.clear()

    }
}