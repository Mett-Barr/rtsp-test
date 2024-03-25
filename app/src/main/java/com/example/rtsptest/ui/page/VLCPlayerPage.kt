package com.example.rtsptest.ui.page

import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.rtsptest.ELITE_STA
import com.example.rtsptest.ELITE_URL
import com.example.rtsptest.PENDELCAM
import com.example.rtsptest.SYNC_KIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.io.File

@Composable
fun VLCPlayerPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for LibVLC and MediaPlayer
    val options = ArrayList<String>()
//    options.add("--no-drop-late-frames")
//    options.add("--no-skip-frames")
// Low-latency options
//    options.add("--no-drop-late-frames")
//    options.add("--no-skip-frames")
    options.add("--network-caching=100")  // Minimize network caching
//    options.add("--clock-jitter=0")     // Reduce clock jitter
//    options.add("--clock-synchro=0")    // Disable clock synchronization
//    options.add("--low-delay")         // Prioritize low delay
//    options.add("--high-priority")     // Raise process priority

    val libVLC = remember { LibVLC(context, options) }
    val mediaPlayer = rememberUpdatedState(MediaPlayer(libVLC))

    // Set up the RTSP stream
    val media = Media(libVLC, Uri.parse(ELITE_STA))
//    val media = Media(libVLC, Uri.parse(SYNC_KIT))
//    val media = Media(libVLC, Uri.parse(ELITE_URL))
//    val media = Media(libVLC, Uri.parse(PENDELCAM))
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
            val videoView = SurfaceView(ctx).apply {
                // 设置 SurfaceView 布局参数以填充父布局
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val vlcVout = mediaPlayer.value.vlcVout
            vlcVout.setVideoView(videoView)
            vlcVout.attachViews()
            videoView
        },
        modifier = modifier.clickable {
//            coroutineScope.launch {
//
//                // 获取外部存储的 Download 目录
//                val downloadDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//                val recordingDirectory = File(downloadDirectory, "VLC_Recordings")
//
//                // 确保录制目录存在
//                if (!recordingDirectory.exists()) {
//                    recordingDirectory.mkdirs()
//                }
//
//
//                mediaPlayer.value.record(recordingDirectory.absolutePath)
//                delay(20000)  // 延迟10秒
//                mediaPlayer.value.record(null)
//                Log.d("!!!", "VLCPlayerPage: done")
//                withContext(Dispatchers.Main) {
//                    repeat(10) {
//                        Toast.makeText(context, "!!!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
        }
    )
}
