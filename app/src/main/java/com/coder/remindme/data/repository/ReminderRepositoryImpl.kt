package com.coder.remindme.data.repository

import android.content.Context
import android.util.Log
import com.coder.core.util.Constants
import com.coder.core.util.Resource
import com.coder.remindme.R
import com.coder.remindme.data.helpers.worker.ReminderWorkManagerRepository
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.data.local.entity.ReminderEntity
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val appContext: Context
) : ReminderRepository {

    private val reminderWorkManager = ReminderWorkManagerRepository(reminderDao)

    override fun insertReminder(reminder: Reminder): Flow<Resource<Long>> {
        Log.i(Constants.TAG, "insertReminder")

        return flow {
            emit(Resource.Loading<Long>())
            val reminderEntity = ReminderEntity(
                reminderStart = reminder.reminderStart,
                reminderEnd = reminder.reminderEnd,
                remindType = reminder.remindType,
                title = reminder.title,
                description = reminder.description
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
        return flow {
            emit(Resource.Loading<Unit>())
            reminderDao.deleteReminder(reminder.id)
            val tag = "${appContext.getString(R.string.reminder_worker_tag)}${reminder.id}"
            reminderWorkManager.cancelWorkRequest(appContext,tag)
            emit(Resource.Success<Unit>(null))
        }
    }

    override fun getReminderById(reminderId: Long): Flow<Resource<Reminder>> {
        return flow {
            emit(Resource.Loading<Reminder>())
            val reminderEntity = reminderDao.getReminderById(reminderId)
            emit(Resource.Success<Reminder>(reminderEntity.toReminder()))
        }
    }

    override fun updateReminder(reminder: Reminder): Flow<Resource<Unit>> {
        return flow { }
    }

    override fun getReminders(): Flow<Resource<List<Reminder>>> {
        return flow {
            emit(Resource.Loading<List<Reminder>>())
            emit(Resource.Success<List<Reminder>>(reminderDao.getReminders().map {
                it.toReminder()
            }.toList()))
        }
    }
}