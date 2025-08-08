package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityPatientLoginBinding
import com.example.documedx.databinding.LoginPageStaffActivityBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPatientLoginBinding
    //creating database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //creating the database
        database = FirebaseDatabase.getInstance().getReference("Users")

        //adding on click listener on login btn
        binding.loginButton.setOnClickListener {
            val phoneNo = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            //Checking all fields are Filled
            if(phoneNo.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill all the Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking the password
            database.child(phoneNo).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    val dbpassword = snapshot.child("password").value.toString()
                    //checks if the user pass and db pass are same
                    if(password == dbpassword){
                        val name = snapshot.child("firstName").value.toString()
                        Toast.makeText(this, "Welcome ${name}", Toast.LENGTH_SHORT).show()
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

        //if user want to create a new acc
        binding.createAccText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun clearAllFields() {
        binding.phoneEditText.text.clear()
        binding.passwordEditText.text.clear()
    }
}