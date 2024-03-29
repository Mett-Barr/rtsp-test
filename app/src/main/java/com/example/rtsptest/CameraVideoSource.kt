package com.example.rtsptest

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.example.rtsptest.camera.CameraController
import com.example.rtsptest.camera.HEIGHT
import com.example.rtsptest.camera.WIDTH
import com.example.rtsptest.camera.testCameraPreview
import com.pedro.encoder.input.video.Camera2ApiManager
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.library.util.sources.video.VideoSource

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraVideoSource(
    val context: Context,
    val cameraController: CameraController = CameraController(context)
) : VideoSource() {
    var boo = false

    override fun create(width: Int, height: Int, fps: Int): Boolean {
        this.width = WIDTH
        this.height = HEIGHT
        return true
    }

    override fun start(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture

        surfaceTexture.setDefaultBufferSize(width, height)
        cameraController.openCamera(context, surfaceTexture)

        boo = true
    }

    override fun stop() {
    }

    override fun release() {
        // 释放相关资源
    }

    override fun isRunning(): Boolean = boo
}
