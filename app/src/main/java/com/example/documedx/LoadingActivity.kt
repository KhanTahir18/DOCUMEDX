package com.example.documedx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.ActivityLoadingBinding

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

            if (role != null) {
                when (role) {
                    "patient" -> {
                        Toast.makeText(this, "Patient Dashboard $phoneNo", Toast.LENGTH_SHORT).show()
                        intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("phoneNo", phoneNo)
                        startActivity(intent)
                    }

                    "staff" -> {
                        Toast.makeText(this, "Staff Dashboard", Toast.LENGTH_SHORT).show()
                    }

                    "organization" -> {
                        Toast.makeText(this, "Organization Dashboard", Toast.LENGTH_SHORT).show()
                    }
                }
                finish()
            } else {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1600)
    }
}