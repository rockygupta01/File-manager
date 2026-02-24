package com.rocky.filemanager

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.privacyfilemanager.core.ui.theme.PrivacyFileManagerTheme
import com.rocky.filemanager.navigation.AppNavigation
import com.rocky.filemanager.navigation.PermissionScreen
import com.rocky.filemanager.ui.OnboardingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var hasStoragePermission by mutableStateOf(false)
    private var showOnboarding by mutableStateOf(false)  // #1
    private var isDarkTheme by mutableStateOf<Boolean?>(null)  // #22: null = follow system

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        hasStoragePermission = checkStoragePermission()
    }

    private val legacyPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasStoragePermission = permissions.values.all { it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // #1: Show onboarding only on first launch
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        showOnboarding = !prefs.getBoolean("onboarding_done", false)

        hasStoragePermission = checkStoragePermission()

        setContent {
            // #22: Dark/Light theme — persisted to SharedPrefs
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            // Read persisted choice (null = not set = follow system)
            val savedDark = prefs.getBoolean("is_dark_theme", systemDark)
            if (isDarkTheme == null) isDarkTheme = savedDark
            val effectiveDark = isDarkTheme ?: systemDark

            PrivacyFileManagerTheme(darkTheme = effectiveDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        showOnboarding -> OnboardingScreen(
                            onGetStarted = {
                                prefs.edit().putBoolean("onboarding_done", true).apply()
                                showOnboarding = false
                            }
                        )
                        !hasStoragePermission -> PermissionScreen(
                            onRequestPermission = { requestStoragePermission() }
                        )
                        else -> AppNavigation(
                            isDarkTheme = effectiveDark,
                            onToggleTheme = { dark ->
                                isDarkTheme = dark
                                prefs.edit().putBoolean("is_dark_theme", dark).apply()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasStoragePermission = checkStoragePermission()
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // Pre-R devices use legacy permissions requested at install
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            storagePermissionLauncher.launch(intent)
        } else {
            legacyPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
}
