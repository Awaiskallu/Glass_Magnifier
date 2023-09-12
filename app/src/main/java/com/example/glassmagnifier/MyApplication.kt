package com.example.glassmagnifier

import android.app.Application
import java.io.File

class MyApplication : Application() {

    private lateinit var outputDirectory: File

    override fun onCreate() {
        super.onCreate()
        outputDirectory = getOutputDirectory()
    }
    fun getOutputDirectory(): File {
        if (!this::outputDirectory.isInitialized) {
            val mediaDir = externalMediaDirs.firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            outputDirectory = mediaDir ?: filesDir
        }
        return outputDirectory
    }
}
