package com.example.glassmagnifier

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImageAdapter(private val imageFiles: List<File>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val imageNames = mutableListOf<String>()
    private val imageExtensions = mutableListOf<String>()

    init {
        for (file in imageFiles) {
            imageNames.add(file.nameWithoutExtension)
            imageExtensions.add(file.extension)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageFiles[position])
    }
    override fun getItemCount(): Int = imageFiles.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val nameLabel: TextView = itemView.findViewById(R.id.nameLabel)

        fun bind(imageFile: File) {
            val imageUri = Uri.fromFile(imageFile)
            imageView.setImageURI(imageUri)

            val position = adapterPosition
            val imageName = imageNames[position]
            val imageExtension = imageExtensions[position]
            val imageLabel = "$imageName.$imageExtension"
            nameLabel.text = imageLabel

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ImageDisplayActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                itemView.context.startActivity(intent)
            }
        }
    }

}
