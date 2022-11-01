package com.coder.remindme.presentation

sealed class Screen(val route: String){
    object ReminderScreen: Screen("remember_screen")
    object ReminderEditScreen: Screen("remember_edit_screen")
}
