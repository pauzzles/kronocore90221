package com.kronocore.app.data

object DefaultSchedules {
    private val ALL_DAYS = setOf(1, 2, 3, 4, 5, 6, 7)

    fun getDefaultReminders(): List<Reminder> {
        val reminders = mutableListOf<Reminder>()

        // TikTok Schedule (US Peak Times converted to IST, every day)
        // EST Peak (11 AM - 1 PM & 7 PM - 9 PM) & PST Peak (8 AM - 10 AM & 5 PM - 7 PM)
        val tikTokTimes = listOf(
            Pair(5, 30),   // 5:30 AM IST  (7:00 PM EST)
            Pair(6, 30),   // 6:30 AM IST  (8:00 PM EST / 5:00 PM PST)
            Pair(7, 30),   // 7:30 AM IST  (9:00 PM EST / 6:00 PM PST)
            Pair(8, 30),   // 8:30 AM IST  (7:00 PM PST)
            Pair(21, 30),  // 9:30 PM IST  (11:00 AM EST / 8:00 AM PST)
            Pair(22, 30),  // 10:30 PM IST (12:00 PM EST / 9:00 AM PST)
            Pair(23, 30)   // 11:30 PM IST (1:00 PM EST / 10:00 AM PST)
        )
        for ((hour, minute) in tikTokTimes) {
            reminders.add(
                Reminder(
                    platform = Platform.TIKTOK,
                    hour = hour,
                    minute = minute,
                    daysOfWeek = ALL_DAYS,
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Instagram Schedule (Philippines Time converted to IST, every day)
        // 6 AM, 10 AM, 2 PM, 6 PM, 10 PM, 2 AM PHT
        val instagramTimes = listOf(
            Pair(3, 30),   // 3:30 AM IST  (6:00 AM PHT)
            Pair(7, 30),   // 7:30 AM IST  (10:00 AM PHT)
            Pair(11, 30),  // 11:30 AM IST (2:00 PM PHT)
            Pair(15, 30),  // 3:30 PM IST  (6:00 PM PHT)
            Pair(19, 30),  // 7:30 PM IST  (10:00 PM PHT)
            Pair(23, 30)   // 11:30 PM IST (2:00 AM PHT)
        )
        for ((hour, minute) in instagramTimes) {
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = hour,
                    minute = minute,
                    daysOfWeek = ALL_DAYS,
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        return reminders
    }
}
