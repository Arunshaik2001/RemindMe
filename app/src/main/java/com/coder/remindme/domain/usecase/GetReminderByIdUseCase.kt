package com.coder.remindme.domain.usecase

import com.coder.core.util.Resource
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class GetReminderByIdUseCase(private val reminderRepository: ReminderRepository) {

    operator fun invoke(reminderId: Long): Flow<Resource<Reminder>> {
        return reminderRepository.getReminderById(reminderId = reminderId)
    }
}