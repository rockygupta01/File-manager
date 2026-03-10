package com.privacyfilemanager.feature.root.shell

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ShellResult(
    val output: String,
    val error: String,
    val exitCode: Int
)

object RootShell {

    suspend fun execute(command: String): ShellResult = withContext(Dispatchers.IO) {
        try {
            // BUG 3 FIX: Use ProcessBuilder and redirectErrorStream to prevent deadlocks from full buffers
            // Use stream `.use` and `.take(5 * 1024 * 1024)` to cap output at 5MB preventing OOM
            val process = ProcessBuilder("su", "-c", command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText().take(5 * 1024 * 1024) }
            val exitCode = process.waitFor()
            ShellResult(output, "", exitCode)
        } catch (e: Exception) {
            ShellResult("", e.message ?: "Unknown error", -1)
        }
    }

    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = execute("id")
            result.exitCode == 0 && result.output.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }

    suspend fun listDirectory(path: String): ShellResult = execute("ls -la '$path'")

    suspend fun chmod(path: String, permissions: String): ShellResult =
        execute("chmod $permissions '$path'")

    suspend fun cat(path: String): ShellResult = execute("cat '$path'")

    suspend fun mount(device: String, mountPoint: String, options: String = "rw"): ShellResult =
        execute("mount -o remount,$options '$device' '$mountPoint'")
}
