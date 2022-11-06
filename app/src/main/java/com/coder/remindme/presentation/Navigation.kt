package com.coder.remindme.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coder.remindme.MainActivity
import com.coder.remindme.presentation.viewmodel.RemindersViewModel

@Composable
fun Navigation() {
    val navigationController = rememberNavController()
    val remindersViewModel = hiltViewModel<RemindersViewModel>()
    val reminderScreen by MainActivity.showSetReminderScreen.observeAsState()
    val reminderData by MainActivity.reminderData.observeAsState()

    if (reminderScreen!!) {
        ReminderEditScreen(
            navController = navigationController,
            remindersViewModel = remindersViewModel,
            reminder = reminderData
        )
    } else {

        NavHost(
            navController = navigationController,
            startDestination = Screen.ReminderScreen.route
        ) {
            composable(route = Screen.ReminderScreen.route) {
                ReminderScreen(navigationController, remindersViewModel)
            }

            composable(route = Screen.ReminderEditScreen.route) {
                ReminderEditScreen(navigationController, remindersViewModel)
            }
        }
    }
}