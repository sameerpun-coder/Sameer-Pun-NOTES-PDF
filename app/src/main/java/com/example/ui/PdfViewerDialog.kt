package com.example.ui

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NotePdf
import com.example.data.SlideItem
import com.example.data.SubjectCategory
import com.example.util.PdfFileManager
import com.example.util.SlidePresentationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class ReaderMode {
    DOCUMENT_SCROLL,
    OPEN_SOURCE_VIEWER,
    SLIDE_PRESENTATION
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PdfViewerDialog(
    note: NotePdf,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val subjectCat = remember(note.subject) { SubjectCategory.fromTitle(note.subject) }

    // Check if the document is a slide deck or standard PDF
    val isDefaultSlides = note.docType.equals("SLIDES", ignoreCase = true) ||
            note.title.contains("slide", ignoreCase = true) ||
            note.title.contains("presentation", ignoreCase = true)

    var currentMode by remember {
        mutableStateOf(if (isDefaultSlides) ReaderMode.SLIDE_PRESENTATION else ReaderMode.DOCUMENT_SCROLL)
    }

    var isFullscreen by remember { mutableStateOf(false) }
    var showPresenterNotes by remember { mutableStateOf(false) }

    // Parse structured slides if available
    val parsedSlides: List<SlideItem> = remember(note.slidesDataJson) {
        if (note.slidesDataJson.isNotBlank()) {
            SlidePresentationHelper.deserializeSlides(note.slidesDataJson)
        } else {
            emptyList()
        }
    }

    val totalPages = remember(note.pageCount, parsedSlides) {
        if (parsedSlides.isNotEmpty()) parsedSlides.size else note.pageCount.coerceAtLeast(1)
    }

    // Bitmap cache for rendered PDF pages
    val renderedBitmaps = remember { mutableStateMapOf<Int, Bitmap>() }
    var fileExists by remember { mutableStateOf(true) }

    val file = remember(note.filePath) { File(note.filePath) }

    // Pager state for slide mode
    val pagerState = rememberPagerState(pageCount = { totalPages })

    // Zoom state for document reading mode
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.8f, 3.5f)
        offset += panChange
    }

    // Lazy list state for continuous document scroll
    val listState = rememberLazyListState()

    // Render pages asynchronously
    LaunchedEffect(file) {
        withContext(Dispatchers.IO) {
            if (!file.exists()) {
                fileExists = false
                return@withContext
            }
            fileExists = true
            // Preload first 3 pages
            for (i in 0 until minOf(3, totalPages)) {
                val bmp = PdfFileManager.renderPageToBitmap(file, i, 1200)
                if (bmp != null) {
                    withContext(Dispatchers.Main) {
                        renderedBitmaps[i] = bmp
                    }
                }
            }
        }
    }

    // Lazy loader for requested pages
    fun loadPageIfNeeded(pageIdx: Int) {
        if (!renderedBitmaps.containsKey(pageIdx) && file.exists()) {
            coroutineScope.launch(Dispatchers.IO) {
                val bmp = PdfFileManager.renderPageToBitmap(file, pageIdx, 1200)
                if (bmp != null) {
                    withContext(Dispatchers.Main) {
                        renderedBitmaps[pageIdx] = bmp
                    }
                }
            }
        }
    }

    var isDialogActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isDialogActive = true
    }

    val requestClose: () -> Unit = {
        isDialogActive = false
        coroutineScope.launch {
            delay(180)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = requestClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        AnimatedVisibility(
            visible = isDialogActive,
            enter = scaleIn(
                initialScale = 0.90f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.90f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(animationSpec = tween(150))
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("pdf_viewer_scaffold"),
                topBar = {
                    if (!isFullscreen) {
                        TopAppBar(
                            title = {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            color = when (currentMode) {
                                                ReaderMode.SLIDE_PRESENTATION -> MaterialTheme.colorScheme.primaryContainer
                                                ReaderMode.OPEN_SOURCE_VIEWER -> MaterialTheme.colorScheme.tertiaryContainer
                                                ReaderMode.DOCUMENT_SCROLL -> MaterialTheme.colorScheme.secondaryContainer
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = when (currentMode) {
                                                    ReaderMode.SLIDE_PRESENTATION -> "SLIDES"
                                                    ReaderMode.OPEN_SOURCE_VIEWER -> "PDF.JS"
                                                    ReaderMode.DOCUMENT_SCROLL -> "PDF"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${note.subject} • ${note.unit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = requestClose) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                                }
                            },
                        actions = {
                            // Toggle Mode: Document vs Open-Source vs Slides
                            FilledTonalIconButton(
                                onClick = {
                                    currentMode = when (currentMode) {
                                        ReaderMode.DOCUMENT_SCROLL -> ReaderMode.OPEN_SOURCE_VIEWER
                                        ReaderMode.OPEN_SOURCE_VIEWER -> ReaderMode.SLIDE_PRESENTATION
                                        ReaderMode.SLIDE_PRESENTATION -> ReaderMode.DOCUMENT_SCROLL
                                    }
                                    scale = 1f
                                    offset = Offset.Zero
                                },
                                modifier = Modifier.testTag("toggle_reader_mode_button")
                            ) {
                                Icon(
                                    imageVector = when (currentMode) {
                                        ReaderMode.DOCUMENT_SCROLL -> Icons.Default.Description
                                        ReaderMode.OPEN_SOURCE_VIEWER -> Icons.Default.MenuBook
                                        ReaderMode.SLIDE_PRESENTATION -> Icons.Default.Slideshow
                                    },
                                    contentDescription = "Toggle Reader Mode (Native, Open-Source, Slides)"
                                )
                            }

                            // Fullscreen toggle
                            IconButton(onClick = { isFullscreen = true }) {
                                Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen")
                            }

                            // Share document
                            IconButton(onClick = { PdfFileManager.sharePdf(context, file, note.title) }) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }

                            // Open in external PowerPoint or PDF viewer
                            IconButton(onClick = { PdfFileManager.openPdfExternally(context, file) }) {
                                Icon(Icons.Default.OpenInNew, contentDescription = "Open externally")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            },
            bottomBar = {
                if (!isFullscreen) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (currentMode == ReaderMode.SLIDE_PRESENTATION) {
                            // Slide Navigation Controls
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Previous Slide Button
                                    FilledTonalButton(
                                        onClick = {
                                            if (pagerState.currentPage > 0) {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                }
                                            }
                                        },
                                        enabled = pagerState.currentPage > 0,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Prev")
                                    }

                                    // Slide Indicator Badge
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = "Slide ${pagerState.currentPage + 1} of $totalPages",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }

                                    // Next Slide Button
                                    FilledTonalButton(
                                        onClick = {
                                            if (pagerState.currentPage < totalPages - 1) {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                }
                                            }
                                        },
                                        enabled = pagerState.currentPage < totalPages - 1,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Next")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                                    }
                                }

                                // Quick Scrubber Slider
                                if (totalPages > 1) {
                                    Slider(
                                        value = pagerState.currentPage.toFloat(),
                                        onValueChange = { targetPage ->
                                            coroutineScope.launch {
                                                pagerState.scrollToPage(targetPage.toInt())
                                            }
                                        },
                                        valueRange = 0f..(totalPages - 1).toFloat(),
                                        steps = (totalPages - 2).coerceAtLeast(0),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFF0F172A),
                                            activeTrackColor = Color(0xFF0F172A),
                                            inactiveTrackColor = Color(0xFFCBD5E1)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Speaker notes toggle if parsed slides exist
                                val currentSlideItem = parsedSlides.getOrNull(pagerState.currentPage)
                                if (currentSlideItem != null && (currentSlideItem.presenterNotes.isNotBlank() || currentSlideItem.formulaOrCallout.isNotBlank())) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        FilterChip(
                                            selected = showPresenterNotes,
                                            onClick = { showPresenterNotes = !showPresenterNotes },
                                            leadingIcon = {
                                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                                            },
                                            label = {
                                                Text(if (showPresenterNotes) "Hide Notes & Formula" else "Show Notes & Formula")
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            // PDF Document Reading Controls (Zoom & Jump)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { scale = (scale - 0.25f).coerceAtLeast(0.8f) }
                                    ) {
                                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out")
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                                    ) {
                                        Text(
                                            text = "${(scale * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { scale = (scale + 0.25f).coerceAtMost(3.5f) }
                                    ) {
                                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in")
                                    }

                                    IconButton(
                                        onClick = {
                                            scale = 1f
                                            offset = Offset.Zero
                                        }
                                    ) {
                                        Icon(Icons.Default.ZoomOutMap, contentDescription = "Reset Zoom")
                                    }
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "$totalPages Pages",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF1F5F9))
            ) {
                if (!fileExists) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "PDF file was moved or deleted.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (currentMode == ReaderMode.SLIDE_PRESENTATION) {
                    // ==========================================
                    // PRESENTATION SLIDE MODE (16:9 Widescreen)
                    // ==========================================
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { pageIndex ->
                            loadPageIfNeeded(pageIndex)

                            val slideItem = parsedSlides.getOrNull(pageIndex)
                            val pageBitmap = renderedBitmaps[pageIndex]

                            var slideScale by remember(pageIndex) { mutableFloatStateOf(1f) }
                            var slideOffset by remember(pageIndex) { mutableStateOf(Offset.Zero) }
                            val animatedSlideScale by animateFloatAsState(
                                targetValue = slideScale,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                label = "slide_scale_$pageIndex"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .pointerInput(pageIndex) {
                                        detectTapGestures(
                                            onDoubleTap = { tapOffset ->
                                                if (slideScale > 1.15f) {
                                                    slideScale = 1f
                                                    slideOffset = Offset.Zero
                                                } else {
                                                    val centerX = size.width / 2f
                                                    val centerY = size.height / 2f
                                                    slideScale = 2.2f
                                                    slideOffset = Offset(
                                                        x = (centerX - tapOffset.x) * 1.2f,
                                                        y = (centerY - tapOffset.y) * 1.2f
                                                    )
                                                }
                                            }
                                        )
                                    }
                                    .pointerInput(pageIndex) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            val newScale = (slideScale * zoom).coerceIn(1f, 4f)
                                            slideScale = newScale
                                            if (newScale > 1.05f) {
                                                slideOffset += pan
                                            } else {
                                                slideOffset = Offset.Zero
                                            }
                                        }
                                    }
                                    .graphicsLayer {
                                        scaleX = animatedSlideScale
                                        scaleY = animatedSlideScale
                                        translationX = if (animatedSlideScale > 1.02f) slideOffset.x else 0f
                                        translationY = if (animatedSlideScale > 1.02f) slideOffset.y else 0f
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (slideItem != null) {
                                    // Render structured vector slide presentation card
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f)
                                            .clip(RoundedCornerShape(16.dp)),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(20.dp)
                                        ) {
                                            // Top header in slide card
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Surface(
                                                    color = Color(0xFFF1F5F9),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = "${note.subject} • ${note.unit}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF0F172A),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }

                                                Text(
                                                    text = "Slide ${pageIndex + 1} / $totalPages",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF64748B)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Slide Title
                                            Text(
                                                text = slideItem.title,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Slide Bullets
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .verticalScroll(rememberScrollState()),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                for (bullet in slideItem.bullets) {
                                                    Row(
                                                        verticalAlignment = Alignment.Top,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .padding(top = 6.dp)
                                                                .size(6.dp)
                                                                .background(Color(0xFF0F172A), CircleShape)
                                                        )
                                                        Text(
                                                            text = bullet,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = Color(0xFF334155)
                                                        )
                                                    }
                                                }
                                            }

                                            // Key Formula / Callout Box
                                            if (slideItem.formulaOrCallout.isNotBlank()) {
                                                Surface(
                                                    color = Color(0xFFF8FAFC),
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(10.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .width(4.dp)
                                                                .height(24.dp)
                                                                .background(Color(0xFF0F172A), RoundedCornerShape(2.dp))
                                                        )
                                                        Text(
                                                            text = slideItem.formulaOrCallout,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (pageBitmap != null) {
                                    // Render 16:9 PDF page bitmap
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp)),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                    ) {
                                        Image(
                                            bitmap = pageBitmap.asImageBitmap(),
                                            contentDescription = "Slide ${pageIndex + 1}",
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color(0xFF0F172A))
                                    }
                                }
                            }
                        }

                        // Presenter Notes Drawer overlay
                        val activeSlide = parsedSlides.getOrNull(pagerState.currentPage)
                        AnimatedVisibility(
                            visible = showPresenterNotes && activeSlide != null,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Presenter & Exam Study Takeaways",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        IconButton(
                                            onClick = { showPresenterNotes = false },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Close notes", tint = Color.White)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeSlide?.presenterNotes ?: "No speaker notes for this slide.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }
                } else if (currentMode == ReaderMode.OPEN_SOURCE_VIEWER) {
                    // ==========================================
                    // OPEN-SOURCE FRAMEWORK ENGINE (Mozilla PDF.js)
                    // ==========================================
                    OpenSourcePdfWebViewer(
                        file = file,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // ==========================================
                    // CONTINUOUS DOCUMENT SCROLL MODE (Rectangular Pages)
                    // ==========================================
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(totalPages) { pageIdx ->
                            loadPageIfNeeded(pageIdx)
                            val bmp = renderedBitmaps[pageIdx]

                            PdfRectangularPageItem(
                                pageIndex = pageIdx,
                                bitmap = bmp,
                                totalPages = totalPages
                            )
                        }
                    }
                }

                // Fullscreen Exit Overlay Button
                if (isFullscreen) {
                    FilledTonalIconButton(
                        onClick = { isFullscreen = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen")
                    }
                }
            }
        }
    }
}
}

@Composable
fun PdfRectangularPageItem(
    pageIndex: Int,
    bitmap: Bitmap?,
    totalPages: Int
) {
    var pageScale by remember { mutableFloatStateOf(1f) }
    var pageOffset by remember { mutableStateOf(Offset.Zero) }

    val animatedScale by animateFloatAsState(
        targetValue = pageScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "page_scale_anim_$pageIndex"
    )

    // Standard A4 aspect ratio is 1f / 1.4142f (~0.707)
    val pageRatio = remember(bitmap) {
        if (bitmap != null && bitmap.height > 0) {
            (bitmap.width.toFloat() / bitmap.height.toFloat()).coerceIn(0.5f, 2.0f)
        } else {
            1f / 1.4142f // A4 paper ratio
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(if (animatedScale > 1f) 1f else 0.96f)
                .aspectRatio(pageRatio)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                .pointerInput(pageIndex) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            if (pageScale > 1.15f) {
                                // Double tap out: return to normal 1x
                                pageScale = 1f
                                pageOffset = Offset.Zero
                            } else {
                                // Double tap in: zoom in to 2.5x centered on tap
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                pageScale = 2.5f
                                pageOffset = Offset(
                                    x = (centerX - tapOffset.x) * 1.5f,
                                    y = (centerY - tapOffset.y) * 1.5f
                                )
                            }
                        }
                    )
                }
                .pointerInput(pageIndex) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            val pointerCount = event.changes.size

                            if (pointerCount >= 2) {
                                // Pinch-to-zoom with 2 fingers
                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()
                                if (zoomChange != 1f || panChange != Offset.Zero) {
                                    val newScale = (pageScale * zoomChange).coerceIn(1f, 4.5f)
                                    pageScale = newScale
                                    if (newScale > 1.05f) {
                                        pageOffset += panChange
                                    } else {
                                        pageOffset = Offset.Zero
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            } else if (pointerCount == 1 && pageScale > 1.05f) {
                                // Single-finger pan ONLY when zoomed in
                                val panChange = event.calculatePan()
                                if (panChange != Offset.Zero) {
                                    pageOffset += panChange
                                    event.changes.forEach { it.consume() }
                                }
                            }
                            // When pointerCount == 1 and pageScale <= 1.05f:
                            // NOTHING is consumed! LazyColumn receives all scroll down/up events completely unhindered!
                        } while (event.changes.any { it.pressed })
                    }
                }
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    translationX = if (animatedScale > 1.02f) pageOffset.x else 0f
                    translationY = if (animatedScale > 1.02f) pageOffset.y else 0f
                    clip = false
                },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            if (bitmap != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Page ${pageIndex + 1} of $totalPages",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // Page number badge on bottom right of rectangular sheet
                    Surface(
                        color = Color(0xCC0F172A),
                        shape = RoundedCornerShape(topStart = 6.dp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "${pageIndex + 1} / $totalPages",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Reset Zoom button when page is zoomed in
                    if (animatedScale > 1.05f) {
                        Surface(
                            onClick = {
                                pageScale = 1f
                                pageOffset = Offset.Zero
                            },
                            color = Color(0xDD0F172A),
                            shape = RoundedCornerShape(bottomStart = 8.dp),
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomOutMap,
                                    contentDescription = "Reset Zoom",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Reset Zoom",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF0F172A),
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            text = "Loading page ${pageIndex + 1}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Embedded Open-Source PDF Framework Engine (Mozilla PDF.js).
 * Renders documents using the battle-tested open-source Mozilla PDF.js library in Android WebView
 * with full continuous vertical scrolling, multi-touch pinch zoom, and double-tap scaling.
 */
@Composable
fun OpenSourcePdfWebViewer(
    file: File,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }

    val base64Pdf = remember(file) {
        try {
            if (file.exists()) {
                android.util.Base64.encodeToString(file.readBytes(), android.util.Base64.NO_WRAP)
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    val htmlContent = remember(base64Pdf) {
        """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes">
          <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js"></script>
          <style>
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { background: #0f172a; display: flex; flex-direction: column; align-items: center; padding: 12px 8px 80px 8px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
            .pdf-sheet { margin-bottom: 20px; box-shadow: 0 4px 16px rgba(0,0,0,0.4); border-radius: 4px; overflow: hidden; background: white; max-width: 100%; position: relative; }
            canvas { display: block; max-width: 100%; height: auto; }
            #loading { color: #cbd5e1; font-size: 14px; padding: 48px 16px; text-align: center; }
            .sheet-header { background: #1e293b; color: #94a3b8; font-size: 11px; padding: 4px 10px; display: flex; justify-content: space-between; }
          </style>
        </head>
        <body>
          <div id="loading">⚡ Initializing Mozilla PDF.js Reader Engine...</div>
          <div id="viewer"></div>
          <script>
            pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js';
            const base64Data = '$base64Pdf';
            if (base64Data) {
              try {
                const raw = atob(base64Data);
                const uint8Array = new Uint8Array(raw.length);
                for (let i = 0; i < raw.length; i++) {
                  uint8Array[i] = raw.charCodeAt(i);
                }
                pdfjsLib.getDocument({ data: uint8Array }).promise.then(pdf => {
                  const loadingEl = document.getElementById('loading');
                  if (loadingEl) loadingEl.style.display = 'none';
                  const viewer = document.getElementById('viewer');
                  for (let pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
                    pdf.getPage(pageNum).then(page => {
                      const scale = 1.5;
                      const viewport = page.getViewport({ scale: scale });
                      const container = document.createElement('div');
                      container.className = 'pdf-sheet';
                      
                      const header = document.createElement('div');
                      header.className = 'sheet-header';
                      header.innerHTML = '<span>Page ' + page.pageNumber + ' of ' + pdf.numPages + '</span><span>Open-Source Engine</span>';
                      container.appendChild(header);

                      const canvas = document.createElement('canvas');
                      const context = canvas.getContext('2d');
                      canvas.height = viewport.height;
                      canvas.width = viewport.width;
                      const renderContext = {
                        canvasContext: context,
                        viewport: viewport
                      };
                      page.render(renderContext);
                      container.appendChild(canvas);
                      viewer.appendChild(container);
                    });
                  }
                }).catch(err => {
                  document.getElementById('loading').innerText = 'Could not render with PDF.js: ' + err.message;
                });
              } catch (e) {
                document.getElementById('loading').innerText = 'Error parsing document data.';
              }
            } else {
              document.getElementById('loading').innerText = 'PDF document is empty or missing.';
            }
          </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }
                    loadDataWithBaseURL("https://localhost", htmlContent, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text("Loading PDF...", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
