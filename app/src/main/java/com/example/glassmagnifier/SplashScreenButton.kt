package com.example.glassmagnifier


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.glassmagnifier.databinding.ActivitySplashButtonBinding
import com.example.glassmagnifier.databinding.ImagepickerBinding

@Suppress("DEPRECATION")
class SplashScreenButton : AppCompatActivity() {

    private lateinit var binding: ActivitySplashButtonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashButtonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.getstarted.setOnClickListener {

            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}