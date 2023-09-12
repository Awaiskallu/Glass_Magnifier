package com.example.glassmagnifier

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.glassmagnifier.databinding.ActivityMagnifyglassBinding

class MagnifyingGlass  : AppCompatActivity() {

    private lateinit var binding: ActivityMagnifyglassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMagnifyglassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button: View = findViewById(R.id.dotButton)
        val parentView: View = findViewById(android.R.id.content)
        button.setOnClickListener {
            showCustomMenuPopup(parentView)
        }
        binding.livemagnifier.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        binding.imagemagnifier.setOnClickListener {
            val intent = Intent(this, ImageMagnifierActivity::class.java)
            startActivity(intent)
        }
        binding.savefiles.setOnClickListener {
            val intent = Intent(this, ImageGalleryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showCustomMenuPopup(anchorView: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.menu_item, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.elevation = 10f
        popupView.setOnClickListener { popupWindow.dismiss() }

        popupWindow.showAtLocation(anchorView, Gravity.TOP, 400, 200)
    }
}