package com.coder.remindme.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.coder.remindme.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ReminderDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Query("DELETE FROM reminders WHERE id == :reminderId")
    suspend fun deleteReminder(reminderId: Long)

    @Query("SELECT * FROM reminders WHERE id == :reminderId")
    suspend fun getReminderById(reminderId: Long): ReminderEntity

    @Query("SELECT * FROM reminders")
    fun getReminders(): Flow<List<ReminderEntity>>
}