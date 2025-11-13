package com.example.flameedgeapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    companion object {
        init {
            System.loadLibrary("c++_shared")
            System.loadLibrary("opencv_java4")
            System.loadLibrary("native-lib")
        }
        private const val CAMERA_PERMISSION = 100
    }

    private lateinit var cameraHelper: Camera2Helper
    private lateinit var glView: GLView
    private lateinit var tvFps: TextView
    private var lastTime = System.currentTimeMillis()
    private var frameCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glView)
        tvFps = findViewById(R.id.tvFps)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
        } else {
            initCamera()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted → start camera
                initCamera()
            } else {
                android.util.Log.e("MainActivity", "Camera permission denied")
            }
        }
    }


    private fun initCamera() {
        cameraHelper = Camera2Helper(this)
        cameraHelper.setOnFrameAvailableListener { yuv, w, h ->
            nativeProcessFrame(yuv, w, h)
            glView.requestRender()
            frameCount++
            val now = System.currentTimeMillis()
            if (now - lastTime >= 1000) {
                val fps = frameCount
                runOnUiThread {
                    tvFps.text = "FPS: $fps"
                }
                frameCount = 0
                lastTime = now
            }
        }

        glView.setNativeTextureProvider { texId ->
            nativeFillTexture(texId)
        }

        cameraHelper.startCamera()
    }

    external fun nativeProcessFrame(yuv: ByteArray, width: Int, height: Int)
    external fun nativeFillTexture(textureId: Int)
}
