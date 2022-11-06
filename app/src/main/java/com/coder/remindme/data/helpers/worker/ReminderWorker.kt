package com.coder.remindme.data.helpers.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.coder.remindme.R
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.data.helpers.notification.NotificationHelper
import com.coder.remindme.data.local.entity.ReminderEntity
import com.coder.remindme.domain.model.Notification
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
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

        LocalDateTime.ofInstant(reminder.reminderStart, ZoneOffset.UTC)

        notificationHelper.removeNotification(reminderId.toInt(),appContext)

        notificationHelper.createNotification(
            appContext,
            Notification(
                title = reminder.title,
                description = reminder.description,
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
        } else {
            val newReminder = reminder.copy(hasCompleted = true, completedOn = Instant.now())
            reminderDao.insertReminder(
                reminder = ReminderEntity(
                    newReminder.id,
                    newReminder.title,
                    newReminder.description,
                    newReminder.reminderStart,
                    newReminder.reminderEnd,
                    newReminder.remindType,
                    newReminder.hasCompleted,
                    newReminder.completedOn,
                    newReminder.hasCanceled
                )
            )
        }
        return Result.success()
    }
}
