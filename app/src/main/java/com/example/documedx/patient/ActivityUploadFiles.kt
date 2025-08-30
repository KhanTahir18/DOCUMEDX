package com.example.documedx.patient

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.documedx.OrganizationReport
import com.example.documedx.databinding.ActivityPatientsUploadReportsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ActivityUploadFiles: AppCompatActivity() {
    private lateinit var binding: ActivityPatientsUploadReportsBinding
//    private var licence: String? = null
    private var patientPhoneNo: String? = null
    private val bucketId = "688b111000356abeadfb"
    private val projectId = "6888c59c00344d7b9867"


    private lateinit var patientDb: DatabaseReference

    private val pdfPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val file = createTempFileFromUri(it, "pdf")
                file?.let { selectedFile ->
                    uploadPdfToAppwrite(selectedFile)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientsUploadReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientPhoneNo = intent.getStringExtra("phoneNo")

        patientDb = FirebaseDatabase.getInstance().getReference("Users").child(patientPhoneNo!!)
        Toast.makeText(this, "$patientPhoneNo", Toast.LENGTH_SHORT).show()
        requestPermissions()

        binding.uploadReportIV.setOnClickListener {
            val reportIdId = binding.reportId.text.toString().trim()
            if (reportIdId.isEmpty()) {
                Toast.makeText(this, "Please enter a report ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            pickPdfFromDevice()
        }

    }
    private fun pickPdfFromDevice() {
        pdfPickerLauncher.launch("application/pdf")
    }
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA
        )
        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED

        }

        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 101)
        }
    }

    private fun createTempFileFromUri(uri: Uri, extension: String): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, UUID.randomUUID().toString() + ".$extension")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadPdfToAppwrite(file: File) {
        val client = Client(this)
            .setEndpoint("https://fra.cloud.appwrite.io/v1") // Appwrite endpoint
            .setProject(projectId) // replace with your projectId

        val storage = Storage(client)
        val inputFile = InputFile.fromFile(file)

        lifecycleScope.launch {
            try {
                val result = storage.createFile(
                    bucketId = bucketId, // replace with your bucketId
                    fileId = ID.unique(),
                    file = inputFile,
                    permissions = listOf(
                        Permission.read(Role.any()) // make it public
                    )
                )

                val fileId = result.id
                val publicUrl =
                    "https://fra.cloud.appwrite.io/v1/storage/buckets/$bucketId/files/$fileId/view?project=$projectId"

                // Save in Firebase under patient
                if (patientPhoneNo != null) {
                    val reportId = binding.reportId.text.toString().trim()
                    val from = null
                    val to = patientPhoneNo.toString()
                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                    val report = OrganizationReport(
                        id = reportId,
                        fromOrg = from,
                        toPatient = to,
                        dateWhenSharred = currentDate,
                        timeWhenShared = currentTime,
                        reportUrl = publicUrl
                    )

                    patientDb.child("Reports").child("Uploaded Reports").child(reportId).setValue(report)
                    binding.reportId.text.clear()
                    Toast.makeText(
                        this@ActivityUploadFiles,
                        "Uploaded & URL saved to Firebase",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ActivityUploadFiles,
                    "Upload failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}