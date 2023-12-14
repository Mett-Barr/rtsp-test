package com.example.rtsptest.ui.page

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView
import com.example.rtsptest.PENDELCAM

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun ExoPlayerPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            Log.d("ExoPlayer", "Player has been built")

            val mediaSource = RtspMediaSource.Factory()
                .createMediaSource(MediaItem.fromUri(PENDELCAM))
            Log.d("ExoPlayer", "Media source has been set")

            setMediaSource(mediaSource)

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.d("ExoPlayer", "Player Error: ", error)
                    Toast.makeText(context, "播放錯誤: ${error.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> Log.d("ExoPlayer", "Player state: IDLE")
                        Player.STATE_BUFFERING -> Log.d("ExoPlayer", "Player state: BUFFERING")
                        Player.STATE_READY -> Log.d("ExoPlayer", "Player state: READY")
                        Player.STATE_ENDED -> Log.d("ExoPlayer", "Player state: ENDED")
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    Log.d("ExoPlayer", "Tracks changed: $tracks")
                }
            })

//            prepare()
//            play()
        }
    }
    val playerView = remember {
        PlayerView(context).apply {
            player = exoPlayer
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setBackgroundColor(Color.RED)

            (player as ExoPlayer).prepare()
            (player as ExoPlayer).play()
        }
    }

    DisposableEffect(Unit) {



        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { playerView },
        modifier = modifier.fillMaxSize()
    )
}