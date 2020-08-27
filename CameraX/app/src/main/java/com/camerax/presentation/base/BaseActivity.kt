package com.camerax.presentation.base

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val REQ_CAMERA = 1
    }

    protected fun String.isGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this@BaseActivity, this) == PackageManager.PERMISSION_GRANTED
    }

    protected fun String.shouldExplainPermission(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(this@BaseActivity, this)
    }
}