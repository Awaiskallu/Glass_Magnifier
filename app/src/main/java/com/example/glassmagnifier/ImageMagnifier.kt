package com.example.glassmagnifier

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class ImageMagnifierActivity : AppCompatActivity() {

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var magnificationFactor: TextView
    private lateinit var loupeRadius: TextView
    private lateinit var magnificationSeekBar: SeekBar
    private lateinit var radiusSeekBar: SeekBar
    private lateinit var previewView: PreviewView
    private lateinit var imageView: LoupeView
    private lateinit var imagepicker: ImageView
    private lateinit var gallerypicker: ImageView



    private var isImageDisplayed = false



    private lateinit var capturedImageView: ImageView
    private lateinit var drawLineButton: ImageView
    private lateinit var clearAllButton: ImageView
    private lateinit var backbutton: ImageView

    private var isDrawingEnabled = false

    @SuppressLint("MissingInflatedId", "WrongViewCast", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagemagnifier)

        magnificationFactor = findViewById(R.id.magnificientFactorValueText)
        loupeRadius = findViewById(R.id.loupeRadiusValueText)
        magnificationSeekBar = findViewById(R.id.sbFactorBar)
        radiusSeekBar = findViewById(R.id.sbRadiusBar)
        previewView = findViewById(R.id.view_findera)
        imageView = findViewById(R.id.image_viewab)
        drawLineButton = findViewById(R.id.drawline)
        clearAllButton = findViewById(R.id.cleanall)
        backbutton = findViewById(R.id.backbutton)
        imagepicker = findViewById(R.id.camerapicker)
        gallerypicker = findViewById(R.id.gallerypicker)





//        showImageButton = findViewById(R.id.view_findera)
        clearAllButton = findViewById(R.id.cleanall)
//        displayedImageView = findViewById(R.id.captured_image_view)




        val button: View = findViewById(R.id.icselectimage)
        val parentView: View = findViewById(android.R.id.content)
        button.setOnClickListener {
            showCustomMenuPopup(parentView)
        }

        drawLineButton.setOnClickListener {
            isDrawingEnabled = !isDrawingEnabled
            if (isDrawingEnabled) {
                "Drawing: ON"
            } else {
                "Drawing: OFF"
            }
        }

        clearAllButton.setOnClickListener {
            if (isImageDisplayed) {
                showToast("pressed")
            } else {
                showToast("pjdhdg")
            }
        }

        imagepicker.setOnClickListener {
            val intent = Intent(this, ImagePickerA::class.java)
            startActivity(intent)
        }

        gallerypicker.setOnClickListener {
            openGalleryForImage()
        }
        backbutton.setOnClickListener {
            super.onBackPressed()
        }


    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }
    private fun showCustomMenuPopup(anchorView: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.custom_layout_menu, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f

        popupView.findViewById<TextView>(R.id.takephoto15).setOnClickListener {
            popupWindow.dismiss()

            val intent = Intent(this, ImagePickerA::class.java)
            startActivity(intent)
        }

        popupView.findViewById<TextView>(R.id.choosephoto1).setOnClickListener {
            popupWindow.dismiss()

            openGalleryForImage()
        }

        popupView.setOnClickListener { popupWindow.dismiss() }

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (exc: Exception) {
                Log.e(ImageMagnifierActivity.TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            capturedImageView.setImageURI(selectedImageUri)
        }
//        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
//            val selectedImageUri = data?.data
//            selectedImageUri?.let {
//                val intent = Intent(this, ImagePreviewActivity::class.java)
//                intent.putExtra(ImagePreviewActivity.EXTRA_IMAGE_URI, selectedImageUri.toString())
//                startActivity(intent)
//            }
//        }

    }

    companion object {
        private const val TAG = "ImageMagnifierActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_CHOOSE_PHOTO = 20
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        const val REQUEST_CODE_GALLERY = 101
    }
}