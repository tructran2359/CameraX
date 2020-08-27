package com.camerax.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import com.camerax.R
import com.camerax.presentation.base.BaseActivity
import com.camerax.utils.toast
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : BaseActivity() {

    val PERM_CAMERA = Manifest.permission.CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onStart() {
        super.onStart()
        checkCameraPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQ_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    toast("Camera permission denied")
                }
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            PERM_CAMERA.isGranted() -> {
                startCamera()
            }

            PERM_CAMERA.shouldExplainPermission() -> {
                toast("Explain Camera Permission")
                requestCameraPermission()
            }

            else -> {
                requestCameraPermission()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        val cameraExecutor = ContextCompat.getMainExecutor(this)
        val runnable = Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (ex: Exception) {
                Log.e("Camera", ex.toString())
            }
        }

        cameraProviderFuture.addListener(runnable, cameraExecutor)

    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(PERM_CAMERA),
            REQ_CAMERA
        )
    }
}