package com.example.documedx.staff

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.documedx.databinding.ActivityStaffEditProfileBinding
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
import java.util.UUID

class EditStaffProfileActivity: AppCompatActivity() {
    private lateinit var binding: ActivityStaffEditProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var spinner: Spinner
    private var empId: String? = null
    private var associatedHospital: String? = null
    private val bucketId = "688b111000356abeadfb"
    private val projectId = "6888c59c00344d7b9867"
    private var publicUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        empId = intent.getStringExtra("empId").toString()
        associatedHospital = intent.getStringExtra("associatedHospital").toString()
        Toast.makeText(this,"${empId}, ${associatedHospital}", Toast.LENGTH_SHORT).show()
        database = FirebaseDatabase.getInstance().getReference("Organizations").child(associatedHospital!!).child("Staffs").child(empId!!)
        database.get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()){
                publicUrl = snapshot.child("ProfileURL").value?.toString()
            }
            if(publicUrl== null){
                //doNothing
            }else{
               Glide.with(this@EditStaffProfileActivity).load(publicUrl).into(binding.hospitalImageView)
            }

            binding.firstNameInputField.setText(snapshot.child("firstName").value?.toString() ?: "")
            binding.lastNameInputField.setText(snapshot.child("lastName").value?.toString() ?: "")
            binding.phoneInputField.setText(snapshot.child("phoneNo").value?.toString() ?: "")
            binding.emailNameInputField.setText(snapshot.child("emailId").value?.toString() ?: "")
            binding.addressNameInputField.setText(snapshot.child("address").value?.toString() ?: "")

        }
        spinner = binding.genderSpinner
        val listItems = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.saveBtn.setOnClickListener {
            val firstName = binding.firstNameInputField.text.toString().trim()
            val lastName = binding.lastNameInputField.text.toString().trim()
            val phoneNo = binding.phoneInputField.text.toString().trim()
            val emailId = binding.emailNameInputField.text.toString().trim()
            val address = binding.addressNameInputField.text.toString()
            val gender = binding.genderSpinner.selectedItem.toString()
            val day = binding.dayDOBNameInputField.text.toString().trim()
            val month = binding.monthDOBNameInputField.text.toString().trim()
            val year = binding.yearDOBNameInputField.text.toString().trim()



            // If all fields are empty, show warning and stop
            if (gender.isEmpty() && firstName.isEmpty() && lastName.isEmpty() && phoneNo.isEmpty() && emailId.isEmpty() && address.isEmpty()) {
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
                if (firstName.isNotEmpty()) resultIntent.putExtra("firstName", firstName)
                if (lastName.isNotEmpty()) resultIntent.putExtra("lastName", lastName)
                if (address.isNotEmpty()) resultIntent.putExtra("address", address)
                if (phoneNo.isNotEmpty()) resultIntent.putExtra("phoneNo", phoneNo)
                if (emailId.isNotEmpty()) resultIntent.putExtra("emailId", emailId)
                if (gender.isNotEmpty()) resultIntent.putExtra("gender", gender)
                if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
                    resultIntent.putExtra("dob", "${day}/${month}/${year}")
                }
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
        binding.hospitalImageView.setOnClickListener {
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



                database.get().addOnSuccessListener { snapshot ->
                    if(snapshot.exists()){
                        database.child("ProfileURL").setValue(publicUrl)
                        Toast.makeText(this@EditStaffProfileActivity, "Uploaded & URL saved to Firebase", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@EditStaffProfileActivity, "licence not found", Toast.LENGTH_SHORT).show()
                    }
                }


                // Display Image
                Glide.with(this@EditStaffProfileActivity).load(publicUrl).into(binding.hospitalImageView)



            } catch (e: Exception) {
                Toast.makeText(this@EditStaffProfileActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}