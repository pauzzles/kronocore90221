package com.kronocore.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPlatform(platform: Platform): String {
        return platform.name
    }

    @TypeConverter
    fun toPlatform(value: String): Platform {
        return Platform.fromString(value)
    }

    @TypeConverter
    fun fromDaysOfWeek(days: Set<Int>): String {
        return days.sorted().joinToString(",")
    }

    @TypeConverter
    fun toDaysOfWeek(value: String): Set<Int> {
        if (value.isBlank()) return emptySet()
        return value.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }
}
