package com.example.documedx.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.Organization
import com.example.documedx.databinding.ActivitySearchHospitalBinding

class SearchHospitalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchHospitalBinding
    private lateinit var hospitalsAdapter: HospitalsAdapter
    private val hospitalList = mutableListOf<Organization>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Add some dummy hospitals (later replace with Firebase data)
        hospitalList.add(
            Organization(
                organizationName = "Apollo Hospital",
                address = "Mumbai",
                phoneNo = "9876543210",
                emailId = "apollo@gmail.com",
                organizationType = "Private"
            )
        )
        hospitalList.add(
            Organization(
                organizationName = "AIIMS",
                address = "Delhi",
                phoneNo = "9123456780",
                emailId = "aiims@gmail.com",
                organizationType = "Government"
            )
        )

        // 2. Setup adapter
        hospitalsAdapter = HospitalsAdapter(hospitalList)

        // 3. Setup RecyclerView
        binding.recyclerHospitals.layoutManager = LinearLayoutManager(this)
        binding.recyclerHospitals.adapter = hospitalsAdapter
    }
}
