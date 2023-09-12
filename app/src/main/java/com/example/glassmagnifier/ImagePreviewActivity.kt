package com.example.glassmagnifier

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.glassmagnifier.databinding.ActivityImagemagnifierBinding
import java.io.File
@Suppress("DEPRECATION")
class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagemagnifierBinding
    private lateinit var loupeView: LoupeView

    private var magnificationFactor = 1.0f
    private var loupeRadius = 50 // Default loupe radius value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagemagnifierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUriString = intent.getStringExtra("photoUri")
        if (photoUriString != null) {
            val photoUri = Uri.parse(photoUriString)
            binding.showimage.setImageURI(photoUri)
        }

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        imageUri?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .into(binding.showimage)
        }

        loupeView = binding.imageViewab

        binding.sbFactorBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                magnificationFactor = (progress / 20f) + 1
                binding.magnificientFactorValueText.text = "$progress%"
                updateLoupeViewSettings()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.sbRadiusBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                loupeRadius = progress
                binding.loupeRadiusValueText.text = progress.toString()
                updateLoupeViewSettings()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })




        // Continue with button functionality as before
        binding.cleanall.setOnClickListener {
            deleteDisplayedImage()
        }

        binding.camerapicker.setOnClickListener {
            val intent = Intent(this, ImagePickerA::class.java)
            startActivity(intent)
        }

        binding.gallerypicker.setOnClickListener {
            openGalleryForImage()
        }

        binding.backbutton.setOnClickListener {
            super.onBackPressed()
        }
        // ... other button click listeners
    }

    private fun updateLoupeViewSettings() {
        loupeView.setMagnificationFactor(magnificationFactor)
        loupeView.setLoupeRadius(loupeRadius)
    }

    private fun deleteDisplayedImage() {
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        imageUri?.let {
            val imageFile = File(Uri.parse(it).path)
            if (imageFile.exists()) {
                imageFile.delete()
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)))
            }
        }
        val intent = Intent(this, ImageMagnifierActivity::class.java)
        startActivity(intent)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ImageMagnifierActivity.REQUEST_CODE_GALLERY)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val REQUEST_CODE_GALLERY = 101
    }
}
