package com.coder.remindme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.data.local.entity.ReminderEntity
import com.coder.remindme.data.local.converter.InstantConverter

@Database(entities = [ReminderEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [InstantConverter::class])
abstract class ReminderDatabase: RoomDatabase() {

    abstract val reminderDao: ReminderDao
}