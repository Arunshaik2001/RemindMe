package com.coder.remindme.domain.usecase

class ReminderUseCases(
    val deleteReminderUseCase: DeleteReminderUseCase,
    val insertReminderUseCase: InsertReminderUseCase,
    val getReminderByIdUseCase: GetReminderByIdUseCase,
    val updateReminderUseCase: UpdateReminderUseCase,
    val getRemindersUseCase: GetRemindersUseCase
)