package com.example.rtsptest

import android.content.Context
import android.media.MediaCodec
import android.os.Build
import android.util.Log
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.pedro.common.AudioCodec
import com.pedro.common.ConnectChecker
import com.pedro.common.VideoCodec
import com.pedro.encoder.input.audio.GetMicrophoneData
import com.pedro.library.base.StreamBase
import com.pedro.library.util.sources.audio.AudioSource
import com.pedro.library.util.sources.audio.MicrophoneSource
import com.pedro.library.util.sources.audio.NoAudioSource
import com.pedro.library.util.sources.video.Camera2Source
import com.pedro.library.util.sources.video.VideoSource
import com.pedro.rtspserver.server.RtspServer
import com.pedro.rtspserver.util.RtspServerStreamClient
import java.nio.ByteBuffer

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyRtspServer(
    context: Context, port: Int, connectChecker: ConnectChecker,
    val textureView: TextureView
): StreamBase(context, TextureViewVideoSource(textureView), NoAudioSource()) {

    val rtspServer = RtspServer(connectChecker, port)

    fun startStream() {

//        prepareVideo(
//            width = textureView.width,
//            height = textureView.height,
//            bitrate = 3000000,
//            fps = 30,
//            iFrameInterval = 1,
//        )
//
//        prepareAudio(
//            sampleRate = 0,
//            isStereo = false,
//            bitrate = 0,
//            echoCanceler = false,
//            noiseSuppressor = false
//        )

//        rtspServer.setOnlyVideo(true)

        super.startStream("")



        
        rtspServer.startServer()
    }

    override fun audioInfo(sampleRate: Int, isStereo: Boolean) {
        rtspServer.setAudioInfo(sampleRate, isStereo)
    }

    override fun rtpStartStream(endPoint: String) { //unused
    }

    override fun rtpStopStream() {
        rtspServer.stopServer()
    }

    override fun getAacDataRtp(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        rtspServer.sendAudio(aacBuffer, info)
    }

    override fun onSpsPpsVpsRtp(sps: ByteBuffer, pps: ByteBuffer?, vps: ByteBuffer?) {
        val newSps = sps.duplicate()
        val newPps = pps?.duplicate()
        val newVps = vps?.duplicate()
        rtspServer.setVideoInfo(newSps, newPps, newVps)
    }

    override fun getH264DataRtp(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        Log.d("!!!", "getH264DataRtp: ")
        rtspServer.sendVideo(h264Buffer, info)
    }

    override fun getStreamClient(): RtspServerStreamClient = RtspServerStreamClient(rtspServer)

    override fun setVideoCodecImp(codec: VideoCodec) {
        rtspServer.setVideoCodec(codec)
    }

    override fun setAudioCodecImp(codec: AudioCodec) {
        rtspServer.setAudioCodec(codec);
    }
}