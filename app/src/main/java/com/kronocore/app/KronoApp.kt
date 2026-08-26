package com.kronocore.app

import android.app.Application
import com.kronocore.app.alarm.AlarmScheduler
import com.kronocore.app.alarm.NotificationHelper
import com.kronocore.app.data.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KronoApp : Application() {

    lateinit var repository: ReminderRepository
        private set

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(this)
        repository = ReminderRepository(this)

        CoroutineScope(Dispatchers.IO).launch {
            repository.ensureDefaultDataLoaded()
            AlarmScheduler.rescheduleAll(this@KronoApp)
        }
    }
}
