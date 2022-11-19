package com.coder.remindme.data.repository

import android.content.Context
import com.coder.core.util.Resource
import com.coder.remindme.R
import com.coder.remindme.data.helpers.notification.NotificationHelper
import com.coder.remindme.data.helpers.worker.ReminderWorkManagerRepository
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.data.local.entity.ReminderEntity
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val appContext: Context
) : ReminderRepository {

    private val reminderWorkManager = ReminderWorkManagerRepository(reminderDao)

    override fun insertReminder(reminder: Reminder): Flow<Resource<Long>> {

        return flow {
            emit(Resource.Loading<Long>())
            val reminderEntity = ReminderEntity(
                reminder.id,
                reminderStart = reminder.reminderStart,
                reminderEnd = reminder.reminderEnd,
                remindType = reminder.remindType,
                title = reminder.title,
                description = reminder.description,
                hasCompleted = reminder.hasCompleted,
                completedOn = reminder.completedOn,
                hasCanceled = reminder.hasCanceled
            )
            val id = reminderDao.insertReminder(reminderEntity)

            reminderWorkManager.createWorkRequestAndEnqueue(
                appContext,
                reminderId = id,
                time = reminderEntity.reminderStart
            )

            emit(Resource.Success<Long>(id))
        }
    }

    override fun deleteReminder(reminder: Reminder): Flow<Resource<Unit>> {
        if(reminder.id == null)
            return flow {  }
        return flow {
            emit(Resource.Loading<Unit>())
            reminderDao.deleteReminder(reminder.id)
            val tag = "${appContext.getString(R.string.reminder_worker_tag)}${reminder.id}"
            reminderWorkManager.cancelWorkRequest(appContext, tag)
            NotificationHelper().removeNotification(reminder.id.toInt(),appContext)
            emit(Resource.Success<Unit>(null))
        }
    }

    override fun getReminderById(reminderId: Long): Reminder {
        val reminderEntity = reminderDao.getReminderById(reminderId)
        return reminderEntity.toReminder()
    }

    override fun updateReminder(reminder: Reminder): Flow<Resource<Unit>> {
        return flow { }
    }

    override fun getReminders(): Flow<Resource<List<Reminder>>> {
        return flow {
            emit(Resource.Loading<List<Reminder>>())
            reminderDao.getReminders().collect { reminderList ->
                emit(
                    Resource.Success<List<Reminder>>(reminderList.map { it.toReminder() })
                )
            }
        }
    }
}