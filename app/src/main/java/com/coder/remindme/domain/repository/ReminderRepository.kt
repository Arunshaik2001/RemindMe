package com.coder.remindme.domain.repository

import com.coder.remindme.domain.model.Reminder
import com.coder.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun insertReminder(reminder: Reminder): Flow<Resource<Long>>

    fun deleteReminder(reminder: Reminder): Flow<Resource<Unit>>

    fun getReminderById(reminderId: Long): Reminder

    fun updateReminder(reminder: Reminder): Flow<Resource<Unit>>

    fun getReminders(): Flow<Resource<List<Reminder>>>
}