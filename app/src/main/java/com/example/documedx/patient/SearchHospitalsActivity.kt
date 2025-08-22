package com.example.documedx.patient

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.R
import com.example.documedx.databinding.ActivitySearchHospitalBinding

class SearchHospitalsActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySearchHospitalBinding
    private lateinit var hospitalsAdapter: HospitalsAdapter
    private val allHospitals = mutableListOf<Hospital>()
    private val filteredHospitals = mutableListOf<Hospital>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHospitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchFunctionality()
        loadStaticHospitals()
    }

    private fun setupToolbar() {
        supportActionBar?.hide() // Hide default action bar since we're using custom header

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        hospitalsAdapter = HospitalsAdapter(filteredHospitals) { hospital ->
            onScheduleClick(hospital)
        }

        binding.recyclerHospitals.apply {
            layoutManager = LinearLayoutManager(this@SearchHospitalsActivity)
            adapter = hospitalsAdapter
        }
    }

    private fun setupSearchFunctionality() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterHospitals(s.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadStaticHospitals() {
        // Static hospital data as requested
        allHospitals.clear()
        allHospitals.addAll(
            listOf(
                Hospital(
                    id = "1",
                    name = "Millat",
                    type = "Govt",
                    phoneNumber = "9898765456",
                    email = "Patient@gmail.com",
                    imageRes = R.drawable.ic_hospital_placeholder // You can replace with actual hospital images
                ),
                Hospital(
                    id = "2",
                    name = "Millat",
                    type = "Govt",
                    phoneNumber = "9898765456",
                    email = "Patient@gmail.com",
                    imageRes = R.drawable.ic_hospital_placeholder // You can replace with actual hospital images
                )
            )
        )

        // Initially show all hospitals
        filterHospitals("")
    }

    private fun filterHospitals(query: String) {
        filteredHospitals.clear()

        if (query.isEmpty()) {
            // Show all hospitals when search is empty
            filteredHospitals.addAll(allHospitals)
        } else {
            // Filter hospitals by name (case insensitive)
            val filtered = allHospitals.filter { hospital ->
                hospital.name.lowercase().contains(query.lowercase())
            }
            filteredHospitals.addAll(filtered)
        }

        // Show/hide no results text
        if (filteredHospitals.isEmpty()) {
            binding.tvNoResults.visibility = View.VISIBLE
            binding.recyclerHospitals.visibility = View.GONE
        } else {
            binding.tvNoResults.visibility = View.GONE
            binding.recyclerHospitals.visibility = View.VISIBLE
        }

        hospitalsAdapter.updateList(filteredHospitals)
    }

    private fun onScheduleClick(hospital: Hospital) {
        // Show "Coming Soon" message as requested
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()

        // TODO: In the future, you can implement actual scheduling functionality here
        // For example:
        // val intent = Intent(this, ScheduleAppointmentActivity::class.java)
        // intent.putExtra("hospital_id", hospital.id)
        // intent.putExtra("hospital_name", hospital.name)
        // startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}