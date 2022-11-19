package com.coder.remindme.domain.model

import java.time.Instant

data class Reminder(
    val id: Long?,
    val title: String,
    val description: String,
    val reminderStart: Instant,
    val reminderEnd: Instant,
    val remindType: RemindType,
    val hasCompleted: Boolean = false,
    val completedOn: Instant? = null,
    val hasCanceled: Boolean = false
)



enum class RemindType{
    HOURLY,
    DAILY,
    WEEKLY,
    NONE
}