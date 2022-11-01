package com.coder.remindme.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coder.core.util.Resource
import com.coder.remindme.domain.model.Reminder

@Composable
fun RemindersList(remindersResource: Resource<List<Reminder>>, onRemoveClick: (Reminder) -> Unit) {

    when (remindersResource) {
        is Resource.Success<List<Reminder>> -> {
            if(remindersResource.data.isNullOrEmpty()){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Add Reminders",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
            else {
                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    remindersResource.data.let { reminderList ->
                        items(count = reminderList.size) { index ->
                            val reminder = reminderList[index]
                            ReminderCard(reminder = reminder) {
                                onRemoveClick(it)
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
        is Resource.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Loading Data",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
        else -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error Loading Data",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}