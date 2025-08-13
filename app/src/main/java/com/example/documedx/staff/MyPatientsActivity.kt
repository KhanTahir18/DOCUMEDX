package com.example.documedx.staff

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.Patient
import com.example.documedx.R
import com.example.documedx.databinding.ActivityMyPatientsBinding
import com.example.documedx.organization.DepartmentAdapter

class MyPatientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPatientsBinding
    private var empId: String? = null
    private  var associatedHospital: String? = null
    private lateinit var adapter: DepartmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        empId = intent.getStringExtra("empId").toString()
        associatedHospital = intent.getStringExtra("associatedHospital").toString()

    }
}