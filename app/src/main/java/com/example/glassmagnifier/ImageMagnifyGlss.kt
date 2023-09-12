package com.example.glassmagnifier

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.glassmagnifier.databinding.ActivityImagemagnifierBinding
import com.google.android.gms.cast.framework.media.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*



@Suppress("DEPRECATION")
class ImageMagnifier : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityImagemagnifierBinding
    private var imageUri: Uri? = null
    private var photo: Bitmap? = null
    private var alteredBitmap: Bitmap? = null
//
//    private var nativeAdHelper:NativeAdHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityImagemagnifierBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.viewFindera.setOnClickListener(this)
        b.camerapicker.setOnClickListener(this)
        b.gallerypicker.setOnClickListener(this)
        b.drawline.setOnClickListener(this)
        b.cleanall.setOnClickListener(this)
        b.backbutton.setOnClickListener(this)

//        showNativeAd()

        b.sbFactorBar.setOnSeekBarChangeListener(mOnFactorChangeListener)
        b.sbFactorBar.progress = 30
        b.sbRadiusBar.setOnSeekBarChangeListener(mOnRadiusChangeListener)
        b.sbRadiusBar.progress = 100

        registerCameraLauncher()
        registerGalleryLauncher()
    }

    private var galleryLauncher : ActivityResultLauncher<Intent>?=null
    private fun registerGalleryLauncher() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode== RESULT_OK){
                clearImageView()
                val uri = result.data?.data
                uri?.let {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                        val afterBitmap = resizeImage(bitmap)

                        b.viewFindera.visibility = View.GONE
                        b.imageViewab.visibility = View.VISIBLE
                        alteredBitmap = Bitmap.createBitmap(
                            afterBitmap.width,
                            afterBitmap.height,
                            afterBitmap.config
                        )

                        b.imageViewab
                            .setNewImage(alteredBitmap, afterBitmap)

                        showToast("Touch image for zoom")
//                        showInterstitial()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun saveImageToStorage(b : Bitmap, imageName : String) {
        val imageOutStream: OutputStream?
        val imagesDir = getOutputDirectory()
        val image = File(imagesDir, "$imageName.jpg")
        image.setLastModified(System.currentTimeMillis())
        val uri = getFileUri(image)
        imageOutStream = FileOutputStream(image)
        try {
            b.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
        } finally {
            imageOutStream.close()
            CoroutineScope(Dispatchers.Main).launch{
                showToast("Image Saved!")
                uri?.path?.let {
                    val f = File(it)
                    scanMedia(f.absolutePath)
                }
            }
        }
    }



    private val mOnFactorChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val factor = (progress/20)+1
            b.imageViewab.setMFactor(factor)
            val factorText = "${progress/20}x"
            b.magnificientFactorValueText.text = factorText
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val mOnRadiusChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            b.imageViewab.setRadius(progress)
            b.loupeRadiusValueText.text = progress.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

//    private fun imageSelectionDialog() {
//        val imagePicker = ImagePicker(
//            this@ImageMagnifier,
//            object : ImagePicker.ImagePickerClicks {
//                override fun openCamera() {
//                    cameraOpen()
//                }
//
//                override fun openGallery() {
//                    galleryOpen()
//                }
//            }
//        )
//        imagePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        imagePicker.show()
//    }

    override fun onClick(view: View) {
        if(b.imageViewab.isUserInteracting()){
            return
        }
        when (view.id) {
            R.id.camerapicker -> {
                b.camerapicker.delayViewClickable()
                cameraOpen()
            }
            R.id.gallerypicker -> {
                b.gallerypicker.delayViewClickable()
                galleryOpen()
            }
            R.id.view_findera -> {
                b.viewFindera.delayViewClickable()
//                imageSelectionDialog()
            }

            R.id.cleanall -> {
                clearImageView()
            }
            R.id.drawline -> {
                if(!b.imageViewab.isImageSelected()){
                    showToast("Please select an image!")
                    return
                }
                if (b.imageViewab.isDraw()) {
                    b.imageViewab.isDraw(false)
                    b.drawline.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ImageMagnifier,
                            R.drawable.ic_draw_image
                        )
                    )
                } else {
                    b.imageViewab.isDraw(true)
                    b.drawline.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ImageMagnifier,
                            R.drawable.ic_gall_svg
                        )
                    )
                }
            }
            R.id.backbutton->{
                super.onBackPressed()
            }
        }
    }

//    private fun enterNameDialog(bitmap: Bitmap) {
//        val dialog = Dialog(this)
//        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
////        dialog.setContentView(R.layout.save_image_dialog_layout)
//        dialog.window!!.setLayout(
//            getWindowWidth(0.8f),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
////        dialog.window!!.setBackgroundDrawable(
////            ResourcesCompat.getDrawable(resources,R.drawable.rateus_dialog_bg,theme)
////        )
////        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
////        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
////        val etName = dialog.findViewById<EditText>(R.id.etName)
//        btnCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//        btnSave.setOnClickListener {
//            val name = etName.text.toString()
//            if(isNameValid(name)){
//                dialog.dismiss()
//                CoroutineScope(Dispatchers.Default).launch {
//                    saveImageToStorage(bitmap, name)
//                }
//            }else{
//                showToast("Please enter a valid name!")
//            }
//        }
//        dialog.setCancelable(false)
//        dialog.show()
//        etName.requestFocus()
//    }

    private fun isNameValid(name: String): Boolean {
        var res = true
        if(name.trim().isEmpty() || containsSpecialCharacters(name)){
            res = false
        }
        return res
    }

    private fun containsSpecialCharacters(name: String): Boolean {
        var res = false
        val specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}"
        for (i in name.indices) {
            val ch: Char = name[i]
            if (specialCharactersString.contains(ch.toString())) {
                res = true
                break
            } else if (i == name.length - 1) {
                res = false
            }
        }
        return res
    }

    private fun clearImageView() {
        b.imageViewab.clean()
        try{
            b.imageViewab.setNewImage(null,null)
        }catch (e:Exception){
        }
        b.viewFindera.visibility = View.VISIBLE
//        b.ivSave.visibility = View.GONE
        if (b.imageViewab.isDraw()) {
            b.imageViewab.isDraw(false)
            b.drawline.setImageDrawable(
                ContextCompat.getDrawable(
                    this@ImageMagnifier,
                    R.drawable.ic_draw_image
                )
            )
        }
    }

    private fun galleryOpen() {
        checkAndRequestPermissions { granted->
            if(granted){
                val intentGallery = Intent()
                intentGallery.type = "image/*"
                intentGallery.action = Intent.ACTION_GET_CONTENT
                galleryLauncher?.launch(intentGallery)
            }
        }
    }

    private fun createImageFile(): File {
        return CameraActivity.createFile(
            getOutputDirectory(),
            getString(R.string.app_name) + " ${System.currentTimeMillis()}"
        )
    }

    private var takePictureLauncher:ActivityResultLauncher<Uri>?=null
    private fun registerCameraLauncher(){
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){ success->
            if(success){
                imageUri?.let {
                    try{
                        clearImageView()
                        photo = MediaStore.Images.Media.getBitmap(contentResolver, it)

                        val afterBitmap = resizeImage(photo!!)

                        b.viewFindera.visibility = View.GONE
                        b.imageViewab.visibility = View.VISIBLE

                        alteredBitmap = Bitmap.createBitmap(
                            afterBitmap.width,
                            afterBitmap.height,
                            afterBitmap.config
                        )

                        b.imageViewab.setNewImage(alteredBitmap, afterBitmap)

                        showToast("Touch image for zoom")
//                        showInterstitial()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun cameraOpen() {
        checkAndRequestPermissions { granted->
            if(granted){
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: Exception) {
                    null
                }
                photoFile?.let {
                    imageUri = getFileUri(it)
                    imageUri?.let { uri->
                        takePictureLauncher?.launch(uri)
                    }
                }
            }
        }
    }

    private fun resizeImage(image: Bitmap): Bitmap {
        val width = image.width
        val height = image.height

        val scaleWidth = width * 0.9
        val scaleHeight = height * 0.9

        if (image.byteCount <= 1000000)
            return image

        return Bitmap.createScaledBitmap(image, scaleWidth.toInt(), scaleHeight.toInt(), false)
    }

}
