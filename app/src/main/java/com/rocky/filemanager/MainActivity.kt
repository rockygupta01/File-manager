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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.privacyfilemanager.core.security.AppLockManager
import com.privacyfilemanager.core.ui.theme.PrivacyFileManagerTheme
import com.rocky.filemanager.navigation.AppNavigation
import com.rocky.filemanager.navigation.PermissionScreen
import com.rocky.filemanager.ui.OnboardingScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appLockManager: AppLockManager

    private var hasStoragePermission by mutableStateOf(false)
    private var showOnboarding by mutableStateOf(false)  // #1
    private var isDarkTheme by mutableStateOf<Boolean?>(null)  // #22: null = follow system
    private var isAppUnlocked by mutableStateOf(false) // tracks whether lock screen has been passed this session

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

        // If lock is not enabled, consider the app already unlocked
        if (!appLockManager.isLockEnabled) {
            isAppUnlocked = true
        }

        setContent {
            // #22: Dark/Light theme — persisted to SharedPrefs
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val savedDark = prefs.getBoolean("is_dark_theme", systemDark)
            if (isDarkTheme == null) isDarkTheme = savedDark
            val effectiveDark = isDarkTheme ?: systemDark

            PrivacyFileManagerTheme(darkTheme = effectiveDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        // ── App Lock Screen ─────────────────────────────────────
                        !isAppUnlocked && appLockManager.isLockEnabled -> {
                            var pinInput by remember { mutableStateOf("") }
                            var attempts by remember { mutableIntStateOf(0) }
                            var errorMsg by remember { mutableStateOf<String?>(null) }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                                Text(
                                    "App Locked",
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Enter your PIN to continue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(24.dp))

                                if (appLockManager.isLockedOut()) {
                                    Text(
                                        "Too many failed attempts. Wait before retrying.",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = pinInput,
                                        onValueChange = { pinInput = it },
                                        label = { Text("PIN") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        singleLine = true,
                                        isError = errorMsg != null,
                                        supportingText = errorMsg?.let { msg ->
                                            { Text(msg, color = MaterialTheme.colorScheme.error) }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            if (appLockManager.verifyPin(pinInput)) {
                                                isAppUnlocked = true
                                                errorMsg = null
                                            } else {
                                                attempts++
                                                pinInput = ""
                                                errorMsg = "Incorrect PIN (attempt $attempts)"
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = pinInput.isNotBlank()
                                    ) {
                                        Text("Unlock")
                                    }
                                }
                            }
                        }

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
