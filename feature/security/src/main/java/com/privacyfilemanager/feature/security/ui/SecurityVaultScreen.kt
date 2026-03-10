package com.privacyfilemanager.feature.security.ui

import androidx.compose.foundation.clickable
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
    var backupActionTarget by remember { mutableStateOf<String?>(null) }
    var showRestoreConfirm by remember { mutableStateOf(false) }

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

            // ── About Security ────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "About Security",
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
                        Column {
                            Text(
                                "Your data is protected by AES-256-GCM encryption backed by Android Keystore (hardware-level).",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "File encryption features coming soon.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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
                        modifier = Modifier.clickable { backupActionTarget = name },
                        headlineContent = { Text(name, style = MaterialTheme.typography.bodySmall) },
                        supportingContent = { Text("Tap to restore or delete", style = MaterialTheme.typography.labelSmall) },
                        leadingContent = {
                            Icon(Icons.Default.FolderZip, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = {
                                    backupActionTarget = name
                                    showRestoreConfirm = true
                                }) {
                                    Icon(Icons.Default.Restore, "Restore", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteBackup(name) }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // ── Backup action dialogs ────────────────────────────────────────────
    if (showRestoreConfirm && backupActionTarget != null) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false; backupActionTarget = null },
            icon = { Icon(Icons.Default.Restore, null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Restore Backup?") },
            text = { Text("This will replace current app data with:\n${backupActionTarget}\n\nRestart the app after restore to apply changes.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.restoreBackup(backupActionTarget!!)
                    showRestoreConfirm = false
                    backupActionTarget = null
                }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirm = false; backupActionTarget = null }) { Text("Cancel") }
            }
        )
    }

    // ── PIN set dialog ─────────────────────────────────────────────────────
    if (showPinDialog) {
        var pin by remember { mutableStateOf("") }
        var confirmPin by remember { mutableStateOf("") }
        val pinsMatch = pin == confirmPin && pin.length >= 4
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set PIN") },
            text = {
                Column {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        label = { Text("Enter PIN (min. 4 digits)") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        label = { Text("Confirm PIN") },
                        isError = confirmPin.isNotEmpty() && pin != confirmPin,
                        supportingText = {
                            if (confirmPin.isNotEmpty() && pin != confirmPin) {
                                Text("PINs don't match")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (pinsMatch) {
                            viewModel.setPin(pin)
                            showPinDialog = false
                        }
                    },
                    enabled = pinsMatch
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
