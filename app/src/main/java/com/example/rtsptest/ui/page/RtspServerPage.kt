package com.example.rtsptest.ui.page

import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.foundation.background
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
import com.example.rtsptest.FakeConnectChecker
import com.pedro.library.view.OpenGlView
import com.pedro.rtspserver.RtspServerCamera2
import com.pedro.rtspserver.server.ClientListener
import com.pedro.rtspserver.server.ServerClient
import org.videolan.libvlc.AWindow
import org.videolan.libvlc.AWindow.SurfaceCallback

@Composable
fun RtspServerPage() {
    var text by remember { mutableStateOf("")}

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            OpenGlView(it).apply {
                RtspServerCamera2(this, FakeConnectChecker, 44554).apply {
                    streamClient.setClientListener(object : ClientListener {
                        override fun onClientConnected(client: ServerClient) {
                        }
                        override fun onClientDisconnected(client: ServerClient) {
                        }
                    })
                    holder.addCallback(
                        object : SurfaceHolder.Callback {
                            override fun surfaceCreated(p0: SurfaceHolder) {
                            }

                            override fun surfaceChanged(
                                p0: SurfaceHolder,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                                prepareVideo()
                                startStream()

                                text = streamClient.getEndPointConnection()
                                Log.d("!!!", "surfaceChanged: $text")
                            }

                            override fun surfaceDestroyed(p0: SurfaceHolder) {
                            }

                        }
                    )
                }
            }
        })


    Text(text = text, color = Color.White, modifier = Modifier.wrapContentSize().background(Color.Black))
}