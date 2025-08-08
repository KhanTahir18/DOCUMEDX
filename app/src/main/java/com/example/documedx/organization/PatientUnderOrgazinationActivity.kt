package com.example.documedx.organization

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.databinding.PatientUnderOrganizationActivityBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument


class PatientUnderOrgazinationActivity: AppCompatActivity() {
    private lateinit var binding: PatientUnderOrganizationActivityBinding
    private val bucketId = "688b111000356abeadfb"
    private val projectId = "6888c59c00344d7b9867"


    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PatientUnderOrganizationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val licence = intent.getStringExtra("licence") ?: ""


        requestPermissions()

        binding.uploadReportBtn.setOnClickListener {
            pickImageFromGallery()
        }


    }
    private fun requestPermissions() {
        //This is an array of all permissions your app may need.
        //You list them here so they can be requested at once.
        //These permission constants come from android.Manifest.permission.
        val permissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA
        )
        //ContextCompat.checkSelfPermission() is used to check if the permission is already granted.
        //PackageManager.PERMISSION_GRANTED means the user already allowed it.
        //The code filters out only those permissions which are not granted yet and stores them in the needed list.
        val needed = permissions.filter {

            // Condition That checks if the permission is not granted.
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED

            //gives same result as above
            //ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
        }

        //Checks if There are still permissions needed
        if (needed.isNotEmpty()) {
            //Not our function but a in-built android fuction that see the permissions list
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 101)
        }
    }

    private val imagePickerLauncher =

        //Allows the user to Open a Gallery and select an image
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { //uri is a Uri type var which can be umm uri?.let only runs if the image is not null

                val imageFile = createTempFileFromUri(it)
                imageFile?.let { selectedImage ->
                    val pdfFile = convertImageToPdf(selectedImage)
                    if (pdfFile != null) {
                        uploadImageToAppwrite(pdfFile)
                    } else {
                        Toast.makeText(this, "Failed to convert image to PDF", Toast.LENGTH_SHORT).show()
                    }
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

    private fun convertImageToPdf(imageFile: File): File? {
        return try {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            // Save the PDF in cache directory
            val pdfFile = File(cacheDir, UUID.randomUUID().toString() + ".pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)

            pdfDocument.close()
            outputStream.close()

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun openPdfInViewer(pdfUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No app found to open PDF", Toast.LENGTH_SHORT).show()
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
        val licence = intent.getStringExtra("licence") ?: ""
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
                    openPdfInViewer(publicUrl)

                    database = FirebaseDatabase.getInstance().getReference("Organizations")
                    database.child(licence).get().addOnSuccessListener { snapshot ->
                        if(snapshot.exists()){
                            database.child(licence).child("reportUrl").push().setValue(publicUrl)
                            Toast.makeText(this@PatientUnderOrgazinationActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@PatientUnderOrgazinationActivity, "licence not found", Toast.LENGTH_SHORT).show()
                        }
                    }


                // Display Image
                //Glide.with(this@MainActivity).load(publicUrl).into(binding.imageViewArea)


                Toast.makeText(this@PatientUnderOrgazinationActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@PatientUnderOrgazinationActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
