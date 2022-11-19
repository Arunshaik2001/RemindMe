package com.coder.remindme.domain.usecase

import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.repository.ReminderRepository

class GetReminderByIdUseCase(private val reminderRepository: ReminderRepository) {

    operator fun invoke(reminderId: Long): Reminder {
        return reminderRepository.getReminderById(reminderId = reminderId)
    }
}