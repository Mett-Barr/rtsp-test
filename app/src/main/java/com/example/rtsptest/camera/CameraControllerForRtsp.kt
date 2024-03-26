package com.example.rtsptest.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Collections
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraControllerForRtsp(context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraList = cameraManager.cameraIdList
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    private var imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 10)
    var previewSurface: Surface? = null
    private val surfaces: List<OutputConfiguration> get() = listOfNotNull(previewSurface, imageReader.surface).map { OutputConfiguration(it) }


    fun openCamera(context: Context, surface: Surface, index: Int = 0) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        previewSurface = surface

        cameraManager.openCamera(
            cameraList[index], object : CameraDevice.StateCallback() {
                @RequiresApi(Build.VERSION_CODES.P)
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera

                    val characteristics = cameraManager.getCameraCharacteristics(cameraList[index])
                    val configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                    // 假設您關注的是預覽尺寸，您可以這樣獲取最大的預覽尺寸：
                    val previewSize = configurationMap?.getOutputSizes(SurfaceTexture::class.java)?.maxByOrNull { it.height * it.width }
                    Log.d("!!!", "Max preview size: ${previewSize?.width} x ${previewSize?.height}")
                    imageReader = ImageReader.newInstance(previewSize!!.width, previewSize.height, ImageFormat.JPEG, 10)


                    createCameraPreviewSession(context, camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()  // 關閉 CameraDevice
                    cameraDevice = null  // 清除引用
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()  // 關閉 CameraDevice
                    cameraDevice = null  // 清除引用
                }
            },
            null
        )
    }

    fun openCamera(context: Context, surfaceTexture: SurfaceTexture, index: Int = 0) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // 创建一个基于SurfaceTexture的Surface
        previewSurface = Surface(surfaceTexture)

        cameraManager.openCamera(
            cameraList[index], object : CameraDevice.StateCallback() {
                @RequiresApi(Build.VERSION_CODES.P)
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera

                    val characteristics = cameraManager.getCameraCharacteristics(cameraList[index])
                    val configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                    // 获取最大的预览尺寸
                    val previewSize = configurationMap?.getOutputSizes(SurfaceTexture::class.java)?.maxByOrNull { it.height * it.width }
                    Log.d("CameraResolution", "Max preview size: ${previewSize?.width} x ${previewSize?.height}")

                    // 实例化ImageReader用于捕获图片
                    imageReader = ImageReader.newInstance(previewSize!!.width, previewSize.height, ImageFormat.JPEG, 10)

                    createCameraPreviewSession(context, camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                }
            },
            null
        )
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun createCameraPreviewSession(
        context: Context,
        camera: CameraDevice
    ) {
        // 創建預覽會話
        val session = SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            surfaces,
            ContextCompat.getMainExecutor(context),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session

                    val previewRequestBuilder =
                        camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                            previewSurface?.let { addTarget(it) }
                        }

                    // 在會話中設置重複請求以更新預覽
                    session.setRepeatingRequest(previewRequestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("CameraController", "Failed to configure camera preview session")
                }
            }
        )

        camera.createCaptureSession(session)
    }
}