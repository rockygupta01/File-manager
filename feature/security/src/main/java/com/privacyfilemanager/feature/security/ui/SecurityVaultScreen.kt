package com.privacyfilemanager.feature.security.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privacyfilemanager.feature.security.viewmodel.SecurityVaultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityVaultScreen(
    onNavigateBack: () -> Unit,
    isDarkTheme: Boolean = false,
    onToggleTheme: (Boolean) -> Unit = {},
    viewModel: SecurityVaultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security & Vault") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "App Lock Settings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            ListItem(
                headlineContent = { Text("Enable PIN Lock") },
                supportingContent = { Text("Require a PIN to open the app") },
                trailingContent = {
                    Switch(
                        checked = uiState.isLockEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) showPinDialog = true
                            else viewModel.removeLock()
                        }
                    )
                }
            )
            
            ListItem(
                headlineContent = { Text("Enable Biometrics") },
                supportingContent = { Text("Use Fingerprint/Face to unlock") },
                trailingContent = {
                    Switch(
                        checked = uiState.isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.toggleBiometric(enabled)
                        },
                        enabled = uiState.isLockEnabled
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text("Dark Theme") },
                supportingContent = { Text("Switch between light and dark mode") },
                leadingContent = {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.DarkMode
                        else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme(it) }
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Secure Vault",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Vault functionality is available internally for encrypted files. Your files are encrypted using AES-256-GCM and stored securely on the device.")
                }
            }
        }
    }

    if (showPinDialog) {
        var pin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set PIN") },
            text = {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    label = { Text("Enter PIN") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (pin.isNotBlank()) {
                            viewModel.setPin(pin)
                            showPinDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
