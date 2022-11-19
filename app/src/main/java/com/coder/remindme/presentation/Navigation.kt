package com.coder.remindme.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.coder.core.util.Constants
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.presentation.viewmodel.RemindersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Navigation() {
    val navigationController = rememberNavController()
    val remindersViewModel = hiltViewModel<RemindersViewModel>()

    NavHost(navController = navigationController, startDestination = Screen.ReminderScreen.route) {
        composable(route = Screen.ReminderScreen.route) {
            ReminderScreen(navigationController, remindersViewModel)
        }

        composable(
            route = Screen.ReminderEditScreen.route + "/{reminder_id}", arguments = listOf(
                navArgument("reminder_id"){
                    type = NavType.StringType
                    defaultValue = "-1"
                }
            )
        ) { entry ->
            val reminderState = remember {
                mutableStateOf<Reminder?>(null)
            }
            val reminderId = entry.arguments?.getString("reminder_id")!!.toLong()

            if(reminderId > -1L) {
                rememberCoroutineScope().launch {
                    withContext(Dispatchers.IO) {
                        val reminder = remindersViewModel.getReminderById(reminderId)
                        reminderState.value = reminder
                        Log.i(Constants.TAG,reminder.toString())
                    }
                }
            }


            ReminderEditScreen(navigationController, remindersViewModel, reminder = reminderState.value)
        }
    }
}