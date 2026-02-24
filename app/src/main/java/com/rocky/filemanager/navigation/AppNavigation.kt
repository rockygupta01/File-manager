package com.rocky.filemanager.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.privacyfilemanager.feature.appmanager.ui.AppManagerScreen
import com.privacyfilemanager.feature.archive.ui.ArchiveScreen
import com.privacyfilemanager.feature.filemanager.ui.FileManagerScreen
import com.privacyfilemanager.feature.search.ui.SearchScreen
import com.privacyfilemanager.feature.security.ui.SecurityVaultScreen
import com.privacyfilemanager.feature.storage.ui.StorageAnalyzerScreen
import com.privacyfilemanager.feature.viewer.ui.ViewerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "filemanager") {
        composable("filemanager") {
            FileManagerScreen(
                onNavigateToStorage = { navController.navigate("storage") },
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToSettings = { navController.navigate("security") },
                onNavigateToArchive = { paths, mode ->
                    val encodedPaths = android.net.Uri.encode(paths.joinToString(","))
                    navController.navigate("archive?paths=$encodedPaths&mode=$mode")
                },
                onNavigateToViewer = { path ->
                    val encodedPath = android.net.Uri.encode(path)
                    navController.navigate("viewer?path=$encodedPath")
                },
                onNavigateToAppManager = { navController.navigate("appmanager") }
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
        composable(
            route = "archive?paths={paths}&mode={mode}",
            arguments = listOf(
                navArgument("paths") { type = NavType.StringType; defaultValue = "" },
                navArgument("mode") { type = NavType.StringType; defaultValue = "compress" }
            )
        ) {
            ArchiveScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "viewer?path={path}",
            arguments = listOf(
                navArgument("path") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            ViewerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("appmanager") {
            AppManagerScreen(
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
