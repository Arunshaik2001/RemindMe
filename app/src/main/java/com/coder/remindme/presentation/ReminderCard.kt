package com.coder.remindme.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coder.remindme.R
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val colorArray = listOf(Color.Green, Color.Gray, Color.Blue, Color.Magenta, Color.Red)

@Composable
fun ReminderCard(reminder: Reminder, onRemoveClick: (Reminder) -> Unit) {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("dd-MM-yyyy hh:mm a")
    val startDateTime = dateTimeFormatter.format(
        LocalDateTime.ofInstant(
            reminder.reminderStart,
            ZoneId.systemDefault()
        )
    )

    Column(
        horizontalAlignment = Alignment.Start, modifier = Modifier
            .background(color = colorArray[reminder.id.toInt() % colorArray.size],RoundedCornerShape(15.dp))
            .padding(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.clickable { onRemoveClick(reminder) },
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Cross"
            )
        }
        Text(text = reminder.title, color = Color.White)
        Text(text = reminder.description, color = Color.White)

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${reminder.remindType}", color = Color.White)
            Text(text = startDateTime, color = Color.White)
        }
    }
}


@Preview
@Composable
fun Reminder() {
    ReminderCard(
        reminder = Reminder(
            -1,
            "Hi",
            "Hello",
            Instant.now(),
            Instant.now(),
            RemindType.HOURLY
        )
    ) {
        println(it.id)
    }
}