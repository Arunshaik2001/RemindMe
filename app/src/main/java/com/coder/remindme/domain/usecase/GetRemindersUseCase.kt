package com.coder.remindme.domain.usecase

import com.coder.core.util.Resource
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class GetRemindersUseCase(private val reminderRepository: ReminderRepository) {

    operator fun invoke(): Flow<Resource<List<Reminder>>> {
        return reminderRepository.getReminders()
    }
}