package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.SignUpPageActiviyBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import kotlin.toString
import android.text.InputType
import android.widget.EditText
import androidx.core.content.edit
import com.example.documedx.patient.PatientDashboardActivity

class SignUpActivity: AppCompatActivity() {
    private lateinit var binding: SignUpPageActiviyBinding
    private lateinit var database: DatabaseReference
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpPageActiviyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toggle fuction for set pass
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


        //Gender spinner code
        spinner = binding.genderSpinner
        val listItems = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        //access the database
        database = FirebaseDatabase.getInstance().getReference("Users")
        binding.signUpBtn.setOnClickListener {
            val firstName = binding.firstNameInputField.text.toString().trim()
            val lastName = binding.lastNameInputField.text.toString().trim()
            val dOB = "${
                binding.dayDOBNameInputField.text.toString().trim()
            }/${
                binding.monthDOBNameInputField.text.toString().trim()
            }/${binding.yearDOBNameInputField.text.toString().trim()}"
            val gender = binding.genderSpinner.selectedItem.toString()
            val phoneNo = binding.phoneInputField.text.toString().trim()
            val setPass = binding.setPassNameInputField.text.toString()
            val confirmPass = binding.conPassNameInputField.text.toString()
            val emailId = binding.emailNameInputField.text.toString().trim()
            val address = binding.addressNameInputField.text.toString()
            val medHistory = binding.medHistInputField.text.toString()


            if (firstName.isEmpty() || lastName.isEmpty() || dOB.isEmpty() || gender.isEmpty() || phoneNo.isEmpty() || setPass.isEmpty() || confirmPass.isEmpty() || emailId.isEmpty() || address.isEmpty() || medHistory.isEmpty()) {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Check set pass equals confirm pass
            if (setPass != confirmPass) {
                Toast.makeText(this, "The password are not the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //to check if phone is not duplicated
            database.child(phoneNo).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {

                    //If the number enter is already registered
                    Toast.makeText(this, "Phone no already Exists", Toast.LENGTH_SHORT).show()
                } else {

                    //passing value in data class
                    val user = User(
                        firstName = firstName,
                        lastName = lastName,
                        gender = gender,
                        dOB = dOB,
                        phoneNo = phoneNo,
                        emailId = emailId,
                        address = address,
                        medicalHistory = medHistory,
                        password = confirmPass
                    )

                    //passing the values to database
                    database.child(phoneNo).setValue(user).addOnSuccessListener {
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()

                        // Shared pref
                        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                        sharedPref.edit {
                            putString("phoneNo", phoneNo)}

                        val intent = Intent(this, PatientDashboardActivity::class.java)
                        intent.putExtra("phoneNo", phoneNo)
                        startActivity(intent)
                        clearAllFields()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.alreadyAnAccText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    //clear the field when registered successfully
    private fun clearAllFields() {
        binding.firstNameInputField.text.clear()
        binding.lastNameInputField.text.clear()
        binding.phoneInputField.text.clear()
        binding.setPassNameInputField.text.clear()
        binding.conPassNameInputField.text.clear()
        binding.emailNameInputField.text.clear()
        binding.dayDOBNameInputField.text.clear()
        binding.monthDOBNameInputField.text.clear()
        binding.yearDOBNameInputField.text.clear()
        binding.addressNameInputField.text.clear()
        binding.medHistInputField.text.clear()
    }
}


