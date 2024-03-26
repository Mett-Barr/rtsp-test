package com.example.rtsptest

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.pedro.common.ConnectChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
class MyConnectChecker(val context: Context) : ConnectChecker {
    override fun onAuthError() {
    }

    override fun onAuthSuccess() {
    }

    override fun onConnectionFailed(reason: String) {
        Log.d("!!!", "onConnectionFailed: $reason")
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, reason, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConnectionStarted(url: String) {
        Log.d("!!!", "onConnectionFailed: $url")
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, url, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConnectionSuccess() {
    }

    override fun onDisconnect() {
    }

    override fun onNewBitrate(bitrate: Long) {
    }
}