package com.kronocore.app.auth

import android.content.Context
import java.security.MessageDigest

object AuthManager {

    private const val PREFS_NAME = "kronocore_auth_prefs"
    private const val KEY_IS_AUTHENTICATED = "is_device_authenticated"

    // Cryptographic salt and one-way SHA-256 hash (never exposes plaintext password in repository)
    private const val SALT = "KronoCoreSecuritySalt_2026_x99"
    private const val TARGET_HASH = "7d57fd5b5c3979888830034a67950bc43eb468a0dcb2c72cddfa81a1faac9a79"

    fun isDeviceAuthenticated(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_AUTHENTICATED, false)
    }

    fun verifyAndAuthenticate(context: Context, input: String): Boolean {
        val trimmed = input.trim()
        val calculatedHash = sha256("$SALT$trimmed")
        if (calculatedHash.equals(TARGET_HASH, ignoreCase = true)) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_IS_AUTHENTICATED, true).apply()
            return true
        }
        return false
    }

    private fun sha256(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
