package com.example.rtsptest.ui.page

import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import com.example.rtsptest.PENDELCAM
import com.example.rtsptest.databinding.LayoutBinding

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun  XmlExoPlayerPage() {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    AndroidView(factory = {
        val binding = LayoutInflater.from(it).let { inflater -> LayoutBinding.inflate(inflater) }
        binding.playerView.player = exoPlayer

        exoPlayer.apply {
            setMediaSource(RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(PENDELCAM)))
            prepare()
            play()
        }
        binding.root
    })
}