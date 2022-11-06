package com.coder.remindme.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coder.remindme.R
import com.coder.remindme.presentation.viewmodel.RemindersViewModel

@Composable
fun ReminderScreen(navController: NavController, remindersViewModel: RemindersViewModel) {
    Scaffold(
        topBar = {
            AppBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.ReminderEditScreen.route)
                }
            ) {
                Icon(Icons.Filled.Add, "", tint = Color(R.color.purple_700))
            }
        }
    ) {
        LaunchedEffect(key1 = true) {
            remindersViewModel.getAllReminders()
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Your Reminders:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(10.dp))
            RemindersList(remindersResource = remindersViewModel.reminderListState) {
                remindersViewModel.deleteReminder(it)
            }
        }
    }

}
