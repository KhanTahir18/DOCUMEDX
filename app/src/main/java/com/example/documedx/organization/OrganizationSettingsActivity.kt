package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.IntroActivity
import com.example.documedx.databinding.ActivitySettingOrganizationBinding

class OrganizationSettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingOrganizationBinding
    private var licence: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        licence = intent.getStringExtra("licence")
        binding.logoutCardView.setOnClickListener {
            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}