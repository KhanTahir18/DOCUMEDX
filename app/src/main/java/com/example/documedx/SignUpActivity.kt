package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.SignUpPageActiviyBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity: AppCompatActivity() {
    private lateinit var binding: SignUpPageActiviyBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpPageActiviyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //access the database
        database = FirebaseDatabase.getInstance().getReference("Users")
        binding.signUpBtn.setOnClickListener{
            val firstName = binding.firstNameInputField.text.toString().trim()
            val lastName = binding.lastNameInputField.text.toString().trim()
            val phoneNo = binding.phoneNoInputField.text.toString().trim()
            val setPass = binding.setPasswordInputField.text.toString()
            val confirmPass = binding.confirmPasswordInputField.text.toString()
            val emailId = binding.userAddressInputField.text.toString().trim()

            //checking if all the fields re filled
            if(firstName.isEmpty() || lastName.isEmpty() || phoneNo.isEmpty() || setPass.isEmpty() || confirmPass.isEmpty() || emailId.isEmpty()){
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Check set pass equals confirm pass
            if(setPass != confirmPass){
                Toast.makeText(this, "The password are not the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //to check if phone is not duplicated
            database.child(phoneNo).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){

                    //If the number enter is already registered
                    Toast.makeText(this, "Phone no already Exists", Toast.LENGTH_SHORT).show()
                }else{

                    //passing value in data class
                    val user = User(
                        firstName = firstName,
                        lastName = lastName,
                        phoneNo = phoneNo,
                        emailId = emailId,
                        password = confirmPass
                    )

                    //passing the values to database
                    database.child(phoneNo).setValue(user).addOnSuccessListener {
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                        clearAllFields()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
        //If user already has an acc
        binding.alreadyAnAccText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    //clear the field when registered successfully
    private fun clearAllFields() {
        binding.firstNameInputField.text.clear()
        binding.lastNameInputField.text.clear()
        binding.phoneNoInputField.text.clear()
        binding.setPasswordInputField.text.clear()
        binding.confirmPasswordInputField.text.clear()
        binding.userAddressInputField.text.clear()
    }
}