package com.example.documedx.staff

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.documedx.R
import com.example.documedx.databinding.ActivityStaffDashboardBinding
import com.example.documedx.databinding.ActivityStaffEditProfileBinding
import com.example.documedx.patient.EditProfileActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar

class StaffDashboardActivity : AppCompatActivity() {

    private lateinit var profileCard: CardView
    private lateinit var patientsCard: CardView
    private lateinit var database: DatabaseReference
    private var empId: String? = null
    private  var associatedHospital: String? = null
    private var publicUrl: String? = null

    private lateinit var binding: ActivityStaffDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        empId = intent.getStringExtra("empId").toString()
        associatedHospital = intent.getStringExtra("associatedHospital").toString()
        Toast.makeText(this,"${empId}, ${associatedHospital}", Toast.LENGTH_SHORT).show()

        database = FirebaseDatabase.getInstance().getReference("Organizations").child(associatedHospital!!).child("Staffs").child(empId!!)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadDataToProfileCard()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StaffDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.editProImgBtn.setOnClickListener {
            val intent = Intent(this, EditStaffProfileActivity::class.java)
            intent.putExtra("empId", empId)
            intent.putExtra("associatedHospital", associatedHospital)
            startActivityForResult(intent,102)
        }

        binding.tvViewMorePatients.setOnClickListener {
            val intent = Intent(this, MyPatientsActivity::class.java)
            intent.putExtra("empId", empId)
            intent.putExtra("associatedHospital", associatedHospital)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this) {
            finishAffinity() // Or whatever you want to do on back press
        }
    }
    private fun loadDataToProfileCard() {
        database.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val firstName = dataSnapshot.child("firstName").value.toString()
                    val lastName = dataSnapshot.child("lastName").value.toString()
                    val phoneNo = dataSnapshot.child("phoneNo").value.toString()
                    val emailId = dataSnapshot.child("emailId").value.toString()
                    val sex = dataSnapshot.child("sex").value.toString()
                    val dateOfBirth = dataSnapshot.child("dateOfBirth").value.toString()
                    val employeeId = dataSnapshot.child("employeeId").value.toString()
                    val address = dataSnapshot.child("address").value.toString()
                    publicUrl = dataSnapshot.child("ProfileURL").value?.toString()

                    val age = calculateAge(dateOfBirth)

                    binding.NameTextView.text = "$firstName $lastName"
                    binding.genderAndAgeTextView.text = "$sex/$age"
                    binding.AddressTextDynamic.text = address
                    binding.phoneTextDynamic.text = phoneNo
                    binding.emailTextDynamic.text = emailId
                    binding.empIdTextDynamic.text = employeeId

                    if(publicUrl== null){
                        //doNothing
                    }else{
                        Glide.with(this@StaffDashboardActivity).load(publicUrl).into(binding.profileImageView)
                    }

                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            val firstName = data.getStringExtra("firstName")
            val lastName = data.getStringExtra("lastName")
            val address = data.getStringExtra("address")
            val phoneNo = data.getStringExtra("phoneNo")
            val emailId = data.getStringExtra("emailId")
            val gender = data.getStringExtra("gender")
            val dob: String? = data.getStringExtra("dob")
            if (firstName?.isNotEmpty() ?: false) {
                database.child("firstName").setValue(firstName)
            }
            if (lastName?.isNotEmpty() ?: false) {
                database.child("lastName").setValue(lastName)
            }
            if (address?.isNotEmpty() ?: false) {
                database.child("address").setValue(address)
            }
            if (phoneNo?.isNotEmpty() ?: false) {
                database.child("phoneNo").setValue(phoneNo)
            }
            if (emailId?.isNotEmpty() ?: false) {
                database.child("emailId").setValue(emailId)
            }
            if (gender?.isNotEmpty() ?: false) {
                database.child("sex").setValue(gender)
            }
            // Only update DOB if at least one field is filled
            if(dob?.isNotEmpty() ?: false){
                database.child("dateOfBirth").setValue(dob).toString()
            }
        }
    }

    private fun calculateAge(dob: String): Int {
        try {
            val parts = dob.split("/") // [dd, MM, yyyy]
            val day = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Calendar months are 0-based
            val year = parts[2].toInt()

            val dobCalendar = Calendar.getInstance()
            dobCalendar.set(year, month, day)

            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

            // If birth date is greater than today's date (month/day), reduce age by 1
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            return age
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }
}