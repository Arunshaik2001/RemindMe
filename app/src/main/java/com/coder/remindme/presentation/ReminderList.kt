package com.coder.remindme.presentation

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coder.core.util.Constants
import com.coder.core.util.Resource
import com.coder.remindme.domain.model.Reminder

@OptIn(ExperimentalMaterialApi::class)
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
                    items(items = remindersResource.data, key = {
                        it.id
                    }){ reminder ->
                        val dismissState = rememberDismissState()

                        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                            onRemoveClick(reminder)
                        }
                        SwipeToDismiss(
                            state = dismissState,
                            modifier = Modifier
                                .padding(vertical = Dp(1f)),
                            directions = setOf(
                                DismissDirection.EndToStart
                            ),
                            dismissThresholds = { direction ->
                                FractionalThreshold(if (direction == DismissDirection.EndToStart) 1f else 0.01f)
                            },
                            background = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.White
                                        else -> Color.Red
                                    }
                                )
                                val alignment = Alignment.CenterEnd
                                val icon = Icons.Default.Delete

                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                                )

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(15.dp))
                                        .padding(horizontal = Dp(20f)),
                                    contentAlignment = alignment
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = "Delete Icon",
                                        modifier = Modifier.scale(scale)
                                    )
                                }
                            },
                            dismissContent = {
                                ReminderCard(reminder = reminder) {
                                    onRemoveClick(it)
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        )

                        Divider(Modifier.fillMaxWidth(), Color.DarkGray)
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