package com.coder.remindme.data.helpers.notification

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.coder.core.util.Constants
import com.coder.remindme.MainActivity
import com.coder.remindme.R
import com.coder.remindme.domain.model.Notification

class NotificationHelper {

    fun createNotification(
        context: Context,
        notificationDTO: Notification
    ) {
        val actionIntent = Intent(context, MainActivity::class.java)
        val actionPendingIntent = PendingIntent.getActivity(
            context, 0,
            actionIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val notificationId = notificationDTO.id.toInt()

        val workerTag =
            "${context.resources.getString(R.string.reminder_worker_tag)}${notificationDTO.id}"

        val postponeIntent = Intent(context, NotificationBroadCastReceiver::class.java).apply {
            action = context.resources.getString(R.string.postpone_action)
            putExtra(context.resources.getString(R.string.worker_tag), workerTag)
            putExtra(context.resources.getString(R.string.reminder_id), notificationDTO.id)
            putExtra(Constants.NOTIFICATION_ID, notificationId)
        }

        val postponePendingIntent = PendingIntent.getBroadcast(
            context, 0,
            postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )


        val cancelIntent = Intent(context, NotificationBroadCastReceiver::class.java).apply {
            action = context.resources.getString(R.string.complete_action)
            putExtra(context.resources.getString(R.string.worker_tag), workerTag)
            putExtra(Constants.NOTIFICATION_ID, notificationId)
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, 0,
            cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(notificationDTO.title)
                .setContentText(notificationDTO.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(actionPendingIntent)
                .addAction(R.drawable.ic_launcher_background, context.getString(R.string.one_hour), postponePendingIntent)
                .addAction(R.drawable.ic_launcher_background, context.getString(R.string.done), cancelPendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            Log.i(Constants.TAG,"createNotification()")
            notify(notificationId, builder.build())
        }
    }
}
