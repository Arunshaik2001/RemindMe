package com.coder.remindme.presentation

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.presentation.viewmodel.RemindersViewModel
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

@Composable
fun ReminderEditScreen(
    navController: NavController,
    remindersViewModel: RemindersViewModel,
    reminder: Reminder? = null
) {

    Scaffold(
        topBar = {
            AppBar()
        }
    ) {
        val titleInputValue = remember { mutableStateOf(TextFieldValue(reminder?.title ?: "")) }
        val titleError = remember { mutableStateOf("") }
        val descriptionInputValue =
            remember { mutableStateOf(TextFieldValue(reminder?.description ?: "")) }
        val descriptionError = remember { mutableStateOf("") }

        var localDateTime: LocalDateTime? by remember {
            mutableStateOf(null)
        }

        val remindTypeState = remember {
            mutableStateOf(reminder?.remindType ?: RemindType.NONE)
        }

        if (reminder != null)
            localDateTime = LocalDateTime.ofInstant(reminder.reminderStart, ZoneId.systemDefault())

        val mContext = LocalContext.current

        BoxWithLayout {
            Column(
                Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Set Reminder",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextFieldValidation(value = titleInputValue.value.text, onValueChange = {
                    titleInputValue.value = TextFieldValue(it)
                }, placeholder = {
                    Text(
                        text = "Enter your title"
                    )
                },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(
                        color = Color.Black, fontSize = TextUnit.Unspecified,
                        fontFamily = FontFamily.SansSerif
                    ),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .border(5.dp, Color.Unspecified, RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xffe2ebf0),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    error = titleError.value
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextFieldValidation(
                    value = descriptionInputValue.value.text,
                    onValueChange = {
                        descriptionInputValue.value = TextFieldValue(it)
                    },
                    placeholder = {
                        Text(
                            text = "Enter your subtitle"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(
                        color = Color.Black, fontSize = TextUnit.Unspecified,
                        fontFamily = FontFamily.SansSerif
                    ),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xffe2ebf0),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    error = descriptionError.value
                )

                Spacer(modifier = Modifier.height(10.dp))

                DatePicker {
                    localDateTime = it
                }
                Spacer(modifier = Modifier.height(20.dp))
                TimePicker(localDateTime) {
                    localDateTime = it
                }
                RepeatTypeDropDown(defaultReminderType = reminder?.remindType) {
                    remindTypeState.value = it
                }

                if (localDateTime != null)
                    Text(
                        text = "Your Selected Date and Time: ${
                            localDateTime!!.toLocalDate()
                        } and ${localDateTime!!.toLocalTime()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {


                    val result = validateData(
                        titleInputValue,
                        descriptionInputValue,
                        titleError,
                        descriptionError,
                        localDateTime,
                        mContext
                    )

                    if (!result) {
                        return@Button
                    }

                    remindersViewModel.createReminder(
                        titleInputValue.value.text,
                        descriptionInputValue.value.text,
                        remindTypeState.value,
                        localDateTime!!.atZone(ZoneId.systemDefault()).toInstant()
                    )
                    navController.popBackStack()

                    if(reminder != null){
                        (mContext as Activity?)?.finish()
                    }
                }) {
                    Text(text = "Set Reminder")
                }
            }
        }
    }


}

fun validateData(
    titleInputValue: MutableState<TextFieldValue>,
    descriptionInputValue: MutableState<TextFieldValue>,
    titleError: MutableState<String>,
    descriptionError: MutableState<String>,
    localDateTime: LocalDateTime?,
    mContext: Context
): Boolean {
    if (titleInputValue.value.text.isEmpty() && descriptionInputValue.value.text.isEmpty()) {
        titleError.value = "title can't be empty"
        descriptionError.value = "subtitle can't be empty"
        return false
    }

    if (titleInputValue.value.text.isEmpty()) {
        titleError.value = "title can't be empty"
        return false
    } else {
        titleError.value = ""
    }

    if (descriptionInputValue.value.text.isEmpty()) {
        descriptionError.value = "subtitle can't be empty"
        return false
    } else {
        descriptionError.value = ""
    }

    if (localDateTime == null) {
        Toast.makeText(mContext, "Select Time and Date", Toast.LENGTH_LONG).show()
        return false
    }

    return true
}

@Composable
fun RepeatTypeDropDown(defaultReminderType: RemindType?, remindTypeClick: (RemindType) -> Unit) {

    var mExpanded by remember { mutableStateOf(false) }


    val mCities = RemindType.values().map {
        it.name
    }.toList()

    var mSelectedText by remember { mutableStateOf(defaultReminderType?.name ?: "") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        Modifier
            .padding(20.dp)

    ) {

        OutlinedTextField(
            value = mSelectedText,
            onValueChange = { mSelectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = { Text("Select Reminder Type") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )

        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
        ) {
            mCities.forEach { label ->
                DropdownMenuItem(onClick = {
                    mSelectedText = label
                    mExpanded = false
                    remindTypeClick(RemindType.valueOf(mSelectedText.uppercase()))
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun BoxWithLayout(content: @Composable ColumnScope.() -> Unit) {
    Column {
        content()
    }
}

@Composable
fun DatePicker(onDateSelected: (LocalDateTime) -> Unit) {
    val mContext = LocalContext.current

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDate = remember { mutableStateOf("") }

    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
            onDateSelected(
                LocalDateTime.of(
                    mYear,
                    mMonth + 1,
                    mDayOfMonth,
                    LocalDateTime.now().hour,
                    LocalDateTime.now().minute,
                    LocalDateTime.now().second
                )
            )
        }, mYear, mMonth, mDay
    )

    mDatePickerDialog.datePicker.minDate = Date().time


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            mDatePickerDialog.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))) {
            Text(text = "Open Date Picker", color = Color.White)
        }
        Spacer(modifier = Modifier.size(50.dp))
    }
}

@Composable
fun TimePicker(localDateTime: LocalDateTime?, onTimeSelected: (LocalDateTime) -> Unit) {
    val mContext = LocalContext.current

    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    val mTime = remember { mutableStateOf("") }

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, hour: Int, minute: Int ->
            mTime.value = "$hour:$minute"
            onTimeSelected(
                LocalDateTime.of(
                    localDateTime?.toLocalDate(),
                    LocalTime.of(hour, minute, 0)
                )
            )
        }, mHour, mMinute, true
    )


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { mTimePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
        ) {
            Text(text = "Open Time Picker", color = Color.White)
        }
        Spacer(modifier = Modifier.size(50.dp))
    }
}