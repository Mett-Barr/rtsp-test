package com.example.rtsptest

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.rtsptest.ui.page.ExoPlayerRTSPStream
import com.example.rtsptest.ui.page.RtspServerPage
import com.example.rtsptest.ui.page.RtspServerSurface
import com.example.rtsptest.ui.page.VLCPlayerPage
import com.example.rtsptest.ui.page.XmlExoPlayerPage
import com.example.rtsptest.ui.theme.RTSPTestTheme

@UnstableApi class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        setContent {
            RTSPTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RtspServerSurface()
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissionsNeeded: ArrayList<String> = getRequestPermissions()
        val lackPermissions: MutableList<String> = ArrayList()
        val var3: Iterator<*> = permissionsNeeded.iterator()
        while (var3.hasNext()) {
            val permission = var3.next() as String
            if (ActivityCompat.checkSelfPermission(this, permission) != 0) {
                lackPermissions.add(permission)
            }
        }
        val size = lackPermissions.size
        if (size > 0) {
            val permissions = arrayOfNulls<String>(size)
            for (i in 0 until size) {
                permissions[i] = lackPermissions[i]
            }
            ActivityCompat.requestPermissions(this, permissions, 33)
        }

        // 檢查外部存儲是否可用
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Log.d("!!!", "saveImageToDCIM: 部存儲不可用")
        } else {
            Log.d("!!!", "saveImageToDCIM: OK")
        }
    }

    private fun getRequestPermissions(): ArrayList<String> {
        val permissionsNeeded = ArrayList<String>()

        with(permissionsNeeded) {
            add("android.permission.MANAGE_EXTERNAL_STORAGE")
            add("android.permission.READ_EXTERNAL_STORAGE")
            add("android.permission.WRITE_EXTERNAL_STORAGE")
            add("android.permission.CAMERA")
            add("android.permission.RECORD_AUDIO")
        }
        return permissionsNeeded
    }

}