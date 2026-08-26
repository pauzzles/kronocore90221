package com.kronocore.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val platform: Platform,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: Set<Int>,
    val notifyMinutesBefore: Int = 10,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val localTime: LocalTime
        get() = LocalTime.of(hour, minute)

    val formattedTime: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            return localTime.format(formatter)
        }

    val notificationLocalTime: LocalTime
        get() = localTime.minusMinutes(notifyMinutesBefore.toLong())

    val formattedNotificationTime: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            return notificationLocalTime.format(formatter)
        }

    fun isEveryDay(): Boolean = daysOfWeek.size == 7

    fun getDaysSummary(): String {
        if (daysOfWeek.size == 7) return "Every day"
        if (daysOfWeek == setOf(1, 2, 3, 4, 5)) return "Weekdays"
        if (daysOfWeek == setOf(6, 7)) return "Weekends"

        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        return daysOfWeek.sorted()
            .mapNotNull { if (it in 1..7) dayNames[it - 1] else null }
            .joinToString(", ")
    }
}
