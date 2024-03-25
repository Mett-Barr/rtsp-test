package com.example.rtsptest

import android.graphics.SurfaceTexture
import android.view.TextureView
import com.pedro.library.util.sources.video.VideoSource

class TextureViewVideoSource(private val textureView: TextureView) : VideoSource() {

    override fun create(width: Int, height: Int, fps: Int): Boolean {
        this.width = width
        this.height = height
        this.fps = fps

        // 确保TextureView可用
        if (textureView.isAvailable) {
            start(textureView.surfaceTexture!!)
            return true
        }

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                start(surface)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                // Handle size changes if necessary
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                stop()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                // Handle frame updates if necessary
            }
        }

        return false
    }

    override fun start(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        if (!isRunning()) {
            surfaceTexture.setDefaultBufferSize(width, height)
//            camera.prepareCamera(surfaceTexture, width, height, fps, facing)
//            camera.openCameraFacing(facing)
        }
        created = true
        // 在这里初始化您的视频源，利用提供的surfaceTexture
    }

    override fun stop() {
        // 在这里停止您的视频源
        created = false
    }

    override fun release() {
        // 清理资源
        stop() // 确保视频源已停止
        surfaceTexture = null
    }

    override fun isRunning(): Boolean {
        return created
    }
}
