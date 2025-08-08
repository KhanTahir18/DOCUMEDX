package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.documedx.databinding.OrganizationDashboardActivityBinding
import com.example.documedx.organization.OrganizationDashboardActivity.Companion.ADD_DEPARTMENT_REQUEST
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values
import com.google.firebase.database.*

class OrganizationDashboardActivity: AppCompatActivity() {
    private lateinit var binding: OrganizationDashboardActivityBinding
    private lateinit var database: DatabaseReference
    private var licence: String? = null
    private var publicUrl: String? = null
    companion object {
        const val ADD_DEPARTMENT_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrganizationDashboardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        licence = intent.getStringExtra("licence")

        if (licence == null) {
            Toast.makeText(this, "Licence is missing", Toast.LENGTH_SHORT).show()
            finish() // close the activity to avoid further crashes
            return
        }



        //loading data to profile card
        database = FirebaseDatabase.getInstance().getReference("Organizations").child(licence!!)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadDataToProfileCard(licence)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrganizationDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


        binding.editProImgBtn.setOnClickListener {
            val intent = Intent(this, OrganizationEditProfileActivity::class.java)
            intent.putExtra("licence", licence)
            startActivityForResult(intent, 101)
        }
//        binding.viewMoreBtnStaff.setOnClickListener {
//            val intent = Intent(this, OrganizationDepartmentActivity::class.java)
//            intent.putExtra("licence", licence)
//            startActivity(intent)
//        }

        binding.viewDeptBtn.setOnClickListener {
            val intent = Intent(this, OrganizationDepartmentActivity::class.java)
            intent.putExtra("licence", licence)
            startActivity(intent)
        }

        binding.addDeptBtn.setOnClickListener {
            val intent = Intent(this, AddDepartmentActivity::class.java)
            intent.putExtra("licence", licence)
            startActivityForResult(intent, ADD_DEPARTMENT_REQUEST)
        }

        onBackPressedDispatcher.addCallback(this) {
            finishAffinity() // Or whatever you want to do on back press
        }
    }
    private fun loadDataToProfileCard(licence: String?) {
        database.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val hospitalName = dataSnapshot.child("organizationName").value.toString()
                    val organizationType = dataSnapshot.child("organizationType").value.toString()
                    val orgAddress = dataSnapshot.child("address").value.toString()
                    val orgPhone = dataSnapshot.child("phoneNo").value.toString()
                    val licenceValue = dataSnapshot.child("licence").value.toString()
                    val emailId = dataSnapshot.child("emailId").value.toString()
                    publicUrl = dataSnapshot.child("ProfileURL").value?.toString()

                    binding.orgNameTextView.text = hospitalName
                    binding.orgTypeTextView.text = organizationType
                    binding.orgAddressTextDynamic.text = orgAddress
                    binding.orgPhoneTextDynamic.text = orgPhone
                    binding.licenceTextDynamic.text = licenceValue
                    binding.emailTextDynamic.text = emailId

                    if(publicUrl== null){
                        //doNothing
                    }else{
                        Glide.with(this@OrganizationDashboardActivity).load(publicUrl).into(binding.hospitalImageView)
                    }
                    Toast.makeText(this, "Public$publicUrl", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            val orgName = data.getStringExtra("orgName")
            val address = data.getStringExtra("address")
            val phoneNo = data.getStringExtra("phoneNo")
            val emailId = data.getStringExtra("emailId")
            val orgType = data.getStringExtra("orgType")
            if (orgName?.isNotEmpty() ?: false) {
                database.child("organizationName").setValue(orgName)
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
            if (orgType?.isNotEmpty() ?: false) {
                database.child("organizationType").setValue(orgType)
            }
        }
    }
}