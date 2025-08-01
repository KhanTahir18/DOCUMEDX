package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.Organization
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
        database = FirebaseDatabase.getInstance().getReference("Organizations")

        //When sign up button is clicked
        binding.signUpBtn.setOnClickListener {
            val orgName = binding.orgNameInputField.text.toString()
            val address = binding.orgAddressInputField.text.toString()
            val phoneNo = binding.argPhoneInputField.text.toString()
            val emailId = binding.orgEmailAddressInputField.text.toString()
            val setPass = binding.orgSetPassInputField.text.toString()
            val confirmPass = binding.orgConfirmPassInputField.text.toString()
            val licence = binding.orgLicenceInputField.text.toString()
            val orgType = binding.orgTypeInputField.text.toString()

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
        binding.orgNameInputField.text.clear()
        binding.argPhoneInputField.text.clear()
        binding.orgAddressInputField.text.clear()
        binding.orgEmailAddressInputField.text.clear()
        binding.orgSetPassInputField.text.clear()
        binding.orgConfirmPassInputField.text.clear()
        binding.orgLicenceInputField.text.clear()
        binding.orgTypeInputField.text.clear()

    }
}