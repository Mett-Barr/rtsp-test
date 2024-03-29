package com.example.rtsptest.ui.page

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.rtsptest.CameraVideoSource
import com.example.rtsptest.FakeConnectChecker
import com.example.rtsptest.camera.CameraController
import com.example.rtsptest.camera.HEIGHT
import com.example.rtsptest.camera.WIDTH
import com.pedro.library.util.sources.audio.NoAudioSource
import com.pedro.rtspserver.RtspServerStream

var click = {}

@Composable
fun RtspServerSurface() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .clickable { click() },
        factory = {
            TextureView(it).also { textureView ->
                textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {

                        Log.d("!!!", "w = $p1,  h = $p2")

                        /**           ↓ ↓ ↓ ↓ ↓          完整操作     ↓ ↓ ↓ ↓ ↓                */
                        val camera = CameraController(context)
                        val cameraVideoSource = CameraVideoSource(context, camera)

                        RtspServerStream(
                            context,
                            44554,
                            FakeConnectChecker,
                            cameraVideoSource,
                            NoAudioSource()
                        ).apply {
//                            prepareVideo(WIDTH, HEIGHT, 4000000)
                            prepareVideo(1920, 1080, 3000000)
                            prepareAudio(16000, false, 64 * 1024)

                            Log.d("!!!", "onSurfaceTextureAvailable: videoSource.width = ${videoSource.width}")
                            Log.d("!!!", "onSurfaceTextureAvailable: videoSource.height = ${videoSource.height}")
//                            startPreview(textureView)
                            Log.d("!!!", "onSurfaceTextureAvailable: videoSource.width = ${videoSource.width}")
                            Log.d("!!!", "onSurfaceTextureAvailable: videoSource.height = ${videoSource.height}")
                            startStream()

                            click = {
                                startPreview(textureView)

//                                click = run {
//                                    var switch = true
//                                    {
//                                        if (switch) {
//                                            getGlInterface().setPreviewResolution(WIDTH, HEIGHT)
//                                            getGlInterface().setEncoderSize(WIDTH, HEIGHT)
//                                        } else {
//                                            getGlInterface().setPreviewResolution(HEIGHT, WIDTH)
//                                        }
//                                        switch = !switch
//                                    }
//                                }
                            }
                            val url = getStreamClient().getEndPointConnection()
                            Log.d("!!!", "onSurfaceTextureAvailable: $url")
                            text = url
                        }
                        /**           ↑ ↑ ↑ ↑ ↑          完整操作           ↑ ↑ ↑ ↑ ↑          */

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