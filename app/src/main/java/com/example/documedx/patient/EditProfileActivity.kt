package com.example.documedx.patient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText

    private lateinit var lastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etGender: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etMedicalHistory: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private var phoneNo:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        phoneNo = intent.getStringExtra("phoneNo")
//        setupToolbar()
        initViews()
//        loadCurrentData()
//        setupClickListeners()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val last = lastName.text.toString().trim()
            val age = etAge.text.toString().trim()
            val gender = etGender.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val medicalHistory = etMedicalHistory.text.toString().trim()

            // If all fields are empty, show warning and stop
            if (name.isEmpty() && last.isEmpty() && age.isEmpty() && gender.isEmpty() && phone.isEmpty() && email.isEmpty() && address.isEmpty() && medicalHistory.isEmpty()) {
                Toast.makeText(this, "Please fill at least one field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show confirmation dialog
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Confirm Save")
            builder.setMessage("Are you sure you want to save these changes?")

            builder.setPositiveButton("Save") { _, _ ->
                // Only save and finish when user confirms
                val resultIntent = Intent()
                if (name.isNotEmpty()) resultIntent.putExtra("Patientname", name)
                if (last.isNotEmpty()) resultIntent.putExtra("Patientlastname", last)
                if (address.isNotEmpty()) resultIntent.putExtra("address", address)
                if (phone.isNotEmpty()) resultIntent.putExtra("phoneNo", phone)
                if (email.isNotEmpty()) resultIntent.putExtra("emailId", email)
                if (medicalHistory.isNotEmpty()) resultIntent.putExtra("medicalHistory", medicalHistory)
                if (age.isNotEmpty()) resultIntent.putExtra("age", age)
                if (gender.isNotEmpty()) resultIntent.putExtra("gender", gender)

                setResult(Activity.RESULT_OK, resultIntent)
                finish() // FINISH only after confirmation
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Do nothing, stay on the page
            }

            builder.create().show()
        }
        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        lastName = findViewById(R.id.et_last_name)
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