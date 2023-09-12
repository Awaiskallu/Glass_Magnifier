package com.example.glassmagnifier

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.example.glassmagnifier.databinding.ActivityCameraXBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraControl: CameraControl
    private lateinit var binding: ActivityCameraXBinding // Declare binding variable
    private val REQUEST_CODE_PERMISSIONS = 123

    private var currentCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


    private var isTorchOn: Boolean = false
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    var zoomLevel:Float=0f
    var exposureLevel:Int=0
    var minExposureValue:Int=0
    var maxExposureValue:Int=0
    private val ANIMATION_SLOW_MILLIS = 100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()


        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.flashLight.setOnClickListener {
            toggleTorch()
        }


        binding.takephotobtn.setOnClickListener {
            takePhoto()
        }
        binding.changecamerabtn.setOnClickListener {

            val newCameraSelector =
                if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else
                    CameraSelector.DEFAULT_BACK_CAMERA

            currentCameraSelector = newCameraSelector

            startCamera(newCameraSelector)
        }





        binding.pausebutton.setOnClickListener {
            onPauseButtonClick()
        }


        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        val brightnessSeekBar = findViewById<SeekBar>(R.id.brightnessSeekBar)


        val zoomSeekBar = findViewById<SeekBar>(R.id.zoomSeekBar)

        zoomSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Check if cameraControl is initialized before using it
                if (fromUser) {
                    val zoomRatio = progress / 100f // Convert progress to ratio
                    cameraControl.setLinearZoom(zoomRatio)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        brightnessSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                exposureLevel = minExposureValue + progress
                binding.brightnessValueText.text = exposureLevel.toString()
                binding.brightnessLabel.text = exposureLevel.toString()
                updateCameraExposure()


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

    }
    private fun updateCameraExposure() {
        cameraControl?.setExposureCompensationIndex(exposureLevel)
    }

    private fun onPauseButtonClick() {
        pauseCamera()
        // Do any additional tasks you need when the camera is paused
    }


    private fun pauseCamera() {
        cameraProvider?.let { provider ->
            camera?.let {
                provider.unbind()
                camera = null
            }
        }
    }


    override fun onPause() {
        super.onPause()
        pauseCamera()
    }


    private fun toggleTorch() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val camera = cameraProvider.bindToLifecycle(this, cameraSelector)

            val cameraControl = camera.cameraControl
            val cameraInfo = camera.cameraInfo

            isTorchOn = !isTorchOn

            try {
                if (cameraInfo.hasFlashUnit()) {
                    cameraControl.enableTorch(isTorchOn)
                } else {
                    Toast.makeText(this, "Flashlight not available on this device", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Torch control failed: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }




    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(savedUri.toFile().extension)

                    val intent = Intent(this@CameraActivity, ImagePreviewActivity::class.java)
                    intent.putExtra("photoUri", savedUri.toString())
                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error capturing photo: ${exception.message}", exception)
                    Toast.makeText(this@CameraActivity, "Error capturing photo", Toast.LENGTH_SHORT).show()
                }
            })
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
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun startCamera(cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val viewFinder = findViewById<PreviewView>(R.id.view_finder)

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraControl = camera.cameraControl
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(

            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXExample"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val ANIMATION_FAST_MILLIS = 50L

        fun createFile(baseFolder: File, name: String) =
            File(
                baseFolder,
                name
            )
    }
}
