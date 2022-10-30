package com.coder.remindme.data.helpers.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.coder.core.util.Constants
import com.coder.remindme.R
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.data.helpers.notification.NotificationHelper
import com.coder.remindme.domain.model.Notification
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.time.ZoneOffset

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val reminderDao: ReminderDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    private val reminderWorkManager = ReminderWorkManagerRepository(reminderDao)

    override suspend fun doWork(): Result {


        val reminderId =
            inputData.getLong(appContext.getString(R.string.reminder_instance_key), -1)
        if (reminderId == -1L) {
            return Result.failure()
        }
        val reminder: Reminder = reminderDao.getReminderById(reminderId).toReminder()

        val localDate: LocalDateTime =
            LocalDateTime.ofInstant(reminder.reminderStart, ZoneOffset.UTC)

        Log.i(Constants.TAG,"doWork()")

        notificationHelper.createNotification(
            appContext,
            Notification(
                title = reminder.title,
                description = "${reminder.description} <= ${localDate.hour}:${localDate.minute}",
                id = reminderId,
            )
        )

        if (reminder.remindType != RemindType.NONE) {
            reminderWorkManager.createWorkRequestAndEnqueue(
                reminderId = reminderId,
                context = appContext,
                isFirstTime = false,
                time = reminder.reminderStart
            )
        }
        return Result.success()
    }
}
