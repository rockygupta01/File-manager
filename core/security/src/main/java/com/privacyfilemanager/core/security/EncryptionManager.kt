package com.privacyfilemanager.core.security

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encryption manager using Android Keystore-backed AES-256-GCM.
 * All keys stay on device — hardware-backed where supported.
 */
@Singleton
class EncryptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    /**
     * Encrypt a file to the app-private vault directory.
     * @return The encrypted file path.
     */
    fun encryptFile(sourceFile: File): File {
        val vaultDir = File(context.filesDir, ".vault").apply { mkdirs() }
        val encryptedFile = File(vaultDir, "${System.currentTimeMillis()}_${sourceFile.name}.enc")

        val encrypted = EncryptedFile.Builder(
            context,
            encryptedFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encrypted.openFileOutput().use { output ->
            sourceFile.inputStream().use { input ->
                input.copyTo(output, bufferSize = 8192)
            }
        }

        return encryptedFile
    }

    /**
     * Decrypt a vault file back to its original form.
     * @return The decrypted file.
     */
    fun decryptFile(encryptedFilePath: File, outputFile: File): File {
        val encrypted = EncryptedFile.Builder(
            context,
            encryptedFilePath,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encrypted.openFileInput().use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output, bufferSize = 8192)
            }
        }

        return outputFile
    }

    /**
     * Secure delete — overwrite with random bytes before deletion.
     * Makes file unrecoverable even with forensic tools.
     */
    fun secureDelete(file: File, passes: Int = 3): Boolean {
        if (!file.exists()) return false

        try {
            val random = SecureRandom()
            val buffer = ByteArray(8192)

            repeat(passes) {
                RandomAccessFile(file, "rw").use { raf ->
                    var remaining = raf.length()
                    raf.seek(0)
                    while (remaining > 0) {
                        val toWrite = minOf(buffer.size.toLong(), remaining).toInt()
                        random.nextBytes(buffer)
                        raf.write(buffer, 0, toWrite)
                        remaining -= toWrite
                    }
                    raf.fd.sync()
                }
            }

            return file.delete()
        } catch (e: Exception) {
            return file.delete()
        }
    }
}
