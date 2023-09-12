package com.example.glassmagnifier

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.glassmagnifier.databinding.ActivityPermissionBinding
import com.permissionx.guolindev.PermissionX

class PermissionActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 123
    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.permissionbutn.setOnClickListener {
                checkAndRequestPermissions { granted ->
                    if (granted) {
                        startMainActivity()
                    }
                }
            }

            binding.notallow.setOnClickListener {
                finish()
            }
        } else {
            // Handle permission logic for versions prior to Android 6.0 (M)
            startMainActivity()
        }

    }

    private fun checkAndRequestPermissions(onPermissionStatus: ((Boolean) -> Unit)? = null) {
        val permissions = mutableListOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        PermissionX.init(this)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Please Allow necessary permissions for the app!",
                    "OK",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    onPermissionStatus?.invoke(true)
                } else {
                    showToast("Permission Denied!")
                    onPermissionStatus?.invoke(false)
                }
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MagnifyingGlass::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showToast("Permissions granted")
                startMainActivity()
            } else {
                showToast("Permissions denied")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
