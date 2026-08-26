package com.kronocore.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kronocore.app.KronoApp
import com.kronocore.app.alarm.AlarmScheduler
import com.kronocore.app.data.Platform
import com.kronocore.app.data.Reminder
import com.kronocore.app.data.ReminderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class NextPostInfo(
    val reminderId: Long,
    val platform: Platform,
    val postTime: LocalTime,
    val postDateTime: LocalDateTime,
    val formattedTime: String,
    val countdownText: String,
    val notifyMinutesBefore: Int
)

data class TodayPostItem(
    val reminderId: Long,
    val platform: Platform,
    val postTime: LocalTime,
    val formattedTime: String,
    val notifyMinutesBefore: Int,
    val isCompleted: Boolean,
    val isEnabled: Boolean
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository =
        (application as? KronoApp)?.repository ?: ReminderRepository(application)

    val allReminders: StateFlow<List<Reminder>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFilter = MutableStateFlow<Platform?>(null)
    val selectedFilter: StateFlow<Platform?> = _selectedFilter

    private val _currentTime = MutableStateFlow(LocalDateTime.now())
    val currentTime: StateFlow<LocalDateTime> = _currentTime

    init {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = LocalDateTime.now()
                delay(1000L)
            }
        }
    }

    fun setPlatformFilter(platform: Platform?) {
        _selectedFilter.value = platform
    }

    val filteredReminders: StateFlow<List<Reminder>> = combine(allReminders, _selectedFilter) { list, filter ->
        if (filter == null) list else list.filter { it.platform == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nextPost: StateFlow<NextPostInfo?> = combine(allReminders, _currentTime) { reminders, now ->
        calculateNextPost(reminders.filter { it.isEnabled }, now)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayUpcomingPosts: StateFlow<List<TodayPostItem>> = combine(allReminders, _currentTime) { reminders, now ->
        getTodayPosts(reminders, now, isCompleted = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCompletedPosts: StateFlow<List<TodayPostItem>> = combine(allReminders, _currentTime) { reminders, now ->
        getTodayPosts(reminders, now, isCompleted = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun calculateNextPost(reminders: List<Reminder>, now: LocalDateTime): NextPostInfo? {
        if (reminders.isEmpty()) return null

        var earliestDateTime: LocalDateTime? = null
        var earliestReminder: Reminder? = null

        for (reminder in reminders) {
            val nextPostDateTime = AlarmScheduler.calculateNextPostTime(reminder, now)
            if (nextPostDateTime != null) {
                if (earliestDateTime == null || nextPostDateTime.isBefore(earliestDateTime)) {
                    earliestDateTime = nextPostDateTime
                    earliestReminder = reminder
                }
            }
        }

        if (earliestDateTime == null || earliestReminder == null) return null

        val duration = Duration.between(now, earliestDateTime)
        val countdown = formatDuration(duration)

        return NextPostInfo(
            reminderId = earliestReminder.id,
            platform = earliestReminder.platform,
            postTime = earliestReminder.localTime,
            postDateTime = earliestDateTime,
            formattedTime = earliestReminder.formattedTime,
            countdownText = countdown,
            notifyMinutesBefore = earliestReminder.notifyMinutesBefore
        )
    }

    private fun getTodayPosts(reminders: List<Reminder>, now: LocalDateTime, isCompleted: Boolean): List<TodayPostItem> {
        val today = now.toLocalDate()
        val currentDayOfWeek = today.dayOfWeek.value
        val currentTime = now.toLocalTime()

        val list = mutableListOf<TodayPostItem>()

        for (reminder in reminders) {
            if (reminder.daysOfWeek.contains(currentDayOfWeek)) {
                val postTime = reminder.localTime
                val hasPassed = postTime.isBefore(currentTime)

                if (hasPassed == isCompleted) {
                    list.add(
                        TodayPostItem(
                            reminderId = reminder.id,
                            platform = reminder.platform,
                            postTime = postTime,
                            formattedTime = reminder.formattedTime,
                            notifyMinutesBefore = reminder.notifyMinutesBefore,
                            isCompleted = hasPassed,
                            isEnabled = reminder.isEnabled
                        )
                    )
                }
            }
        }

        return list.sortedBy { it.postTime }
    }

    private fun formatDuration(duration: Duration): String {
        val totalSeconds = duration.seconds.coerceAtLeast(0)
        val days = totalSeconds / 86400
        val hours = (totalSeconds % 86400) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return when {
            days > 0 -> "in ${days}d ${hours}h"
            hours > 0 -> if (minutes > 0) "in ${hours}h ${minutes}m" else "in ${hours}h"
            minutes > 0 -> "in ${minutes} min"
            else -> "in ${seconds}s"
        }
    }

    fun addReminder(
        platform: Platform,
        hour: Int,
        minute: Int,
        daysOfWeek: Set<Int>,
        notifyMinutesBefore: Int
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                platform = platform,
                hour = hour,
                minute = minute,
                daysOfWeek = daysOfWeek,
                notifyMinutesBefore = notifyMinutesBefore,
                isEnabled = true
            )
            repository.insert(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.update(reminder)
        }
    }

    fun toggleReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.toggleEnabled(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.delete(reminder)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaultSchedules()
        }
    }
}
