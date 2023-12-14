package com.example.rtsptest.ui.page

import android.net.Uri
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.rtsptest.PENDELCAM
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

@Composable
fun VLCPlayerPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // State for LibVLC and MediaPlayer
    val options = ArrayList<String>()
    options.add("--no-drop-late-frames")
    options.add("--no-skip-frames")
    val libVLC = remember { LibVLC(context, options) }
    val mediaPlayer = rememberUpdatedState(MediaPlayer(libVLC))

    // Set up the RTSP stream
    val media = Media(libVLC, Uri.parse(PENDELCAM))
    mediaPlayer.value.media = media

    DisposableEffect(Unit) {
        // Play the RTSP stream
        mediaPlayer.value.play()

        // Cleanup on dispose
        onDispose {
            mediaPlayer.value.release()
            libVLC.release()
        }
    }

    // VLC Player UI
    AndroidView(
        factory = { ctx ->
            val videoView = SurfaceView(ctx)
            val vlcVout = mediaPlayer.value.vlcVout
            vlcVout.setVideoView(videoView)
            vlcVout.attachViews()
            videoView
        },
        modifier = modifier.fillMaxSize()
    )
}
