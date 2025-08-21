package com.example.documedx.patient

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.R

class ViewReportsActivity : AppCompatActivity() {

    // ADDED: RecyclerView for received reports from organizations
    private lateinit var receivedReportsRecycler: RecyclerView
    private lateinit var uploadedReportsRecycler: RecyclerView
    private lateinit var sharedReportsRecycler: RecyclerView

    // ADDED: Adapter for received reports
    private lateinit var receivedAdapter: ReportsAdapter
    private lateinit var uploadedAdapter: ReportsAdapter
    private lateinit var sharedAdapter: ReportsAdapter

    // ADDED: List for received reports from organizations
    private val receivedReports = mutableListOf<Report>()
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
        // ADDED: Received section - Initialize received reports RecyclerView for organization reports
        receivedReportsRecycler = findViewById(R.id.recycler_received_reports)
        uploadedReportsRecycler = findViewById(R.id.recycler_uploaded_reports)
        sharedReportsRecycler = findViewById(R.id.recycler_shared_reports)

        // ADDED: Received section - Setup received reports section
        setupReceivedReports()
        setupUploadedReports()
        setupSharedReports()
    }

    // ADDED: Received section - Setup received reports from organizations
    private fun setupReceivedReports() {
        receivedReportsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        receivedAdapter = ReportsAdapter(receivedReports,
            onReportClick = { report -> openPdf(report.filePath) },
            onShareClick = { report -> showQRDialog(report) }
        )
        receivedReportsRecycler.adapter = receivedAdapter
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
        // ADDED: Load received reports from organizations
        receivedReports.clear()
        receivedReports.addAll(getDummyReceivedReports())
        receivedAdapter.notifyDataSetChanged()

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

    // ADDED: Dummy data for received reports from organizations
    private fun getDummyReceivedReports(): List<Report> {
        return listOf(
            Report("1", "Lab Results", "09:15", "dd/mm/yyyy", "lab_results.pdf", "LR-001", "City Hospital", false),
            Report("2", "X-Ray Scan", "14:22", "dd/mm/yyyy", "xray_scan.pdf", "XR-002", "Medical Center", false),
            Report("3", "Blood Work", "11:45", "dd/mm/yyyy", "blood_work.pdf", "BW-003", "Diagnostic Lab", false)
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
//package com.example.documedx.patient
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.documedx.R
//
//class ViewReportsActivity : AppCompatActivity() {
//
//    // ADDED: RecyclerView for received reports from organizations
//    private lateinit var receivedReportsRecycler: RecyclerView
//    private lateinit var uploadedReportsRecycler: RecyclerView
//    private lateinit var sharedReportsRecycler: RecyclerView
//
//    // ADDED: Adapter for received reports
//    private lateinit var receivedAdapter: ReportsAdapter
//    private lateinit var uploadedAdapter: ReportsAdapter
//    private lateinit var sharedAdapter: ReportsAdapter
//
//    // ADDED: List for received reports from organizations
//    private val receivedReports = mutableListOf<Report>()
//    private val uploadedReports = mutableListOf<Report>()
//    private val sharedReports = mutableListOf<Report>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_view_reports)
//
//        setupToolbar()
//        initViews()
//        loadReports()
//    }
//
//    private fun setupToolbar() {
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = "View Reports"
//        supportActionBar?.setBackgroundDrawable(getDrawable(android.R.color.transparent))
//    }
//
//    private fun initViews() {
//        // ADDED: Received section - Initialize received reports RecyclerView for organization reports
//        receivedReportsRecycler = findViewById(R.id.recycler_received_reports)
//        uploadedReportsRecycler = findViewById(R.id.recycler_uploaded_reports)
//        sharedReportsRecycler = findViewById(R.id.recycler_shared_reports)
//
//        // ADDED: Received section - Setup received reports section
//        setupReceivedReports()
//        setupUploadedReports()
//        setupSharedReports()
//    }
//
//    // ADDED: Received section - Setup received reports from organizations
//    private fun setupReceivedReports() {
//        receivedReportsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        receivedAdapter = ReportsAdapter(receivedReports,
//            onReportClick = { report -> openPdf(report.filePath) },
//            onShareClick = { report -> showQRDialog(report) }
//        )
//        receivedReportsRecycler.adapter = receivedAdapter
//    }
//
//    private fun setupUploadedReports() {
//        uploadedReportsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        uploadedAdapter = ReportsAdapter(uploadedReports,
//            onReportClick = { report -> openPdf(report.filePath) },
//            onShareClick = { report -> showQRDialog(report) }
//        )
//        uploadedReportsRecycler.adapter = uploadedAdapter
//    }
//
//    private fun setupSharedReports() {
//        sharedReportsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        sharedAdapter = ReportsAdapter(sharedReports,
//            onReportClick = { report -> openPdf(report.filePath) },
//            onShareClick = { report -> showQRDialog(report) }
//        )
//        sharedReportsRecycler.adapter = sharedAdapter
//    }
//
//    private fun loadReports() {
//        // ADDED: Load received reports from organizations
//        receivedReports.clear()
//        receivedReports.addAll(getDummyReceivedReports())
//        receivedAdapter.notifyDataSetChanged()
//
//        // Load uploaded reports (from organizations)
//        uploadedReports.clear()
//        uploadedReports.addAll(getDummyUploadedReports())
//        uploadedAdapter.notifyDataSetChanged()
//
//        // Load shared reports (sent to doctors)
//        sharedReports.clear()
//        sharedReports.addAll(getDummySharedReports())
//        sharedAdapter.notifyDataSetChanged()
//    }
//
//    private fun getDummyUploadedReports(): List<Report> {
//        return listOf(
//            Report("1", "Blood Test", "12:09", "dd/mm/yyyy", "blood_test.pdf", "BO-1"),
//            Report("2", "X-Ray Report", "14:30", "dd/mm/yyyy", "xray.pdf", "XR-2")
//        )
//    }
//
//    // ADDED: Dummy data for received reports from organizations
//    private fun getDummyReceivedReports(): List<Report> {
//        return listOf(
//            Report("1", "Lab Results", "09:15", "dd/mm/yyyy", "lab_results.pdf", "LR-001", "City Hospital", false),
//            Report("2", "X-Ray Scan", "14:22", "dd/mm/yyyy", "xray_scan.pdf", "XR-002", "Medical Center", false),
//            Report("3", "Blood Work", "11:45", "dd/mm/yyyy", "blood_work.pdf", "BW-003", "Diagnostic Lab", false)
//        )
//    }
//
//    private fun getDummySharedReports(): List<Report> {
//        return listOf(
//            Report("3", "Blood Test", "21:34", "dd/mm/yyyy", "shared_blood.pdf", "BO-1", "Hospital name", true)
//        )
//    }
//
//    private fun openPdf(filePath: String) {
//        try {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(Uri.parse(filePath), "application/pdf")
//            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//            startActivity(intent)
//        } catch (e: Exception) {
//            // Handle error - no PDF viewer found
//        }
//    }
//
//    private fun showQRDialog(report: Report) {
//        val qrFragment = QRShareFragment.newInstance(report.id, report.title)
//        qrFragment.show(supportFragmentManager, "QRShareDialog")
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
//}