package com.example.rtsptest

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.example.rtsptest.camera.CameraController
import com.pedro.library.util.sources.video.VideoSource

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraVideoSource(val cameraController: CameraController, val context: Context) :
    VideoSource() {

    override fun create(width: Int, height: Int, fps: Int): Boolean {
        this.width = 1920  // 这里设定默认宽度，可能需要根据实际情况调整
        this.height = 1080 // 这里设定默认高度，可能需要根据实际情况调整
        this.fps = 30      // 这里设定默认帧率，可能需要根据实际情况调整
        return true
    }

    override fun start(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        surfaceTexture.setDefaultBufferSize(width, height)

        cameraController.openCamera(context, Surface(surfaceTexture))
//        cameraController.openCamera(context, surfaceTexture)
    }

    override fun stop() {
    }

    override fun release() {
        // 释放相关资源
    }

    override fun isRunning(): Boolean = true
}
