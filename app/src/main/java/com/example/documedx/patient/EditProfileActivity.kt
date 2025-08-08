package com.example.documedx.patient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etGender: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etMedicalHistory: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setupToolbar()
        initViews()
        loadCurrentData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etAge = findViewById(R.id.et_age)
        etGender = findViewById(R.id.et_gender)
        etPhone = findViewById(R.id.et_phone)
        etEmail = findViewById(R.id.et_email)
        etAddress = findViewById(R.id.et_address)
        etMedicalHistory = findViewById(R.id.et_medical_history)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)
    }

    private fun loadCurrentData() {
        // TODO: Load from database/SharedPreferences
        // For now, leave fields empty for user to fill
        etName.setText("")
        etAge.setText("")
        etGender.setText("")
        etPhone.setText("")
        etEmail.setText("")
        etAddress.setText("")
        etMedicalHistory.setText("")
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveProfile()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val gender = etGender.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val medicalHistory = etMedicalHistory.text.toString().trim()

        if (validateInputs(name, age, phone, email)) {
            // TODO: Save to database/SharedPreferences
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun validateInputs(name: String, age: String, phone: String, email: String): Boolean {
        if (name.isEmpty()) {
            etName.error = "Name is required"
            return false
        }

        if (age.isEmpty()) {
            etAge.error = "Age is required"
            return false
        }

        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}