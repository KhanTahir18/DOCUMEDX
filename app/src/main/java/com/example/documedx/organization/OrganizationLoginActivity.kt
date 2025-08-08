package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.documedx.R
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

        database = FirebaseDatabase.getInstance().getReference("Organizations")

        //Click listener
        binding.LoginBtn.setOnClickListener {
            val licence = binding.licenceInputField.text.toString()
            val password = binding.setPassNameInputField.text.toString()

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
                        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("licence", licence)
                        }

                        val intent = Intent(this, OrganizationDashboardActivity::class.java)
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
        binding.licenceInputField.text.clear()
        binding.setPassNameInputField.text.clear()
    }
}