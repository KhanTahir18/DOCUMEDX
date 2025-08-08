package com.example.documedx.organization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.documedx.Organization
import com.example.documedx.databinding.EditProfileOrganizationActivityBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.documedx.databinding.PatientUnderOrganizationActivityBinding
import android.Manifest
import android.annotation.SuppressLint
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class OrganizationEditProfileActivity: AppCompatActivity() {
    private lateinit var binding: EditProfileOrganizationActivityBinding

    private val bucketId = "688b111000356abeadfb"
    private val projectId = "6888c59c00344d7b9867"
    private var publicUrl: String? = null
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileOrganizationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Organizations")
        val licence = intent.getStringExtra("licence").toString()
        database.child(licence).get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()){
                publicUrl = snapshot.child("ProfileURL").value?.toString()
            }
            if(publicUrl== null){
                //doNothing
            }else{
                Glide.with(this@OrganizationEditProfileActivity).load(publicUrl).into(binding.hospitalImageView)
            }
        }
        binding.saveBtn.setOnClickListener {
            val orgName = binding.hospitalNameInputField.text.toString()
            val address = binding.addressNameInputField.text.toString()
            val phoneNo = binding.phoneInputField.text.toString()
            val emailId = binding.emailNameInputField.text.toString().trim()
            val orgType = binding.orgTypeInputField.text.toString().trim()

            // If all fields are empty, show warning and stop
            if (orgName.isEmpty() && address.isEmpty() && phoneNo.isEmpty() && emailId.isEmpty() && orgType.isEmpty()) {
                Toast.makeText(this, "Please fill at least one field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show confirmation dialog
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Confirm Save")
            builder.setMessage("Are you sure you want to save these changes?")

            builder.setPositiveButton("Save") { _, _ ->
                // Only save and finish when user confirms
                val resultIntent = Intent()
                if (orgName.isNotEmpty()) resultIntent.putExtra("orgName", orgName)
                if (address.isNotEmpty()) resultIntent.putExtra("address", address)
                if (phoneNo.isNotEmpty()) resultIntent.putExtra("phoneNo", phoneNo)
                if (emailId.isNotEmpty()) resultIntent.putExtra("emailId", emailId)
                if (orgType.isNotEmpty()) resultIntent.putExtra("orgType", orgType)

                setResult(Activity.RESULT_OK, resultIntent)
                finish() // FINISH only after confirmation
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Do nothing, stay on the page
            }

            builder.create().show()
        }

        binding.cancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        requestPermissions()
        binding.uploadImgBtn.setOnClickListener {
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



                database.child(licence).get().addOnSuccessListener { snapshot ->
                    if(snapshot.exists()){
                        database.child(licence).child("ProfileURL").setValue(publicUrl)
                        Toast.makeText(this@OrganizationEditProfileActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@OrganizationEditProfileActivity, "licence not found", Toast.LENGTH_SHORT).show()
                    }
                }


                // Display Image
                Glide.with(this@OrganizationEditProfileActivity).load(publicUrl).into(binding.hospitalImageView)



                Toast.makeText(this@OrganizationEditProfileActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@OrganizationEditProfileActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}