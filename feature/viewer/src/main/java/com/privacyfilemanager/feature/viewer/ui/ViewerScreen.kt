package com.privacyfilemanager.feature.viewer.ui

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.privacyfilemanager.core.common.util.FileCategory
import com.privacyfilemanager.feature.viewer.viewmodel.ViewerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    onNavigateBack: () -> Unit,
    viewModel: ViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = viewModel.fileName,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Share
                    IconButton(onClick = {
                        uiState.file?.let { file ->
                            try {
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "*/*"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share"))
                            } catch (_: Exception) {}
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    // Open with
                    IconButton(onClick = {
                        uiState.file?.let { file ->
                            try {
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "*/*")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Open with"))
                            } catch (_: Exception) {}
                        }
                    }) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Open with")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (viewModel.category == FileCategory.IMAGE || viewModel.category == FileCategory.VIDEO) Color.Black else MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                uiState.file != null -> {
                    ContentViewer(
                        category = viewModel.category, 
                        file = uiState.file!!, 
                        textContent = uiState.textContent,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ContentViewer(category: FileCategory, file: File, textContent: String?, viewModel: ViewerViewModel) {
    when (category) {
        FileCategory.IMAGE -> {
            ZoomableBox {
                AsyncImage(
                    model = file,
                    contentDescription = file.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
        FileCategory.VIDEO -> AdvancedVideoPlayer(viewModel.getPlayer(file))
        FileCategory.AUDIO -> {
            MediaPlayer(file, viewModel)
        }
        FileCategory.PDF -> {
            PdfViewer(file)
        }
        FileCategory.TEXT, FileCategory.CODE -> {
            TextViewer(textContent ?: "")
        }
        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No built-in viewer for this file type.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun TextViewer(text: String) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        ZoomableBox {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Floating Copy Button
        FloatingActionButton(
            onClick = {
                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                android.widget.Toast.makeText(context, "Text copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy all text")
        }
    }
}

fun android.content.Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun MediaPlayer(file: File, viewModel: ViewerViewModel) {
    val context = LocalContext.current
    val exoPlayer = viewModel.getPlayer(file)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                val activity = context.findActivity()
                if (activity == null || !activity.isChangingConfigurations) {
                    exoPlayer.pause()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        onRelease = { view ->
            view.player = null
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun PdfViewer(file: File) {
    var renderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(file) {
        withContext(Dispatchers.IO) {
            try {
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(pfd)
                renderer = pdfRenderer
                pageCount = pdfRenderer.pageCount
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            renderer?.close()
        }
    }

    if (pageCount > 0 && renderer != null) {
        var currentScale by remember { mutableFloatStateOf(1f) }
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            ZoomableBox(onScaleChanged = { currentScale = it }) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pageCount) { index ->
                        PdfPage(renderer!!, index, currentScale)
                    }
                }
            }

            // Floating Page Indicator
            val firstVisiblePage = remember { derivedStateOf { listState.firstVisibleItemIndex + 1 } }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${firstVisiblePage.value} / $pageCount",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PdfPage(renderer: PdfRenderer, pageIndex: Int, currentScale: Float = 1f) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Debounce or bucket scale to avoid excessive re-rendering.
    val targetRenderScale = if (currentScale > 2.5f) 3f else if (currentScale > 1.5f) 2f else 1f

    LaunchedEffect(pageIndex, targetRenderScale) {
        withContext(Dispatchers.IO) {
            // Add synchronized block to prevent PdfRenderer "Current page not closed" exception.
            synchronized(renderer) {
                try {
                    val page = renderer.openPage(pageIndex)
                    // A4 size roughly 210x297mm. Multiply by 2 * targetRenderScale for sharp zoom
                    val width = (page.width * 2 * targetRenderScale).toInt()
                    val height = (page.height * 2 * targetRenderScale).toInt()
                    val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    // white background
                    bm.eraseColor(android.graphics.Color.WHITE)
                    page.render(bm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    bitmap = bm
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Page $pageIndex",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentScale = ContentScale.FillWidth
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
