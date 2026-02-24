package com.privacyfilemanager.feature.lan.viewmodel

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Environment
import androidx.lifecycle.ViewModel
import com.privacyfilemanager.feature.lan.server.LocalFileServer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.inject.Inject

data class LanUiState(
    val isRunning: Boolean = false,
    val serverUrl: String = "",
    val port: Int = 8181,
    val error: String? = null
)

@HiltViewModel
class LanViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanUiState())
    val uiState: StateFlow<LanUiState> = _uiState.asStateFlow()

    private var server: LocalFileServer? = null

    fun startServer() {
        try {
            val port = _uiState.value.port
            val rootDir = Environment.getExternalStorageDirectory()
            server = LocalFileServer(port, rootDir)
            server!!.start()
            val ip = getLocalIpAddress() ?: "localhost"
            _uiState.value = _uiState.value.copy(
                isRunning = true,
                serverUrl = "http://$ip:$port",
                error = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Failed to start server: ${e.message}")
        }
    }

    fun stopServer() {
        server?.stop()
        server = null
        _uiState.value = _uiState.value.copy(isRunning = false, serverUrl = "")
    }

    private fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress
        } catch (e: Exception) { null }
    }

    override fun onCleared() {
        super.onCleared()
        stopServer()
    }
}
