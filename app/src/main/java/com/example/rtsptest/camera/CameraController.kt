package com.example.rtsptest.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
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

class CameraController(context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraList = cameraManager.cameraIdList
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    private var imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 10)
    var previewSurface: Surface? = null
    private val surfaces: List<OutputConfiguration> get() = listOfNotNull(previewSurface, imageReader.surface).map { OutputConfiguration(it) }

    private val handlerThread = HandlerThread("CameraPhoto").apply { start() }
    private val handler = Handler(handlerThread.looper)


    fun openCamera(context: Context, id: String, surface: Surface) {
        Log.d("!!!", "openCamera: ")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        previewSurface = surface

        cameraManager.openCamera(
            id, object : CameraDevice.StateCallback() {
                @RequiresApi(Build.VERSION_CODES.P)
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera

                    val characteristics = cameraManager.getCameraCharacteristics(id)
                    val configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                    // 假設您關注的是預覽尺寸，您可以這樣獲取最大的預覽尺寸：
                    val previewSize = configurationMap?.getOutputSizes(SurfaceTexture::class.java)?.maxByOrNull { it.height * it.width }
                    Log.d("CameraResolution", "Max preview size: ${previewSize?.width} x ${previewSize?.height}")
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

    fun openCamera(context: Context, surface: Surface, index: Int = 0) {
        Log.d("!!!", "openCamera: ")
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
        surfaceTexture.setDefaultBufferSize(1920, 1080)
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
                    configurationMap?.getOutputSizes(SurfaceTexture::class.java)?.forEach {
                        Log.d("CameraResolution", "Max preview size: ${it?.width} x ${it?.height}")
                    }

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

    fun open(context: Context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // 创建一个基于SurfaceTexture的Surface

        cameraManager.openCamera(
            cameraList[0], object : CameraDevice.StateCallback() {
                @RequiresApi(Build.VERSION_CODES.P)
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera

                    val characteristics = cameraManager.getCameraCharacteristics(cameraList[0])
                    val configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                    // 获取最大的预览尺寸
                    configurationMap?.getOutputSizes(SurfaceTexture::class.java)?.forEach {
                        Log.d("CameraResolution", "Max preview size: ${it?.width} x ${it?.height}")
                    }

                    // 实例化ImageReader用于捕获图片

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
        camera: CameraDevice) {
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


    suspend fun takePhoto(): List<Bitmap> =
        suspendCoroutine { continuation ->
            // 清除 imageReader 中的所有待處理圖像
            while (imageReader.acquireNextImage() != null) {
            }

            val captureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
                    addTarget(imageReader.surface)
                }

            // 設置 imageReader 的回調來接收圖像數據
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireNextImage()
                if (image != null) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    image.close()  // 這裡確保圖像被關閉

                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    continuation.resume(listOf(bitmap))
                }
            }, handler)


            // 在當前的會話上發送捕獲請求
            captureRequestBuilder?.let {
                cameraCaptureSession?.capture(it.build(), null, handler)
            }

        }

    suspend fun takeBurstPhotos2(): List<Bitmap> = suspendCoroutine { continuation ->
        val bitmaps = Collections.synchronizedList(mutableListOf<Bitmap>())

        // 清除 imageReader 中的所有待處理圖像
        while (imageReader.acquireNextImage() != null) {
        }

        val captureRequestBuilder =
            cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
                addTarget(imageReader.surface)
            }

        imageReader.setOnImageAvailableListener({ reader ->
            while (true) {
                val image = reader.acquireNextImage() ?: break  // 確保圖像不為 null
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                image.close() // 關閉圖像以避免佔用資源

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmaps.add(bitmap)

                // 檢查是否收集了足夠的圖像，如果是，則結束協程
                if (bitmaps.size == 10) {
                    continuation.resume(bitmaps.toList())
                    break
                }
            }
        }, handler)

        // 創建並發出連拍請求
        val captureRequests = (1..10).map { captureRequestBuilder?.build() }
        captureRequestBuilder?.let {
            cameraCaptureSession?.captureBurst(captureRequests, null, handler)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun testCameraPreview(context: Context, surfaceTexture: SurfaceTexture) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // 权限问题处理
        return
    }

    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraList = cameraManager.cameraIdList
    if (cameraList.isEmpty()) {
        // 无可用相机处理
        return
    }

    // 选择后置相机，模拟openCameraFacing的行为
    val cameraId = cameraList.find { id ->
        val characteristics = cameraManager.getCameraCharacteristics(id)
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
        facing == CameraCharacteristics.LENS_FACING_BACK
    } ?: return

    cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            // 使用提供的SurfaceTexture设置预览尺寸
            surfaceTexture.setDefaultBufferSize(1920, 1080)  // 假设1920x1080是最优分辨率
            val previewSurface = Surface(surfaceTexture)

            camera.createCaptureSession(listOf(previewSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    try {
                        val requestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                            addTarget(previewSurface)
                        }
                        session.setRepeatingRequest(requestBuilder.build(), null, null)
                    } catch (e: CameraAccessException) {
                        // 配置失败处理
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    // 配置失败处理
                }
            }, null)
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }, null)
}
