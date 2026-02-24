package com.rocky.filemanager.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.privacyfilemanager.feature.filemanager.ui.FileManagerScreen
import com.privacyfilemanager.feature.search.ui.SearchScreen
import com.privacyfilemanager.feature.security.ui.SecurityVaultScreen
import com.privacyfilemanager.feature.storage.ui.StorageAnalyzerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "filemanager") {
        composable("filemanager") {
            FileManagerScreen(
                onNavigateToStorage = { navController.navigate("storage") },
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToSettings = { navController.navigate("security") }
            )
        }
        composable("storage") {
            StorageAnalyzerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("security") {
            SecurityVaultScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("search") {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PermissionScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Storage Access Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app is a fully offline file manager. It needs full access to your device storage to manage files.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}
