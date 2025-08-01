package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.organization.OrganizationSignUpActivity
import com.example.documedx.databinding.OrganizationLoginActivityBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrganizationLoginActivity: AppCompatActivity() {
    private lateinit var binding: OrganizationLoginActivityBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrganizationLoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Organizations")

        //Click listener
        binding.LoginBtn.setOnClickListener {
            val licence = binding.orgLicenceInputField.text.toString()
            val password = binding.passInputField.text.toString()

            //checks if fields are filled
            if(licence.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill the Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //if the acc exits
            database.child(licence).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    val dbpass = snapshot.child("password").value.toString()
                    //checks if the user pass and database pass are equal
                    if(password == dbpass){
                        val name = snapshot.child("organizationName").value.toString()

                        //Starting the uPload image activity and passing licence to it
                        val intent = Intent(this, PatientUnderOrgazinationActivity::class.java)
                        intent.putExtra("licence", licence)
                        startActivity(intent)

                        Toast.makeText(this, "Welcome, $name", Toast.LENGTH_SHORT).show()
                        clearAllFields()
                    }else{
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Employee do not exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        //navigates to sign up page
        binding.createAnAccText.setOnClickListener {
            val intent = Intent(this, OrganizationSignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun clearAllFields() {
        binding.orgLicenceInputField.text.clear()
        binding.passInputField.text.clear()
    }
}