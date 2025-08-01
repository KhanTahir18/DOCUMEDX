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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.MedicalReport
import com.example.documedx.utilis.PdfGenerator
import com.example.documedx.R
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Role
import io.appwrite.Permission
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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

    private val bucketId = "688b111000356abeadfb"
    private val projectId = "6888c59c00344d7b9867"
    private lateinit var database: DatabaseReference

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
            //checkCameraPermissionAndCapture()
            pickImageFromGallery()
        }
    }

    private val imagePickerLauncher =

        //Allows the user to Open a Gallery and select an image
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { //uri is a Uri type var which can be umm uri?.let only runs if the image is not null

                val file = createTempFileFromUri(it)
                file?.let { selectedFile ->//file?.let only runs if the image is not null
                    uploadImageToAppwrite(selectedFile)
                }
            }
        }

    //Pick and image from gallery
    private fun pickImageFromGallery() {
        //says to only allow images to be selected
        imagePickerLauncher.launch("image/*")
    }

    // 🔹 Convert URI to File
    private fun createTempFileFromUri(uri: Uri): File? {
        return try {

            //This lets you read the content of the image (or any file) from the user's device.
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            //This line creates a temporary file with a unique name like: f47ac10b-58cc-4372-a567-0e02b2c3d479
            val file = File(cacheDir, UUID.randomUUID().toString() + ".jpg")

            //Open an output stream that lets you write data into the temporary file.
            val outputStream = FileOutputStream(file)

            //This line copies the content from the input stream (image from the URI) into the output stream (your new file).
            //If the inputStream is not null, the image gets copied.
            inputStream?.copyTo(outputStream)

            //closes the stream
            inputStream?.close()
            outputStream.close()

            //return the file
            file

            //if any error occurs null will be passed
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImageToAppwrite(file: File) {
        val client = Client(this)
            .setEndpoint("https://fra.cloud.appwrite.io/v1") // Appwrite Cloud endpoint
            .setProject(projectId)

        // Initializes the Storage service from Appwrite SDK using the client.
        //This is the object you'll use to interact with Appwrite Storage API (like uploading or downloading files).
        val storage = Storage(client)

        //Converts your local File object into InputFile, which is Appwrite's expected format for uploads.
        val inputFile = InputFile.fromFile(file)
        val empId = intent.getStringExtra("empId")
        lifecycleScope.launch {
            try {
                val result = storage.createFile(
                    bucketId = bucketId,     // Replace with your bucket ID
                    fileId = ID.unique(),
                    file = inputFile,
                    permissions = listOf(
                        Permission.read(Role.any()) // Public read access
                    )
                )

                val fileId = result.id
                val publicUrl =
                    "https://fra.cloud.appwrite.io/v1/storage/buckets/$bucketId/files/$fileId/view?project=$projectId"

                // Save to Firebase


                database = FirebaseDatabase.getInstance().getReference("Staffs")
                database.child(empId.toString()).get().addOnSuccessListener { snapshot ->
                    if(snapshot.exists()){
                        database.child(empId.toString()).child("reportUrl").push().setValue(publicUrl)
                        Toast.makeText(this@PatientDetailsActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@PatientDetailsActivity, "licence not found", Toast.LENGTH_SHORT).show()
                    }
                }


                // Display Image
                //Glide.with(this@MainActivity).load(publicUrl).into(binding.imageViewArea)


                Toast.makeText(this@PatientDetailsActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@PatientDetailsActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
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