package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.LoginPageStaffActivityBinding
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

        //accessing the staff
        database = FirebaseDatabase.getInstance().getReference("Staffs")
        binding.LoginBtn.setOnClickListener {
            val phoneNo = binding.phoneNoInputField.text.toString().trim()
            val password = binding.passInputField.text.toString()

            //checking if the the fields are filled
            if(phoneNo.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            database.child(phoneNo).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    val dbpass = snapshot.child("password").value.toString()
                    if(password == dbpass){
                        val name = snapshot.child("firstName").value.toString()
                        Toast.makeText(this, "Welcome, $name!", Toast.LENGTH_SHORT).show()
                        clearAllFields()
                    }else{
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Phone no do not exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        //If user does'nt have an acc
        binding.createAnAccText.setOnClickListener {
            val intent = Intent(this, StaffSignUpActivity::class.java)
            startActivity(intent)
        }

    }


    private fun clearAllFields() {
        binding.phoneNoInputField.text.clear()
        binding.passInputField.text.clear()
    }
}