package com.example.rtsptest.ui.page

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.rtsptest.CameraVideoSource
import com.example.rtsptest.FakeConnectChecker
import com.example.rtsptest.MyCamera2Source
import com.example.rtsptest.camera.CameraController
import com.pedro.library.util.sources.audio.NoAudioSource
import com.pedro.rtspserver.RtspServerStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RtspServerSurface() {

    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                TextureView(it).also { textureView ->
                    textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {

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
                                prepareVideo(640, 480, 3000000)
                                prepareAudio(16000, false, 64 * 1024)
                                startPreview(textureView)
                                startStream()
                                val url = getStreamClient().getEndPointConnection()
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

        Text(text = text, modifier = Modifier.background(Color.Black), color = Color.White, fontSize = 32.sp)
    }
}