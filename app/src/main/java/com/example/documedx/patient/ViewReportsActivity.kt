package com.example.documedx.patient

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.R

class ViewReportsActivity : AppCompatActivity() {

    private lateinit var uploadedReportsRecycler: RecyclerView
    private lateinit var sharedReportsRecycler: RecyclerView
    private lateinit var uploadedAdapter: ReportsAdapter
    private lateinit var sharedAdapter: ReportsAdapter

    private val uploadedReports = mutableListOf<Report>()
    private val sharedReports = mutableListOf<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reports)

        setupToolbar()
        initViews()
        loadReports()
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "View Reports"
        supportActionBar?.setBackgroundDrawable(getDrawable(android.R.color.transparent))
    }

    private fun initViews() {
        uploadedReportsRecycler = findViewById(R.id.recycler_uploaded_reports)
        sharedReportsRecycler = findViewById(R.id.recycler_shared_reports)

        setupUploadedReports()
        setupSharedReports()
    }

    private fun setupUploadedReports() {
        uploadedReportsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        uploadedAdapter = ReportsAdapter(uploadedReports,
            onReportClick = { report -> openPdf(report.filePath) },
            onShareClick = { report -> showQRDialog(report) }
        )
        uploadedReportsRecycler.adapter = uploadedAdapter
    }

    private fun setupSharedReports() {
        sharedReportsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sharedAdapter = ReportsAdapter(sharedReports,
            onReportClick = { report -> openPdf(report.filePath) },
            onShareClick = { report -> showQRDialog(report) }
        )
        sharedReportsRecycler.adapter = sharedAdapter
    }

    private fun loadReports() {
        // Load uploaded reports (from organizations)
        uploadedReports.clear()
        uploadedReports.addAll(getDummyUploadedReports())
        uploadedAdapter.notifyDataSetChanged()

        // Load shared reports (sent to doctors)
        sharedReports.clear()
        sharedReports.addAll(getDummySharedReports())
        sharedAdapter.notifyDataSetChanged()
    }

    private fun getDummyUploadedReports(): List<Report> {
        return listOf(
            Report("1", "Blood Test", "12:09", "dd/mm/yyyy", "blood_test.pdf", "BO-1"),
            Report("2", "X-Ray Report", "14:30", "dd/mm/yyyy", "xray.pdf", "XR-2")
        )
    }

    private fun getDummySharedReports(): List<Report> {
        return listOf(
            Report("3", "Blood Test", "21:34", "dd/mm/yyyy", "shared_blood.pdf", "BO-1", "Hospital name", true)
        )
    }

    private fun openPdf(filePath: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(filePath), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error - no PDF viewer found
        }
    }

    private fun showQRDialog(report: Report) {
        val qrFragment = QRShareFragment.newInstance(report.id, report.title)
        qrFragment.show(supportFragmentManager, "QRShareDialog")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}