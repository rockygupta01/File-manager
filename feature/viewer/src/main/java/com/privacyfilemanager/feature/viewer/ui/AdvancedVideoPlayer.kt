package com.privacyfilemanager.feature.viewer.ui

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AdvancedVideoPlayer(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
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
    
    var showOverlay by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    setShowSubtitleButton(true)
                    setShowNextButton(false) 
                    setShowPreviousButton(false)
                    setKeepScreenOn(true)
                }
            },
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    var startX = 0f
                    var startY = 0f
                    var isAdjustingVolume = false
                    var isAdjustingBrightness = false

                    detectDragGestures(
                        onDragStart = { offset ->
                            startX = offset.x
                            startY = offset.y
                            // Divide screen in half
                            val isRightSide = startX > size.width / 2
                            isAdjustingVolume = isRightSide
                            isAdjustingBrightness = !isRightSide
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val deltaY = dragAmount.y
                            
                            if (isAdjustingVolume) {
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
                        }
                    )
                }
        )
    }
}
