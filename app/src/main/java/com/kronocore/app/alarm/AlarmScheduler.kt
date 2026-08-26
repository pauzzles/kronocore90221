package com.kronocore.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kronocore.app.data.AppDatabase
import com.kronocore.app.data.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"
    const val EXTRA_REMINDER_ID = "extra_reminder_id"

    fun calculateNextNotificationTime(reminder: Reminder, fromDateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime? {
        if (!reminder.isEnabled || reminder.daysOfWeek.isEmpty()) return null

        val fromDate = fromDateTime.toLocalDate()

        for (dayOffset in 0..14) {
            val candidateDate = fromDate.plusDays(dayOffset.toLong())
            val candidateDayOfWeek = candidateDate.dayOfWeek.value

            if (reminder.daysOfWeek.contains(candidateDayOfWeek)) {
                val postDateTime = LocalDateTime.of(candidateDate, LocalTime.of(reminder.hour, reminder.minute))
                val notifyDateTime = postDateTime.minusMinutes(reminder.notifyMinutesBefore.toLong())

                if (notifyDateTime.isAfter(fromDateTime)) {
                    return notifyDateTime
                }
            }
        }
        return null
    }

    fun calculateNextPostTime(reminder: Reminder, fromDateTime: LocalDateTime = LocalDateTime.now()): LocalDateTime? {
        if (!reminder.isEnabled || reminder.daysOfWeek.isEmpty()) return null

        val fromDate = fromDateTime.toLocalDate()

        for (dayOffset in 0..14) {
            val candidateDate = fromDate.plusDays(dayOffset.toLong())
            val candidateDayOfWeek = candidateDate.dayOfWeek.value

            if (reminder.daysOfWeek.contains(candidateDayOfWeek)) {
                val postDateTime = LocalDateTime.of(candidateDate, LocalTime.of(reminder.hour, reminder.minute))
                if (postDateTime.isAfter(fromDateTime)) {
                    return postDateTime
                }
            }
        }
        return null
    }

    fun scheduleAlarm(context: Context, reminder: Reminder) {
        if (!reminder.isEnabled) {
            cancelAlarm(context, reminder.id)
            return
        }

        val nextNotificationTime = calculateNextNotificationTime(reminder) ?: return
        val triggerEpochMillis = nextNotificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpochMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerEpochMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpochMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpochMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm for #${reminder.id} at $nextNotificationTime")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission missing for exact alarm", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm", e)
        }
    }

    fun cancelAlarm(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Canceled alarm for #$reminderId")
        }
    }

    suspend fun rescheduleAll(context: Context) {
        val database = AppDatabase.getInstance(context)
        val enabledReminders = database.reminderDao().getEnabledReminders()
        for (reminder in enabledReminders) {
            scheduleAlarm(context, reminder)
        }
        Log.d(TAG, "Rescheduled ${enabledReminders.size} alarms")
    }
}
