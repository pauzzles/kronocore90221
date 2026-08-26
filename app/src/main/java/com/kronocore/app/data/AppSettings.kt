package com.kronocore.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object AppSettings {

    private const val PREFS_NAME = "kronocore_app_settings"
    private const val KEY_MASTER_ENABLED = "key_master_reminders_enabled"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isMasterEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_MASTER_ENABLED, true)
    }

    fun setMasterEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_MASTER_ENABLED, enabled).apply()
    }

    fun getMasterEnabledFlow(context: Context): Flow<Boolean> = callbackFlow {
        val prefs = getPrefs(context)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_MASTER_ENABLED) {
                trySend(prefs.getBoolean(KEY_MASTER_ENABLED, true))
            }
        }
        trySend(prefs.getBoolean(KEY_MASTER_ENABLED, true))
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}
