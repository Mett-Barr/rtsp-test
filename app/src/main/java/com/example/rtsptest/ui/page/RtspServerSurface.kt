package com.example.rtsptest.ui.page

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.rtsptest.CameraController
import com.example.rtsptest.FakeConnectChecker
import com.example.rtsptest.TextureViewVideoSource
import com.pedro.library.util.sources.audio.NoAudioSource
import com.pedro.rtspserver.RtspServerStream

@Composable
fun RtspServerSurface() {
    var click by remember { mutableStateOf({}) }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .clickable { click() },
        factory = {
            TextureView(it).apply {
                val view = this

                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                        val camera = CameraController(it)
                        camera.openCamera(it, 0, Surface(p0))

                        click = {
                            RtspServerStream(
                                it,
                                33333,
                                FakeConnectChecker,
                                TextureViewVideoSource(view),
                                NoAudioSource()
                            ).apply {
                                val boo = prepareVideo(
                                    width = 1920,
                                    height = 1080,
                                    bitrate = 300000,
                                    fps = 30,
                                    iFrameInterval = 1,
                                )
                                val audioBoo =
                                    prepareAudio(
                                        sampleRate = 0,
                                        isStereo = false,
                                        bitrate = 0,
                                        echoCanceler = false,
                                        noiseSuppressor = false
                                    )
                                if (boo && audioBoo) {
                                    startStream()
                                } else {
                                    Log.d("!!!", "onSurfaceTextureAvailable: prepareVideo = false")
                                }
                            }
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
                    }

                    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                        return true
                    }

                    override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                    }
                }
            }
        })
}