package com.example.flameedgeapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log

class Camera2Helper(private val context: Context) {

    private val TAG = "Camera2Helper"

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null

    private var frameListener: ((ByteArray, Int, Int) -> Unit)? = null

    private val backgroundThread = HandlerThread("CameraBackground").apply { start() }
    private val backgroundHandler = Handler(backgroundThread.looper)

    fun setOnFrameAvailableListener(listener: (ByteArray, Int, Int) -> Unit) {
        frameListener = listener
    }

    // Convert YUV_420_888 to NV21
    private fun yuv420ToNV21(image: Image): ByteArray {
        val y = image.planes[0].buffer
        val u = image.planes[1].buffer
        val v = image.planes[2].buffer

        val ySize = y.remaining()
        val uSize = u.remaining()
        val vSize = v.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        y.get(nv21, 0, ySize)
        v.get(nv21, ySize, vSize)
        u.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    @SuppressLint("MissingPermission")
    fun startCamera() {

        Log.d(TAG, "startCamera() called")

        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // --------- FIX: choose back camera ----------
        for (id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)

            if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                cameraId = id
                break
            }
        }

        Log.d(TAG, "Selected cameraId = $cameraId")

        cameraManager.openCamera(
            cameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    Log.d(TAG, "Camera opened successfully")
                    cameraDevice = device
                    createPreviewSession()
                }

                override fun onDisconnected(device: CameraDevice) {
                    Log.e(TAG, "Camera disconnected")
                }

                override fun onError(device: CameraDevice, error: Int) {
                    Log.e(TAG, "Camera error: $error")
                }
            },
            backgroundHandler
        )
    }

    private fun createPreviewSession() {

        Log.d(TAG, "Creating preview session...")

        val width = 1280
        val height = 720

        val imageReader = ImageReader.newInstance(
            width, height,
            ImageFormat.YUV_420_888,
            2
        )

        Log.d(TAG, "ImageReader created: ${width}x${height}")

        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            Log.d(TAG, "Frame received from camera")

            try {
                val nv21 = yuv420ToNV21(image)
                frameListener?.invoke(nv21, image.width, image.height)
            } finally {
                image.close()
            }

        }, backgroundHandler)

        val surface = imageReader.surface

        cameraDevice?.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d(TAG, "Camera configured successfully")

                    captureSession = session
                    val requestBuilder =
                        cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

                    requestBuilder.addTarget(surface)

                    session.setRepeatingRequest(
                        requestBuilder.build(),
                        null,
                        backgroundHandler
                    )
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Camera configuration FAILED")
                }
            },
            backgroundHandler
        )
    }
}
