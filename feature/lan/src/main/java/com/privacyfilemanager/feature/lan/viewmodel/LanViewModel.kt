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
            // BUG 1 FIX: Guard against missing storage permissions before starting server
            val hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            
            if (!hasPermission) {
                _uiState.value = _uiState.value.copy(error = "Storage permission required to start LAN server")
                return
            }

            val port = _uiState.value.port
            val rootDir = Environment.getExternalStorageDirectory()
            server = LocalFileServer(port, rootDir, context)
            server!!.start()
            val ip = getLocalIpAddress()
            if (ip == null) {
                _uiState.value = _uiState.value.copy(error = "No active Wi-Fi or LAN connection found.")
                server!!.stop()
                server = null
                return
            }
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
            val interfaces = NetworkInterface.getNetworkInterfaces().toList()
            
            // Priority 1: wlan (Wi-Fi), eth (Ethernet), ap (Hotspot)
            val primaryIp = interfaces
                .filter { it.isUp && !it.isLoopback }
                .filter { it.name.startsWith("wlan") || it.name.startsWith("eth") || it.name.startsWith("ap") }
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress
                
            if (primaryIp != null) return primaryIp
            
            // Priority 2: Fallback to any valid non-cellular/non-VPN IPv4
            interfaces
                .filter { it.isUp && !it.isLoopback }
                .filter { !it.name.startsWith("rmnet") && !it.name.startsWith("tun") && !it.name.startsWith("ppp") }
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
