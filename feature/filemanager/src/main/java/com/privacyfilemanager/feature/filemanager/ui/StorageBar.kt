package com.privacyfilemanager.feature.filemanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.privacyfilemanager.core.common.util.FileUtils
import com.privacyfilemanager.core.domain.repository.StorageInfo

/**
 * #16 — Storage usage bar shown at the root folder view.
 */
@Composable
fun StorageBar(
    storageInfo: StorageInfo,
    modifier: Modifier = Modifier
) {
    val usedFraction = if (storageInfo.totalBytes > 0)
        storageInfo.usedBytes.toFloat() / storageInfo.totalBytes.toFloat()
    else 0f

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device Storage",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${FileUtils.formatFileSize(storageInfo.usedBytes)} used of ${FileUtils.formatFileSize(storageInfo.totalBytes)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { usedFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = when {
                    usedFraction > 0.9f -> MaterialTheme.colorScheme.error
                    usedFraction > 0.7f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${FileUtils.formatFileSize(storageInfo.freeBytes)} free",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
