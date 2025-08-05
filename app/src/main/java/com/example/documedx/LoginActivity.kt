package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityPatientLoginBinding
import com.example.documedx.databinding.LoginPageStaffActivityBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.edit

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPatientLoginBinding
    //creating database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toggle fuction for set pass
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
        database = FirebaseDatabase.getInstance().getReference("Users")

        //adding on click listener on login btn
        binding.loginBtn.setOnClickListener {
            val phoneNo = binding.phoneInputField.text.toString().trim()
            val password = binding.setPassNameInputField.text.toString()

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
                        // Shared pref
                        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("phoneNo", phoneNo)}

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("phoneNo", phoneNo)
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

        //if user want to create a new acc
        binding.createAccText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun clearAllFields() {
        binding.phoneInputField.text.clear()
        binding.setPassNameInputField.text.clear()
    }
}