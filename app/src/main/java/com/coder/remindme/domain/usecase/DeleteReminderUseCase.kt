package com.coder.remindme.domain.usecase

import com.coder.core.util.Resource
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class DeleteReminderUseCase(private val reminderRepository: ReminderRepository) {

    operator fun invoke(reminder: Reminder): Flow<Resource<Unit>> {
        return reminderRepository.deleteReminder(reminder)
    }
}