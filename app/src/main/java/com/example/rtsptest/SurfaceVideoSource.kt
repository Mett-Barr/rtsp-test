package com.example.rtsptest

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import com.pedro.library.util.sources.video.VideoSource

class SurfaceVideoSource : VideoSource() {

    override fun create(width: Int, height: Int, fps: Int): Boolean {
        this.width = width
        this.height = height
        this.fps = fps

        return false
    }

    override fun start(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        if (!isRunning()) {
            surfaceTexture.setDefaultBufferSize(width, height)
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