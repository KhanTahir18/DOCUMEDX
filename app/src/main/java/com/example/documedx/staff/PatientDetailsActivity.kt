package com.example.documedx.staff

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.MedicalReport
import com.example.documedx.utilis.PdfGenerator
import com.example.documedx.R
import java.text.SimpleDateFormat
import java.util.*

class PatientDetailsActivity : AppCompatActivity() {

    private lateinit var patientNameTV: TextView
    private lateinit var reportsRecyclerView: RecyclerView
    private lateinit var addPrescriptionBtn: Button
    private lateinit var noReportsTV: TextView

    private lateinit var reportsAdapter: ReportsAdapter
    private val reportsList = mutableListOf<MedicalReport>()

    private var patientId: String = ""
    private var patientName: String = ""

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val CAMERA_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)

        getIntentData()
        setupToolbar()
        initViews()
        loadPatientReports()
    }

    private fun getIntentData() {
        patientId = intent.getStringExtra("patient_id") ?: ""
        patientName = intent.getStringExtra("patient_name") ?: ""
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.patient_details)
    }

    private fun initViews() {
        patientNameTV = findViewById(R.id.tv_patient_name)
        reportsRecyclerView = findViewById(R.id.recycler_reports)
        addPrescriptionBtn = findViewById(R.id.btn_add_prescription)
        noReportsTV = findViewById(R.id.tv_no_reports)

        patientNameTV.text = patientName

        reportsRecyclerView.layoutManager = LinearLayoutManager(this)
        reportsAdapter = ReportsAdapter(reportsList) { report ->
            // Open PDF viewer
            openPdfViewer(report.filePath)
        }
        reportsRecyclerView.adapter = reportsAdapter

        addPrescriptionBtn.setOnClickListener {
            checkCameraPermissionAndCapture()
        }
    }

    private fun loadPatientReports() {
        // TODO: Load from database/API
        reportsList.clear()
        reportsList.addAll(getDummyReports())

        if (reportsList.isEmpty()) {
            showNoReports()
        } else {
            showReports()
        }

        reportsAdapter.notifyDataSetChanged()
    }

    private fun getDummyReports(): List<MedicalReport> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return listOf(
            MedicalReport("1", "Blood Test Report", dateFormat.format(Date()), "blood_test.pdf"),
            MedicalReport("2", "X-Ray Report", dateFormat.format(Date(System.currentTimeMillis() - 86400000)), "xray_report.pdf")
        )
    }

    private fun showNoReports() {
        noReportsTV.visibility = android.view.View.VISIBLE
        reportsRecyclerView.visibility = android.view.View.GONE
        addPrescriptionBtn.visibility = android.view.View.GONE
    }

    private fun showReports() {
        noReportsTV.visibility = android.view.View.GONE
        reportsRecyclerView.visibility = android.view.View.VISIBLE
        addPrescriptionBtn.visibility = android.view.View.VISIBLE
    }

    private fun checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, getString(R.string.camera_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                convertImageToPdfAndSend(it)
            }
        }
    }

    private fun convertImageToPdfAndSend(bitmap: Bitmap) {
        try {
            val pdfPath = PdfGenerator.createPdfFromBitmap(
                this,
                bitmap,
                "prescription_${System.currentTimeMillis()}"
            )

            if (pdfPath != null) {
                // TODO: Send PDF to patient via API/database
                Toast.makeText(this, getString(R.string.prescription_sent_successfully), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.error_creating_pdf), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_processing_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPdfViewer(filePath: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(filePath), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.no_pdf_viewer_found), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, getString(R.string.camera_permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}