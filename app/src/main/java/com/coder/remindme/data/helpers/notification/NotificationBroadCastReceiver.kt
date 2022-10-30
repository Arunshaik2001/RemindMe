package com.coder.remindme.data.helpers.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.app.NotificationManagerCompat
import com.coder.core.util.Constants
import com.coder.remindme.R
import com.coder.remindme.data.helpers.worker.ReminderWorkManagerRepository
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadCastReceiver : HiltBroadcastReceiver() {

    companion object {
        const val TAG = "NotificationBroadCast"
    }

    @Inject
    lateinit var reminderWorkManager: ReminderWorkManagerRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            context.getString(R.string.complete_action) ->
                reminderWorkManager.cancelWorkRequest(
                    context,
                    intent.getStringExtra(context.getString(R.string.worker_tag)) ?: ""
                )
            context.getString(R.string.postpone_action) -> {
                reminderWorkManager.cancelWorkRequest(
                    context,
                    intent.getStringExtra(context.getString(R.string.worker_tag)) ?: ""
                )

                reminderWorkManager.createWorkRequestAndEnqueue(
                    context,
                    time = Instant.now().plus(1, ChronoUnit.HOURS),
                    isFirstTime = false,
                    reminderId = intent.getLongExtra(
                        context.getString(R.string.reminder_id),
                        -1
                    )
                )
            }
            else -> Log.i(TAG, "Nothing to Perform this Action ${intent.action}")
        }

        with(NotificationManagerCompat.from(context)) {
            cancel(intent.getIntExtra(Constants.NOTIFICATION_ID, -1))
        }
    }
}

abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @SuppressWarnings("EmptyFunctionBlock")
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}
