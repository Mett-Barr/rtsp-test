package com.example.rtsptest

import android.util.Log
import com.pedro.common.ConnectChecker

object FakeConnectChecker : ConnectChecker {
    override fun onAuthError() {
    }

    override fun onAuthSuccess() {
    }

    override fun onConnectionFailed(reason: String) {
        Log.d("!!!", "onConnectionFailed: $reason")
    }

    override fun onConnectionStarted(url: String) {
        Log.d("!!!", "onConnectionFailed: $url")
    }

    override fun onConnectionSuccess() {
    }

    override fun onDisconnect() {
    }

    override fun onNewBitrate(bitrate: Long) {
    }
}