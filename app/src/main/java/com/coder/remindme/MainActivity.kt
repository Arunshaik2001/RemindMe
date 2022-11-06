package com.coder.remindme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.MutableLiveData
import com.coder.core.util.Constants
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.presentation.*
import com.coder.remindme.ui.theme.RemindMeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {

        val showSetReminderScreen = MutableLiveData(false)
        val reminderData = MutableLiveData<Reminder>(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val data: Uri? = intent.data

        Log.i(Constants.TAG, data.toString())

        if (Intent.ACTION_SEND == intent.action) {
            val isSchemaSame = data?.scheme == this.getString(R.string.app_name_lowercase)
            val isHostSame = data?.host == "reminder"
            Log.i(Constants.TAG, "$isSchemaSame $isHostSame")
            if (isSchemaSame && isHostSame) {
                val title = data?.getQueryParameter("title")
                val description = data?.getQueryParameter("description")
                val startTimeInMillis = data?.getQueryParameter("startTimeInMillis")
                val reminderType = data?.getQueryParameter("reminderType")


                val startTimeInInstant = startTimeInMillis?.toLong()
                    ?.let { Instant.ofEpochMilli(it) }
                Log.i(Constants.TAG, "$title $description $startTimeInMillis $reminderType")
                showSetReminderScreen.postValue(true)
                reminderData.postValue(
                    Reminder(
                        id = -1,
                        title = title ?: "",
                        description = description ?: "",
                        reminderStart = startTimeInInstant ?: Instant.now(),
                        remindType = RemindType.valueOf(reminderType ?: ""),
                        reminderEnd = Instant.now()
                    )
                )
            }
        }

        setContent {
            RemindMeTheme {
                Navigation()
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}








