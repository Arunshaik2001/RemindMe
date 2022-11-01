package com.coder.remindme.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coder.core.util.Constants
import com.coder.core.util.Resource
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.domain.usecase.ReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val remindersRepository: ReminderUseCases
) : ViewModel() {


    var reminderListState by mutableStateOf<Resource<List<Reminder>>>(Resource.Loading<List<Reminder>>())

    fun getAllReminders() =
        viewModelScope.launch {
            remindersRepository.getRemindersUseCase().collect{
                reminderListState = it
                it.data?.toString()?.let { it1 -> Log.i(Constants.TAG, it1) }
            }
        }

    fun createReminder(
        title: String,
        description: String,
        repeat: RemindType,
        time: Instant
    ) = viewModelScope.launch {
        Log.i(Constants.TAG,"createReminder")
        remindersRepository.insertReminderUseCase(
            Reminder(
                -1,
                title,
                description,
                time,
                Instant.now().plus(1, ChronoUnit.HOURS),
                repeat
            )
        ).collect {
            Log.i(Constants.TAG, it.toString())
        }
    }

    fun deleteReminder(
        reminder: Reminder
    ) = viewModelScope.launch {
        Log.i(Constants.TAG,"createReminder")
        remindersRepository.deleteReminderUseCase(
            reminder
        ).collect {
            Log.i(Constants.TAG, it.toString())
            if(it is Resource.Success){
                getAllReminders()
            }
        }
    }
}