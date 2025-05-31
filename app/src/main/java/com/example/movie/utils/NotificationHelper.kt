package com.example.movie.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.movie.R
import com.example.movie.data.model.local.Movie

object NotificationHelper {

    private const val CHANNEL_ID = "movie_list_channel"
    private const val CHANNEL_NAME = "Movie list updates"
    private const val CHANNEL_DESC = "Notifies when a movie is added to your list"
    private const val NOTIFICATION_ID = 101

    /** App start-da çağırılır. */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = CHANNEL_DESC }

            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    /** Filmi siyahıya əlavə edəndən dərhal sonra çağırılır. */
    fun showMovieAdded(context: Context, movie: Movie) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.add)       // öz icon-unuzu qoyun
            .setContentTitle("Added to list")
            .setContentText(movie.title)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}
