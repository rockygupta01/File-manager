package com.privacyfilemanager.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages app lock credentials using encrypted storage.
 * PIN/pattern hash is stored in EncryptedSharedPreferences.
 * All data stays on-device in app-private storage.
 */
@Singleton
class AppLockManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "app_lock_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_LOCK_ENABLED = "lock_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_TIMEOUT_MINUTES = "timeout_minutes"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until"
    }

    val isLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOCK_ENABLED, false)

    val isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    val timeoutMinutes: Int
        get() = prefs.getInt(KEY_TIMEOUT_MINUTES, 1)

    /**
     * Set a new PIN. Stores salt + SHA-256 hash.
     */
    fun setPin(pin: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = hashPin(pin, salt)

        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt.toHex())
            .putBoolean(KEY_LOCK_ENABLED, true)
            .apply()
    }

    /**
     * Verify a PIN against stored hash.
     */
    fun verifyPin(pin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val saltHex = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val salt = saltHex.hexToBytes()

        val inputHash = hashPin(pin, salt)
        val result = storedHash == inputHash

        if (!result) {
            incrementFailedAttempts()
        } else {
            resetFailedAttempts()
        }

        return result
    }

    /**
     * Remove app lock.
     */
    fun removeLock() {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .putBoolean(KEY_LOCK_ENABLED, false)
            .apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun setTimeoutMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_TIMEOUT_MINUTES, minutes).apply()
    }

    /**
     * Check if currently locked out due to failed attempts.
     */
    fun isLockedOut(): Boolean {
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0)
        if (lockoutUntil > System.currentTimeMillis()) return true
        return false
    }

    fun getFailedAttempts(): Int = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)

    private fun incrementFailedAttempts() {
        val attempts = getFailedAttempts() + 1
        val editor = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts)

        // Exponential lockout: 5 attempts = 30s, 10 = 60s, 15 = 5min
        if (attempts >= 5) {
            val lockoutSeconds = when {
                attempts >= 15 -> 300L
                attempts >= 10 -> 60L
                else -> 30L
            }
            editor.putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + lockoutSeconds * 1000)
        }

        editor.apply()
    }

    private fun resetFailedAttempts() {
        prefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .remove(KEY_LOCKOUT_UNTIL)
            .apply()
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return digest.digest(pin.toByteArray()).toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
