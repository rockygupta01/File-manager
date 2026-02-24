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
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            ShellResult(output, error, exitCode)
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
