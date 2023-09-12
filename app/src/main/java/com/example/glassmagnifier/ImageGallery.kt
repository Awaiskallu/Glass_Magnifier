package com.example.glassmagnifier

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

@Suppress("DEPRECATION")
class ImageGalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var  backbutton: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gallery)

        recyclerView = findViewById(R.id.imageRecyclerView)
        backbutton = findViewById(R.id.back)
        imageAdapter = ImageAdapter(getImageFiles())
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@ImageGalleryActivity, 3)
            adapter = imageAdapter
        }

        backbutton.setOnClickListener {
            val intent = Intent(this, MagnifyingGlass::class.java)
            startActivity(intent)
        }

    }
    private fun getImageFiles(): List<File> {
        val outputDirectory = (application as MyApplication).getOutputDirectory()
        return outputDirectory.listFiles()?.filter { it.isFile } ?: emptyList()
    }
}
