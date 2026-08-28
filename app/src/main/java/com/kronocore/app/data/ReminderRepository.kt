package com.kronocore.app.data

import android.content.Context
import com.kronocore.app.alarm.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReminderRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context)
) {
    private val reminderDao = database.reminderDao()

    val allReminders: Flow<List<Reminder>> = reminderDao.getAllRemindersFlow()

    suspend fun insert(reminder: Reminder): Long = withContext(Dispatchers.IO) {
        val existing = reminderDao.getAllReminders().find {
            it.platform == reminder.platform &&
            it.hour == reminder.hour &&
            it.minute == reminder.minute &&
            it.daysOfWeek == reminder.daysOfWeek &&
            it.notifyMinutesBefore == reminder.notifyMinutesBefore
        }
        if (existing != null) {
            if (!existing.isEnabled && reminder.isEnabled) {
                val updated = existing.copy(isEnabled = true)
                reminderDao.update(updated)
                AlarmScheduler.scheduleAlarm(context, updated)
            }
            return@withContext existing.id
        }

        val id = reminderDao.insert(reminder)
        val created = reminder.copy(id = id)
        if (created.isEnabled) {
            AlarmScheduler.scheduleAlarm(context, created)
        }
        id
    }

    suspend fun update(reminder: Reminder) = withContext(Dispatchers.IO) {
        reminderDao.update(reminder)
        if (reminder.isEnabled) {
            AlarmScheduler.scheduleAlarm(context, reminder)
        } else {
            AlarmScheduler.cancelAlarm(context, reminder.id)
        }
    }

    suspend fun toggleEnabled(reminder: Reminder) = withContext(Dispatchers.IO) {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        reminderDao.update(updated)
        if (updated.isEnabled) {
            AlarmScheduler.scheduleAlarm(context, updated)
        } else {
            AlarmScheduler.cancelAlarm(context, updated.id)
        }
    }

    suspend fun delete(reminder: Reminder) = withContext(Dispatchers.IO) {
        reminderDao.delete(reminder)
        AlarmScheduler.cancelAlarm(context, reminder.id)
    }

    val isMasterEnabled: Flow<Boolean> = AppSettings.getMasterEnabledFlow(context)

    suspend fun setMasterEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        AppSettings.setMasterEnabled(context, enabled)
        if (enabled) {
            AlarmScheduler.rescheduleAll(context)
        } else {
            AlarmScheduler.cancelAllAlarms(context)
        }
    }

    suspend fun resetToDefaultSchedules() = withContext(Dispatchers.IO) {
        val existing = reminderDao.getAllReminders()
        for (r in existing) {
            AlarmScheduler.cancelAlarm(context, r.id)
        }
        reminderDao.deleteAll()
        val defaults = DefaultSchedules.getDefaultReminders()
        reminderDao.insertAll(defaults)
        AlarmScheduler.rescheduleAll(context)
    }

    suspend fun ensureDefaultDataLoaded() = withContext(Dispatchers.IO) {
        deduplicateReminders()
        syncTikTokSchedule()
        syncInstagramSchedule()
        val count = reminderDao.getCount()
        if (count == 0) {
            val defaults = DefaultSchedules.getDefaultReminders()
            reminderDao.insertAll(defaults)
            AlarmScheduler.rescheduleAll(context)
        }
    }

    private suspend fun syncTikTokSchedule() {
        val all = reminderDao.getAllReminders()
        val tikTokReminders = all.filter { it.platform == Platform.TIKTOK && it.isEveryDay() }
        val oldTimes = setOf(Pair(3, 30), Pair(11, 30), Pair(15, 30), Pair(19, 30))
        val hasOldSchedule = tikTokReminders.any { Pair(it.hour, it.minute) in oldTimes }

        if (hasOldSchedule) {
            for (reminder in tikTokReminders) {
                reminderDao.delete(reminder)
                AlarmScheduler.cancelAlarm(context, reminder.id)
            }
            val newTikTokDefaults = DefaultSchedules.getDefaultReminders().filter { it.platform == Platform.TIKTOK }
            reminderDao.insertAll(newTikTokDefaults)
            AlarmScheduler.rescheduleAll(context)
        }
    }

    private suspend fun syncInstagramSchedule() {
        val all = reminderDao.getAllReminders()
        val instagramReminders = all.filter { it.platform == Platform.INSTAGRAM }
        val targetTimes = setOf(
            Pair(1, 30),
            Pair(3, 30),
            Pair(7, 30),
            Pair(19, 30),
            Pair(21, 30),
            Pair(23, 30)
        )
        val currentTimes = instagramReminders.map { Pair(it.hour, it.minute) }.toSet()
        val isDifferent = currentTimes != targetTimes || instagramReminders.any { !it.isEveryDay() }

        if (isDifferent) {
            for (reminder in instagramReminders) {
                reminderDao.delete(reminder)
                AlarmScheduler.cancelAlarm(context, reminder.id)
            }
            val newInstagramDefaults = DefaultSchedules.getDefaultReminders().filter { it.platform == Platform.INSTAGRAM }
            reminderDao.insertAll(newInstagramDefaults)
            AlarmScheduler.rescheduleAll(context)
        }
    }

    private suspend fun deduplicateReminders() {
        val all = reminderDao.getAllReminders()
        if (all.isEmpty()) return

        val grouped = all.groupBy {
            "${it.platform}_${it.hour}_${it.minute}_${it.daysOfWeek.sorted().joinToString(",")}_${it.notifyMinutesBefore}"
        }

        var hasDuplicates = false
        for ((_, group) in grouped) {
            if (group.size > 1) {
                hasDuplicates = true
                val toKeep = group.firstOrNull { it.isEnabled } ?: group.first()
                val toDelete = group.filter { it.id != toKeep.id }
                for (reminder in toDelete) {
                    reminderDao.delete(reminder)
                    AlarmScheduler.cancelAlarm(context, reminder.id)
                }
            }
        }

        if (hasDuplicates) {
            AlarmScheduler.rescheduleAll(context)
        }
    }
}
