package com.koaladev.storryapp.ui.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.storryapp.databinding.ActivityAddStoryBinding
import com.koaladev.storryapp.ui.getImageUri
import com.koaladev.storryapp.ui.viewmodel.AddStoryViewModel
import com.koaladev.storryapp.ui.viewmodel.ViewModelFactory
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.util.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var token: String? = null
    private lateinit var toolbar: MaterialToolbar

    // Permission handler
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) showToast("Camera permission denied")
        }

    // Gallery handler
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val selectedImg = result.data?.data
        if (result.resultCode == RESULT_OK && selectedImg != null) {
            startCrop(selectedImg)
        } else {
            showToast("No image selected.")
        }
    }

    // Camera handler
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.currentImageUri.value?.let { uri ->
                viewModel.setCurrentImageUri(uri)
                showImage(uri)
            }
        } else {
            clearPreview()
            showToast("Image capture failed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        checkPermissions()
        setupObservers()
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Permission check
    private fun checkPermissions() {
        if (!isCameraPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Setup Observers
    private fun setupObservers() {
        viewModel.currentImageUri.observe(this) { uri ->
            uri?.let { showImage(it) }
        }
        viewModel.getSession().observe(this) { session ->
            token = session.token
        }
    }

    // Setup Click Listeners
    private fun setupListeners() {
        binding.apply {
            galleryButton.setOnClickListener { openGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadStory() }
        }
    }

    // Open Gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        launcherIntentGallery.launch(Intent.createChooser(intent, "Choose a Picture"))
    }

    // Start Camera
    private fun startCamera() {
        val imageUri = getImageUri(this)
        viewModel.setCurrentImageUri(imageUri)
        launcherIntentCamera.launch(imageUri)
    }

    // Start Crop
    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(16f, 9f)
            .withMaxResultSize(1080, 720)
            .start(this)
    }

    // Handle Crop Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            croppedUri?.let { uri ->
                viewModel.setCurrentImageUri(uri)
                showImage(uri)
            }
        } else if (requestCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    // Show Image
    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
    }

    // Upload Story
    private fun uploadStory() {
        val description = binding.descriptionEditText.text.toString()
        if (description.isBlank()) {
            showToast("Description cannot be empty.")
            return
        }

        val imageUri = viewModel.currentImageUri.value ?: run {
            showToast("Please select an image.")
            return
        }

        val imageFile = getImageFile(imageUri)
        val compressedFile = compressImageIfNeeded(imageFile)

        token?.let {
            val imagePart = MultipartBody.Part.createFormData(
                "photo", compressedFile.name, compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.uploadNewStory(it, imagePart, descriptionPart, null)
            observeUploadStatus()
        } ?: showToast("Token is null.")
    }

    // Observe Upload Status
    private fun observeUploadStatus() {
        viewModel.uploadStatus.observe(this) { status ->
            if (status.isSuccess) {
                showToast("Upload successful")
                clearPreview()
                navigateToMain()
            } else {
                showToast("Upload failed: ${status.exceptionOrNull()?.message}")
            }
        }
    }

    // Utilities
    private fun getImageFile(uri: Uri): File {
        return File(uri.path!!).takeIf { it.exists() }
            ?: copyUriToFile(uri, File(cacheDir, "output_image.jpg"))
    }

    private fun copyUriToFile(contentUri: Uri, destination: File): File {
        contentResolver.openInputStream(contentUri)?.use { inputStream ->
            FileOutputStream(destination).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return destination
    }

    private fun compressImageIfNeeded(file: File, maxSizeInBytes: Int = 1_048_576): File {
        if (file.length() <= maxSizeInBytes) return file

        val originalBitmap = BitmapFactory.decodeFile(file.path)
        val outputStream = ByteArrayOutputStream()
        var quality = 100

        do {
            outputStream.reset()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5
        } while (outputStream.size() > maxSizeInBytes && quality > 0)

        val compressedFile = File(cacheDir, "compressed_image.jpg")
        FileOutputStream(compressedFile).use { fos -> fos.write(outputStream.toByteArray()) }
        return compressedFile
    }

    private fun clearPreview() {
        binding.previewImageView.setImageDrawable(null)
        viewModel.setCurrentImageUri(null)
    }

    private fun navigateToMain() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
