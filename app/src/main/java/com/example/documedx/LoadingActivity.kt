package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityLoadingBinding
import com.example.documedx.organization.OrganizationDashboardActivity
import com.example.documedx.patient.PatientDashboardActivity
import com.example.documedx.staff.StaffDashboardActivity

class LoadingActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            val role = sharedPref.getString("role", null)
            val phoneNo = sharedPref.getString("phoneNo", null)
            val licence = sharedPref.getString("licence", null)
            val empId = sharedPref.getString("empId", null)
            val assosiatedHospital = sharedPref.getString("associatedHospital", null)

            if (role != null) {
                when (role) {
                    "patient" -> {
                        Toast.makeText(this, "Patient Dashboard $phoneNo", Toast.LENGTH_SHORT).show()
                        intent = Intent(this, PatientDashboardActivity::class.java)
                        intent.putExtra("phoneNo", phoneNo)
                        startActivity(intent)
                    }

                    "staff" -> {
                        Toast.makeText(this, "Id:${empId} Licence:${assosiatedHospital}", Toast.LENGTH_SHORT).show()
                        intent = Intent(this, StaffDashboardActivity::class.java)
                        intent.putExtra("empId", empId)
                        intent.putExtra("associatedHospital", assosiatedHospital)
                        startActivity(intent)
                    }

                    "organization" -> {
                        Toast.makeText(this, "Organization Dashboard $licence", Toast.LENGTH_SHORT).show()
                        intent = Intent(this, OrganizationDashboardActivity::class.java)
                        intent.putExtra("licence", licence)
                        startActivity(intent)
                    }
                }
                finish()
            } else {
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1600)
    }
}