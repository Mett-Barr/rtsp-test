package com.example.rtsptest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView
import com.example.rtsptest.ui.page.ExoPlayerPage
import com.example.rtsptest.ui.page.VLCPlayerPage
import com.example.rtsptest.ui.page.XmlExoPlayerPage
import com.example.rtsptest.ui.theme.RTSPTestTheme

@UnstableApi class MainActivity : ComponentActivity() {

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.layout)
//
//        // Initialize ExoPlayer
//        player = ExoPlayer.Builder(this).build()
//
//        // Set up the player with the PlayerView
//        val playerView: PlayerView = findViewById(R.id.player_view)
//        playerView.player = player
//
//        // Create MediaSource
//        val mediaSource = RtspMediaSource.Factory()
//            .createMediaSource(MediaItem.fromUri(PENDELCAM))
//
//        player.setMediaSource(mediaSource)
//        player.prepare()
//        player.play()

        setContent {
            RTSPTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
//                        VLCPlayerPage(Modifier.weight(1f))
//                        ExoPlayerPage(Modifier.weight(1f))
                        XmlExoPlayerPage()
                    }
                }
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        player.pause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        player.play()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        player.release()
//    }
}