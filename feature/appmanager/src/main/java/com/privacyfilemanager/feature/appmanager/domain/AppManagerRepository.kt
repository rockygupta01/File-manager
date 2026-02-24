package com.privacyfilemanager.feature.appmanager.domain

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import com.privacyfilemanager.core.common.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class AppItem(
    val name: String,
    val packageName: String,
    val versionName: String,
    val icon: Drawable?,
    val apkPath: String,
    val sizeBytes: Long,
    val isSystemApp: Boolean
)

@Singleton
class AppManagerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getInstalledApps(includeSystemApps: Boolean = false): Flow<List<AppItem>> = flow {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        
        val apps = packages.mapNotNull { packageInfo ->
            val appInfo = packageInfo.applicationInfo ?: return@mapNotNull null
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            
            if (!includeSystemApps && isSystem) return@mapNotNull null

            val file = File(appInfo.sourceDir)
            
            AppItem(
                name = pm.getApplicationLabel(appInfo).toString(),
                packageName = appInfo.packageName,
                versionName = packageInfo.versionName ?: "Unknown",
                icon = pm.getApplicationIcon(appInfo),
                apkPath = appInfo.sourceDir,
                sizeBytes = file.length(),
                isSystemApp = isSystem
            )
        }.sortedBy { it.name.lowercase() }
        
        emit(apps)
    }.flowOn(Dispatchers.IO)

    fun backupApp(app: AppItem, destinationDir: File): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }
            val sourceFile = File(app.apkPath)
            val destFile = File(destinationDir, "${app.name}_${app.versionName}.apk")
            
            sourceFile.copyTo(destFile, overwrite = true)
            emit(Result.Success(destFile.absolutePath))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to backup app"))
        }
    }.flowOn(Dispatchers.IO)

    fun getUninstallIntent(packageName: String): Intent {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }
}
