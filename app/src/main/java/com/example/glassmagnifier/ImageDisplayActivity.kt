package com.example.glassmagnifier

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import java.io.File

@Suppress("DEPRECATION")
class ImageDisplayActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var deleteButton: ImageView
    private lateinit var imageFile: File
    private lateinit var backbuttona: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        imageView = findViewById(R.id.imageView)
        deleteButton = findViewById(R.id.ivDeleteImg)
        backbuttona = findViewById(R.id.icbackc)

        val imageUriString = intent.getStringExtra("imageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            imageFile = File(imageUri.path)
            imageView.setImageURI(imageUri)

            val imageName = imageFile.name
            val imageExtension = imageFile.extension
            val imageLabel = "$imageName.$imageExtension"
            val nameLabel = findViewById<TextView>(R.id.nameLabel)
            nameLabel.text = imageLabel
        }

        deleteButton.setOnClickListener {
            deleteImageAndNavigateBack()
        }

        backbuttona.setOnClickListener{
            super.onBackPressed()
        }
    }
    private fun deleteImageAndNavigateBack() {
        val imageUriString = intent.getStringExtra("imageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            val file = File(imageUri.path)
            if (file.exists()) {
                file.delete()
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            }
        }
        navigateBackToGallery() // Navigate back and refresh the gallery
    }

    private fun navigateBackToGallery() {
        val intent = Intent(this, ImageGalleryActivity::class.java)
        startActivity(intent)
        finish()
    }
}
