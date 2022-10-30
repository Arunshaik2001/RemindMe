package com.coder.remindme.domain.usecase

import android.util.Log
import com.coder.core.util.Constants
import com.coder.core.util.Resource
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class InsertReminderUseCase(private val reminderRepository: ReminderRepository) {

    operator fun invoke(reminder: Reminder): Flow<Resource<Long>> {
        Log.i(Constants.TAG,"insert $reminder")
        return reminderRepository.insertReminder(reminder)
    }
}