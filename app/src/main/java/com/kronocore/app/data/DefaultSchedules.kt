package com.kronocore.app.data

object DefaultSchedules {
    private val ALL_DAYS = setOf(1, 2, 3, 4, 5, 6, 7)

    fun getDefaultReminders(): List<Reminder> {
        val reminders = mutableListOf<Reminder>()

        // TikTok Schedule
        val tikTokTimes = listOf(
            Pair(3, 30),
            Pair(7, 30),
            Pair(11, 30),
            Pair(15, 30),
            Pair(19, 30),
            Pair(23, 30)
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

        // Instagram Schedule
        // Monday (1)
        listOf(Pair(7, 30), Pair(15, 30), Pair(19, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(1),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Tuesday (2)
        listOf(Pair(12, 30), Pair(13, 30), Pair(18, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(2),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Wednesday (3)
        listOf(Pair(8, 30), Pair(16, 30), Pair(18, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(3),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Thursday (4)
        listOf(Pair(4, 30), Pair(9, 30), Pair(18, 30), Pair(23, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(4),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Friday (5)
        listOf(Pair(1, 30), Pair(14, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(5),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Saturday (6)
        listOf(Pair(4, 30), Pair(5, 30), Pair(20, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(6),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        // Sunday (7)
        listOf(Pair(1, 30), Pair(17, 30)).forEach { (h, m) ->
            reminders.add(
                Reminder(
                    platform = Platform.INSTAGRAM,
                    hour = h,
                    minute = m,
                    daysOfWeek = setOf(7),
                    notifyMinutesBefore = 10,
                    isEnabled = true
                )
            )
        }

        return reminders
    }
}
