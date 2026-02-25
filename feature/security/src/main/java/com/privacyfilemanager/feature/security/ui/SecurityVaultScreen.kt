package com.privacyfilemanager.feature.security.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    LaunchedEffect(Unit) {
        viewModel.loadBackups()
    }

    // Show a snackbar when backup message arrives
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.backupMessage) {
        uiState.backupMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBackupMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // ── App Lock ───────────────────────────────────────────────────
            item {
                Text(
                    text = "App Lock",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                ListItem(
                    headlineContent = { Text("Enable PIN Lock") },
                    supportingContent = { Text("Require a PIN to open the app") },
                    leadingContent = { Icon(Icons.Default.Pin, null) },
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
            }
            item {
                ListItem(
                    headlineContent = { Text("Enable Biometrics") },
                    supportingContent = { Text("Use Fingerprint/Face to unlock") },
                    leadingContent = { Icon(Icons.Default.Fingerprint, null) },
                    trailingContent = {
                        Switch(
                            checked = uiState.isBiometricEnabled,
                            onCheckedChange = { enabled -> viewModel.toggleBiometric(enabled) },
                            enabled = uiState.isLockEnabled
                        )
                    }
                )
            }

            // ── Appearance ─────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
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
            }

            // ── Secure Vault ────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Secure Vault",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp).padding(end = 12.dp)
                        )
                        Text(
                            "Files can be encrypted using AES-256-GCM and stored securely on the device. " +
                                "Encryption is managed via the Android Keystore (hardware-backed).",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ── Backup & Restore ────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Local Backup & Restore",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Backups are saved to app-private storage only — never uploaded anywhere.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Button(
                    onClick = { viewModel.createBackup() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isBackupInProgress
                ) {
                    if (uiState.isBackupInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Creating Backup…")
                    } else {
                        Icon(Icons.Default.Backup, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Create Backup Now")
                    }
                }
            }
            if (uiState.availableBackups.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Saved Backups (${uiState.availableBackups.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(uiState.availableBackups) { name ->
                    ListItem(
                        headlineContent = { Text(name, style = MaterialTheme.typography.bodySmall) },
                        leadingContent = {
                            Icon(Icons.Default.FolderZip, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                }
            }
        }
    }

    // ── PIN set dialog ─────────────────────────────────────────────────────
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
                    label = { Text("Enter PIN (min. 4 digits)") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (pin.length >= 4) {
                            viewModel.setPin(pin)
                            showPinDialog = false
                        }
                    },
                    enabled = pin.length >= 4
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
