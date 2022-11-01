package com.coder.remindme

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.coder.core.util.Constants
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.domain.model.Reminder
import com.coder.remindme.presentation.Navigation
import com.coder.remindme.presentation.ReminderCard
import com.coder.remindme.presentation.RemindersList
import com.coder.remindme.presentation.Screen
import com.coder.remindme.presentation.viewmodel.RemindersViewModel
import com.coder.remindme.ui.theme.RemindMeTheme
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemindMeTheme {
                Navigation()
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

@Composable
fun ReminderScreen(navController: NavController, remindersViewModel: RemindersViewModel) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.ReminderEditScreen.route)
                }
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        LaunchedEffect(key1 = true) {
            remindersViewModel.getAllReminders()
        }
        Column() {
            Text(text = "You Reminders:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(10.dp))
            RemindersList(remindersResource = remindersViewModel.reminderListState) {
                remindersViewModel.deleteReminder(it)
            }
        }
    }

}

@Composable
fun OutlinedTextFieldValidation(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    error: String = "",
    isError: Boolean = error.isNotEmpty(),
    trailingIcon: @Composable (() -> Unit)? = {
        if (error.isNotEmpty())
            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colors.error)
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        disabledTextColor = Color.Black
    )

) {

    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        OutlinedTextField(
            enabled = enabled,
            readOnly = readOnly,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = singleLine,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 0.dp)
            )
        }
    }
}


@Composable
fun ReminderEditScreen(navController: NavController, remindersViewModel: RemindersViewModel) {
    val titleInputValue = remember { mutableStateOf(TextFieldValue()) }
    val titleError = remember { mutableStateOf("") }
    val descriptionInputValue = remember { mutableStateOf(TextFieldValue()) }
    val descriptionError = remember { mutableStateOf("") }

    var localDateTime: LocalDateTime? by remember {
        mutableStateOf(null)
    }

    val remindTypeState = remember {
        mutableStateOf(RemindType.NONE)
    }

    val mContext = LocalContext.current

    Column(
        Modifier
            .padding(10.dp)
            .fillMaxSize(),
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

        OutlinedTextFieldValidation(value = descriptionInputValue.value.text, onValueChange = {
            descriptionInputValue.value = TextFieldValue(it)
        }, placeholder = {
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

        DatePicker {
            localDateTime = it
            Log.i(Constants.TAG + "Date", localDateTime.toString())
        }
        Spacer(modifier = Modifier.height(20.dp))
        TimePicker(localDateTime) {
            localDateTime = it
        }
        RepeatTypeDropDown {
            remindTypeState.value = it
        }

        Button(onClick = {

            val result = validateData(
                titleInputValue,
                descriptionInputValue,
                titleError,
                descriptionError,
                localDateTime,
                mContext
            )

            if(!result) {
                return@Button
            }

            Log.i(
                Constants.TAG,
                localDateTime!!.atZone(ZoneId.systemDefault()).toInstant().toString()
            )

            remindersViewModel.createReminder(
                titleInputValue.value.text,
                descriptionInputValue.value.text,
                remindTypeState.value,
                localDateTime!!.atZone(ZoneId.systemDefault()).toInstant()
            )
            navController.popBackStack()
        }) {
            Text(text = "Set Reminder")
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
fun RepeatTypeDropDown(remindTypeClick: (RemindType) -> Unit) {

    var mExpanded by remember { mutableStateOf(false) }


    val mCities = RemindType.values().map {
        it.name
    }.toList()

    var mSelectedText by remember { mutableStateOf("") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {

        OutlinedTextField(
            value = mSelectedText,
            onValueChange = { mSelectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = { Text("NONE") },
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
fun DatePicker(onDateSelected: (LocalDateTime) -> Unit) {

    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }


    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear ${LocalDate.now().lengthOfMonth()}"
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

        // Creating a button that on
        // click displays/shows the DatePickerDialog
        Button(onClick = {
            mDatePickerDialog.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))) {
            Text(text = "Open Date Picker", color = Color.White)
        }

        // Adding a space of 100dp height
        Spacer(modifier = Modifier.size(50.dp))

        // Displaying the mDate value in the Text
        Text(text = "Selected Date: ${mDate.value}", fontSize = 30.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun TimePicker(localDateTime: LocalDateTime?, onTimeSelected: (LocalDateTime) -> Unit) {

    // Fetching local context
    val mContext = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }

    // Creating a TimePicker dialod
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

        // On button click, TimePicker is
        // displayed, user can select a time
        Button(
            onClick = { mTimePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
        ) {
            Text(text = "Open Time Picker", color = Color.White)
        }

        // Add a spacer of 100dp
        Spacer(modifier = Modifier.size(50.dp))

        // Display selected time
        Text(text = "Selected Time: ${mTime.value}", fontSize = 30.sp)
    }
}

