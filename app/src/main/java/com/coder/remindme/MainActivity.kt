package com.coder.remindme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.presentation.viewmodel.RemindersViewModel
import com.coder.remindme.ui.theme.RemindMeTheme
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemindMeTheme {
                myContent()
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

@Composable
fun myContent(): LocalDateTime {

    val reminderViewModel = hiltViewModel<RemindersViewModel>()

    // Fetching local context
    val mContext = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar =
        LocalDateTime.now()
    val mHour =
        mCalendar.hour
    val mMinute = mCalendar.minute
    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }
    val mTimeStore = remember { mutableStateOf(mCalendar) }

    // Creating a TimePicker dialog
    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, mHour: Int, mMinute: Int ->
            run {
                mTime.value = "$mHour:$mMinute"
                mTimeStore.value =
                    LocalDateTime.of(
                        mCalendar.toLocalDate(), LocalTime.of(mHour, mMinute)
                    )


                reminderViewModel.createReminder(
                    "Hello",
                    "Hi",
                    repeat = RemindType.HOURLY,
                    time = Instant.now()
                )

                reminderViewModel.getAllReminders()
            }
        }, mHour, mMinute, false
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // On button click, TimePicker is
        // displayed, user can select a time
        Button(
            onClick = { mTimePickerDialog.show() },
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(text = "Open Time Picker", color = Color.White)
        }

        // Add a spacer of 100dp
        Spacer(modifier = Modifier.size(100.dp))

        // Display selected time
        Text(text = "Selected Time: ${mTime.value}", fontSize = 30.sp)
    }
    return mTimeStore.value
}

