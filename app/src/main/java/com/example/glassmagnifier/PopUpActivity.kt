package com.example.glassmagnifier

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity

class PopUpActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        val showMenuButton: View = findViewById(R.id.showMenuButton)
        val parentView: View = findViewById(android.R.id.content)

        showMenuButton.setOnClickListener {
            showCustomMenuPopup(parentView)
        }
    }

    private fun showCustomMenuPopup(anchorView: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.custom_layout_menu, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.elevation = 10f
        popupView.setOnClickListener { popupWindow.dismiss() }
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }
}