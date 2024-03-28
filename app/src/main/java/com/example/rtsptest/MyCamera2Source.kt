package com.example.rtsptest

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pedro.encoder.input.video.Camera2ApiManager
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.library.util.sources.video.VideoSource


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyCamera2Source(context: Context): VideoSource() {

    private val camera = Camera2ApiManager(context)
    private var facing = CameraHelper.Facing.BACK

    override fun create(width: Int, height: Int, fps: Int): Boolean {
        return true
    }

    override fun start(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        if (!isRunning()) {
            surfaceTexture.setDefaultBufferSize(width, height)
            camera.prepareCamera(surfaceTexture, width, height, fps, facing)
            camera.openCameraFacing(facing)

            Log.d("!!!", "start: MyCamera2Source")
        }
    }

    override fun stop() {
        if (isRunning()) camera.closeCamera()
    }

    override fun release() {}

    override fun isRunning(): Boolean = camera.isRunning
}
