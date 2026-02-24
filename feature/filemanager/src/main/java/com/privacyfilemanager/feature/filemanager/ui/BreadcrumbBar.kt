package com.privacyfilemanager.feature.filemanager.ui

import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * #6 — Breadcrumb navigation bar.
 * Shows scrollable path segments. Tapping any segment navigates directly to that level.
 *
 * Example: 📱 Storage > DCIM > Camera
 */
@Composable
fun BreadcrumbBar(
    currentPath: String,
    onSegmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentPath.isEmpty()) return

    val rootPath = Environment.getExternalStorageDirectory().absolutePath
    val segments = buildBreadcrumbSegments(currentPath, rootPath)
    if (segments.isEmpty()) return

    val scrollState = rememberScrollState()

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            segments.forEachIndexed { index, (label, path) ->
                val isLast = index == segments.lastIndex
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                    color = if (isLast)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = if (!isLast) Modifier.clickable { onSegmentClick(path) } else Modifier
                )
                if (!isLast) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(10.dp)
                    )
                }
            }
        }
    }
}

/** Builds ordered list of (label, absolutePath) pairs from root to current path */
private fun buildBreadcrumbSegments(
    currentPath: String,
    rootPath: String
): List<Pair<String, String>> {
    if (!currentPath.startsWith(rootPath)) return listOf("Storage" to rootPath)

    val relative = currentPath.removePrefix(rootPath).trimStart('/')
    val segments = mutableListOf("Storage" to rootPath)

    if (relative.isEmpty()) return segments

    var accumulated = rootPath
    relative.split('/').filter { it.isNotEmpty() }.forEach { part ->
        accumulated = "$accumulated/$part"
        segments.add(part to accumulated)
    }
    return segments
}
