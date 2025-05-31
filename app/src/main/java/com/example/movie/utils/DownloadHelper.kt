package com.example.movie.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.getSystemService

object DownloadHelper {

    fun startDownload(context: Context, url: String, fileName: String = "sample_video.mp4") {
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            )
        }

        val dm = context.getSystemService<DownloadManager>()
        dm?.enqueue(request)

        Toast.makeText(context, "Download started…", Toast.LENGTH_SHORT).show()
    }
}
