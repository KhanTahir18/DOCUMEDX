package com.example.documedx.organization

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.OrganizationDashboardActivityBinding

class OrganizationDashboardActivity: AppCompatActivity() {
    private lateinit var binding: OrganizationDashboardActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrganizationDashboardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val licence = intent.getStringExtra("licence")
//        binding.viewMoreBtnStaff.setOnClickListener {
//            val intent = Intent(this, OrganizationDepartmentActivity::class.java)
//            intent.putExtra("licence", licence)
//            startActivity(intent)
//        }

        onBackPressedDispatcher.addCallback(this) {
            finishAffinity() // Or whatever you want to do on back press
        }
    }
}