package com.kronocore.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kronocore.app.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(AlarmScheduler.EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val reminder = db.reminderDao().getReminderById(reminderId)

                if (reminder != null && reminder.isEnabled) {
                    NotificationHelper.showPostingReminder(
                        context = context,
                        notificationId = reminder.id.toInt(),
                        platform = reminder.platform,
                        postingTimeFormatted = reminder.formattedTime,
                        minutesBefore = reminder.notifyMinutesBefore
                    )

                    AlarmScheduler.scheduleAlarm(context, reminder)
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error processing alarm", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
