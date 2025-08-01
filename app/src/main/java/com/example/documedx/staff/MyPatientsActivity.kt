package com.example.documedx.staff

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.Patient
import com.example.documedx.R

class MyPatientsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var patientsAdapter: PatientsAdapter
    private val patientsList = mutableListOf<Patient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_patients)

        setupToolbar()
        initViews()
        loadPatients()
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.my_patients)
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_patients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val empId = intent.getStringExtra("empId")
        patientsAdapter = PatientsAdapter(patientsList) { patient ->
            val intent = Intent(this, PatientDetailsActivity::class.java)
            intent.putExtra("empId", empId)
            intent.putExtra("patient_name", patient.name)
            startActivity(intent)
        }

        recyclerView.adapter = patientsAdapter
    }

    private fun loadPatients() {
        // TODO: Load from database/API
        // For now, using dummy data
        patientsList.clear()
        patientsList.addAll(getDummyPatients())
        patientsAdapter.notifyDataSetChanged()
    }

    private fun getDummyPatients(): List<Patient> {
        return listOf(
            Patient("1", "John Doe", 2),
            Patient("2", "Jane Smith", 1),
            Patient("3", "Mike Johnson", 0)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}