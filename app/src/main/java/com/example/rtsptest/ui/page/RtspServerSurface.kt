package com.example.rtsptest.ui.page

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.example.rtsptest.CameraVideoSource
import com.example.rtsptest.FakeConnectChecker
import com.example.rtsptest.camera.CameraController
import com.pedro.library.util.sources.audio.NoAudioSource
import com.pedro.rtspserver.RtspServerStream

@Composable
fun RtspServerSurface() {

    var click by remember { mutableStateOf({}) }

    val context = LocalContext.current

    AndroidView(
        modifier = Modifier
            .height(with(LocalDensity.current) { 1080.toDp() })
            .width(with(LocalDensity.current) { 1920.toDp() })
            .clickable { click() },
        factory = {
            TextureView(it).apply {
                val view = this

                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                        val camera = CameraController(context)

                        // start stream
                        click = {
                            val cameraVideoSource = CameraVideoSource(camera, context)
                            cameraVideoSource.start(p0)
                            RtspServerStream(
                                context,
                                44554,
                                FakeConnectChecker,
                                cameraVideoSource,
                                NoAudioSource()
                            ).apply {
                                prepareVideo(1920, 1080, 3000000)
                                prepareAudio(16000, false, 64 * 1024)
                                startPreview(view)
                                startStream()
                                val url = getStreamClient().getEndPointConnection()
                                Log.d("!!!", "onSurfaceTextureAvailable: $url")
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