package com.privacyfilemanager.feature.viewer.ui

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AdvancedVideoPlayer(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier,
    fileSize: Long? = null
) {
    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    
    // PiP logic mapping
    val activity = context.findActivity()
    androidx.compose.runtime.DisposableEffect(exoPlayer.isPlaying) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && activity != null) {
            val builder = android.app.PictureInPictureParams.Builder()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(true)
            }
            try {
                activity.setPictureInPictureParams(builder.build())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onDispose {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && activity != null) {
                try {
                    activity.setPictureInPictureParams(
                        android.app.PictureInPictureParams.Builder().setAutoEnterEnabled(false).build()
                    )
                } catch (e: Exception) {}
            }
        }
    }
    
    var showMetadataOverlay by remember { mutableStateOf(false) }
    var resizeMode by remember { mutableStateOf(androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var isLandscape by remember { mutableStateOf(false) }
    var playInBackground by remember { mutableStateOf(false) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner, playInBackground) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                val act = context.findActivity()
                val isEnteringPip = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N && act?.isInPictureInPictureMode == true
                if (!playInBackground && !isEnteringPip && (act == null || !act.isChangingConfigurations)) {
                    exoPlayer.pause()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    androidx.compose.runtime.LaunchedEffect(isLandscape) {
        val act = context.findActivity() ?: return@LaunchedEffect
        act.requestedOrientation = if (isLandscape) {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    setShowSubtitleButton(true)
                    setShowNextButton(false) 
                    setShowPreviousButton(false)
                    setShowFastForwardButton(true)
                    setShowRewindButton(true)
                    setKeepScreenOn(true)
                }
            },
            update = { playerView ->
                playerView.resizeMode = resizeMode
            },
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    var startX = 0f
                    var startY = 0f
                    var isAdjustingVolume = false
                    var isAdjustingBrightness = false
                    var isAdjustingProgress = false

                    detectDragGestures(
                        onDragStart = { offset ->
                            startX = offset.x
                            startY = offset.y
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val deltaX = dragAmount.x
                            val deltaY = dragAmount.y
                            
                            // Determine axis of intent if not already locked
                            if (!isAdjustingVolume && !isAdjustingBrightness && !isAdjustingProgress) {
                                if (kotlin.math.abs(deltaX) > kotlin.math.abs(deltaY)) {
                                    isAdjustingProgress = true
                                } else {
                                    val isRightSide = startX > size.width / 2
                                    isAdjustingVolume = isRightSide
                                    isAdjustingBrightness = !isRightSide
                                }
                            }

                            if (isAdjustingProgress) {
                                // Assume 1px drag = 50ms seek (adjust multiplier as needed)
                                val seekDelta = (deltaX * 50).toLong()
                                val newPos = (exoPlayer.currentPosition + seekDelta).coerceIn(0, exoPlayer.duration)
                                exoPlayer.seekTo(newPos)
                            } else if (isAdjustingVolume) {
                                // Decrease volume when swiping down (positive delta), increase up
                                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                val newVolume = (currentVolume - (deltaY * 0.05f)).toInt().coerceIn(0, maxVolume)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI)
                            } else if (isAdjustingBrightness) {
                                val activity = context.findActivity() ?: return@detectDragGestures
                                val window = activity.window
                                val params = window.attributes
                                // brightness from 0.0 to 1.0
                                var newBrightness = params.screenBrightness - (deltaY * 0.005f)
                                newBrightness = newBrightness.coerceIn(0f, 1f)
                                params.screenBrightness = newBrightness
                                window.attributes = params
                            }
                        },
                        onDragEnd = {
                            isAdjustingVolume = false
                            isAdjustingBrightness = false
                            isAdjustingProgress = false
                        }
                    )
                }
        )
        
        // Top right controls
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            // Audio Track Button
            androidx.compose.material3.IconButton(
                onClick = {
                    val activity = context.findActivity() ?: return@IconButton
                    androidx.media3.ui.TrackSelectionDialogBuilder(
                        activity,
                        "Select Audio Track",
                        exoPlayer,
                        androidx.media3.common.C.TRACK_TYPE_AUDIO
                    ).build().show()
                }
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.Audiotrack,
                    contentDescription = "Audio Tracks",
                    tint = Color.White
                )
            }
            
            // Aspect Ratio Button
            androidx.compose.material3.IconButton(
                onClick = {
                    resizeMode = when (resizeMode) {
                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                        androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        else -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                }
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.AspectRatio,
                    contentDescription = "Aspect Ratio",
                    tint = Color.White
                )
            }
            
            // Screen Rotate Button
            androidx.compose.material3.IconButton(
                onClick = { isLandscape = !isLandscape }
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.ScreenRotation,
                    contentDescription = "Rotate",
                    tint = Color.White
                )
            }
            
            // Background Playback Button
            androidx.compose.material3.IconButton(
                onClick = { playInBackground = !playInBackground }
            ) {
                androidx.compose.material3.Icon(
                    if (playInBackground) Icons.Default.Headset else Icons.Default.HeadsetOff,
                    contentDescription = "Background Playback",
                    tint = if (playInBackground) Color.Green else Color.White
                )
            }
            
            // Info Button
            androidx.compose.material3.IconButton(
                onClick = { showMetadataOverlay = !showMetadataOverlay }
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.Info,
                    contentDescription = "Metadata",
                    tint = Color.White
                )
            }
        }

        // The Metadata Overlay Display
        if (showMetadataOverlay) {
            val format = exoPlayer.videoFormat
            val resolution = if (format != null && format.width > 0 && format.height > 0) "${format.width}x${format.height}" else "Unknown"
            val codec = format?.sampleMimeType ?: "Unknown Codec"
            
            val durationMs = exoPlayer.duration.coerceAtLeast(0)
            val durationStr = if (durationMs > 0) {
                String.format("%02d:%02d:%02d", durationMs / 3600000, (durationMs / 60000) % 60, (durationMs / 1000) % 60)
            } else "Unknown"
            
            val fileSizeStr = fileSize?.let { android.text.format.Formatter.formatFileSize(context, it) } ?: "Unknown Size"
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.Text("Resolution: $resolution", color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    androidx.compose.material3.Text("Format: $codec", color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    if (format?.frameRate ?: 0f > 0f) {
                        androidx.compose.material3.Text("FPS: ${format?.frameRate}", color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    }
                    androidx.compose.material3.Text("Duration: $durationStr", color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    androidx.compose.material3.Text("Size: $fileSizeStr", color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
