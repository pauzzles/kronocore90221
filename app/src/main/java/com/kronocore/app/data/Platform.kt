package com.kronocore.app.data

enum class Platform(val displayName: String) {
    TIKTOK("TikTok"),
    INSTAGRAM("Instagram");

    companion object {
        fun fromString(name: String): Platform {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: TIKTOK
        }
    }
}
