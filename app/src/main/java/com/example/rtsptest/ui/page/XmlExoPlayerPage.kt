package com.example.rtsptest.ui.page

import android.util.Log
import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerView
import com.example.rtsptest.ELITE_STA
import com.example.rtsptest.ELITE_URL
import com.example.rtsptest.PENDELCAM
import com.example.rtsptest.SYNC_KIT
import com.example.rtsptest.databinding.LayoutBinding

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun XmlExoPlayerPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
    }
    AndroidView(
        factory = {
            val binding =
                LayoutInflater.from(it).let { inflater -> LayoutBinding.inflate(inflater) }
            binding.playerView.player = exoPlayer

            exoPlayer.apply {
                setMediaSource(
//                    RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(ELITE_STA))
                    RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(SYNC_KIT))
//                    RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(ELITE_URL))
                )
                addAnalyticsListener(object : EventLogger(null) {

                    override fun onPlayerStateChanged(eventTime: AnalyticsListener.EventTime, playWhenReady: Boolean, playbackState: Int) {
                        super.onPlayerStateChanged(eventTime, playWhenReady, playbackState)
                        Log.d("CustomEventLogger", "onPlayerStateChanged: playWhenReady = $playWhenReady, playbackState = $playbackState")
                    }

                        // 您可以根據需要重寫更多方法來添加日誌
                })
                prepare()
                play()
            }
            binding.root
        },
        modifier = modifier
    )
}


@Composable
fun ExoPlayerRTSPStream(modifier: Modifier = Modifier, rtspUrl: String = ELITE_URL) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(rtspUrl)
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
    )
}