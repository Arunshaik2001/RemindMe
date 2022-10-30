package com.coder.remindme.domain.model

import java.time.Instant

data class Reminder(
    val id: Long,
    val title: String,
    val description: String,
    val reminderStart: Instant,
    val reminderEnd: Instant,
    val remindType: RemindType
)


enum class RemindType{
    HOURLY,
    DAILY,
    MONTHLY,
    YEARLY,
    WEEKLY,
    NONE
}