package com.camerax.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.camerax.R
import com.camerax.presentation.base.BaseActivity
import com.camerax.utils.toast
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File

class CameraActivity : BaseActivity() {

    val PERM_CAMERA = Manifest.permission.CAMERA
    private var mImageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView.setOnClickListener {
            takePhoto()
        }
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

            // Preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Image Capture use case
            mImageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture)
            } catch (ex: Exception) {
                Log.e("Camera", ex.toString())
            }
        }

        cameraProviderFuture.addListener(runnable, cameraExecutor)

    }

    private fun takePhoto() {
        val imageCapture = mImageCapture ?: return

        val photoFile = File(
            filesDir,
            "TestImage.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val callback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(photoFile)
                val message = "Captured: $uri"
                toast(message)
                Log.i("Capture", message)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Capture", exception.toString())
                exception.printStackTrace()
            }
        }

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), callback)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(PERM_CAMERA),
            REQ_CAMERA
        )
    }
}