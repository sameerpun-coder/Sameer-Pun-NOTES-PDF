package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeState
import com.example.data.NotePdf
import com.example.data.SubjectCategory
import com.example.data.SyllabusChapter
import com.example.data.SyllabusUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNotesScreen(
    viewModel: NotesViewModel
) {
    val allNotes by viewModel.allNotes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDocType by viewModel.selectedDocType.collectAsState()
    val favoritesOnly by viewModel.favoritesOnly.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val activeViewingNote by viewModel.activeViewingNote.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val targetChapterForAdd by viewModel.targetChapterForAdd.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val editingNote by viewModel.editingNote.collectAsState()
    val deletingNote by viewModel.deletingNote.collectAsState()

    val themeState by viewModel.themeState.collectAsState()
    val showSettingsScreen by viewModel.showSettingsScreen.collectAsState()

    AnimatedContent(
        targetState = showSettingsScreen,
        transitionSpec = {
            if (targetState) {
                // Opening settings: snappy popup scale in + slide in + fade in
                (scaleIn(initialScale = 0.94f, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessHigh)) +
                        slideInVertically(initialOffsetY = { it / 8 }, animationSpec = spring(stiffness = Spring.StiffnessHigh)) +
                        fadeIn(animationSpec = tween(90)))
                    .togetherWith(
                        scaleOut(targetScale = 0.96f, animationSpec = spring(stiffness = Spring.StiffnessHigh)) + fadeOut(animationSpec = tween(70))
                    )
            } else {
                // Closing settings: snappy slide out down + scale out + fade out
                (scaleIn(initialScale = 0.96f, animationSpec = spring(stiffness = Spring.StiffnessHigh)) + fadeIn(animationSpec = tween(90)))
                    .togetherWith(
                        scaleOut(targetScale = 0.94f, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessHigh)) +
                                slideOutVertically(targetOffsetY = { it / 8 }, animationSpec = spring(stiffness = Spring.StiffnessHigh)) +
                                fadeOut(animationSpec = tween(70))
                    )
            }
        },
        label = "settings_popup_anim"
    ) { isSettingsVisible ->
        if (isSettingsVisible) {
            SettingsScreen(
                themeState = themeState,
                onPresetSelected = { viewModel.selectThemePreset(it) },
                onCustomBackgroundSelected = { viewModel.selectCustomBackground(it) },
                onCustomAccentSelected = { viewModel.selectCustomAccent(it) },
                onShowMicroTopicsChanged = { viewModel.toggleShowMicroTopics(it) },
                onCompactDensityChanged = { viewModel.toggleCompactDensity(it) },
                onResetDefaults = { viewModel.resetThemeDefaults() },
                onBack = { viewModel.closeSettings() }
            )
        } else {
            val snackbarHostState = remember { SnackbarHostState() }

            // Expansion states for Subject cards and Unit cards - CLOSED by default as requested
            val expandedSubjects = remember {
                mutableStateMapOf<String, Boolean>()
            }

            val expandedUnits = remember {
                mutableStateMapOf<String, Boolean>()
            }

            // Auto-expand on search
            LaunchedEffect(searchQuery) {
                if (searchQuery.isNotBlank()) {
                    SubjectCategory.entries.forEach { subject ->
                        var subjectHasMatch = false
                        subject.syllabusUnits.forEach { unit ->
                            val unitHasMatch = unit.chapters.any { chapter ->
                                chapter.name.contains(searchQuery, ignoreCase = true) ||
                                chapter.microTopics.any { it.contains(searchQuery, ignoreCase = true) }
                            }
                            if (unitHasMatch) {
                                expandedUnits["${subject.id}_${unit.name}"] = true
                                subjectHasMatch = true
                            }
                        }
                        if (subjectHasMatch) {
                            expandedSubjects[subject.id] = true
                        }
                    }
                }
            }

            LaunchedEffect(snackbarMessage) {
                snackbarMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearSnackbar()
                }
            }

            // Filter notes by docType and favorites
            val visibleNotes = remember(allNotes, selectedDocType, favoritesOnly) {
                allNotes.filter { note ->
                    val matchesFav = !favoritesOnly || note.isFavorite
                    val matchesDocType = selectedDocType == null || selectedDocType == "ALL" || note.docType.equals(selectedDocType, ignoreCase = true)
                    matchesFav && matchesDocType
                }
            }

            Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = themeState.backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeState.onBackgroundColor
                    )
                },
                actions = {
                    // Favorites toggle
                    IconButton(
                        onClick = { viewModel.toggleFavoritesOnly() },
                        modifier = Modifier.testTag("favorites_filter_button")
                    ) {
                        Icon(
                            imageVector = if (favoritesOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorites Only",
                            tint = if (favoritesOnly) Color(0xFFD97706) else themeState.onSurfaceVariant
                        )
                    }

                    // Expand / Collapse all
                    IconButton(
                        onClick = {
                            val anyCollapsed = SubjectCategory.entries.any { expandedSubjects[it.id] != true }
                            SubjectCategory.entries.forEach { subject ->
                                expandedSubjects[subject.id] = anyCollapsed
                                subject.syllabusUnits.forEach { unit ->
                                    expandedUnits["${subject.id}_${unit.name}"] = anyCollapsed
                                }
                            }
                        },
                        modifier = Modifier.testTag("expand_collapse_all_button")
                    ) {
                        val allExpanded = SubjectCategory.entries.all { expandedSubjects[it.id] == true }
                        Icon(
                            imageVector = if (allExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = "Expand/Collapse All",
                            tint = themeState.onBackgroundColor
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier.testTag("settings_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Appearance & Settings",
                            tint = themeState.onBackgroundColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeState.surfaceColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = themeState.primaryColor,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("main_add_note_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note / Slides",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = "Search chapters... (e.g. Kinematics, Dynamics, Optics)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeState.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = themeState.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = themeState.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = themeState.surfaceColor,
                        unfocusedContainerColor = themeState.surfaceColor,
                        focusedBorderColor = themeState.primaryColor,
                        unfocusedBorderColor = themeState.borderColor,
                        focusedTextColor = themeState.onBackgroundColor,
                        unfocusedTextColor = themeState.onBackgroundColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_notes_field")
                )
            }

            // Quick Format Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pdfCount = allNotes.count { it.docType == "PDF" }
                val slidesCount = allNotes.count { it.docType == "SLIDES" }

                FilterChip(
                    selected = selectedDocType == "ALL" || selectedDocType == null,
                    onClick = { viewModel.onDocTypeFilterSelected("ALL") },
                    label = { Text("All (${allNotes.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeState.primaryColor,
                        selectedLabelColor = Color.White,
                        containerColor = themeState.surfaceColor,
                        labelColor = themeState.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = themeState.borderColor,
                        selectedBorderColor = themeState.primaryColor,
                        enabled = true,
                        selected = selectedDocType == "ALL" || selectedDocType == null
                    )
                )

                FilterChip(
                    selected = selectedDocType == "PDF",
                    onClick = { viewModel.onDocTypeFilterSelected("PDF") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = if (selectedDocType == "PDF") Color.White else Color(0xFFE11D48),
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("PDFs ($pdfCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeState.primaryColor,
                        selectedLabelColor = Color.White,
                        containerColor = themeState.surfaceColor,
                        labelColor = themeState.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = themeState.borderColor,
                        selectedBorderColor = themeState.primaryColor,
                        enabled = true,
                        selected = selectedDocType == "PDF"
                    )
                )

                FilterChip(
                    selected = selectedDocType == "SLIDES",
                    onClick = { viewModel.onDocTypeFilterSelected("SLIDES") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Slideshow,
                            contentDescription = null,
                            tint = if (selectedDocType == "SLIDES") Color.White else Color(0xFF4F46E5),
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Slides ($slidesCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = themeState.primaryColor,
                        selectedLabelColor = Color.White,
                        containerColor = themeState.surfaceColor,
                        labelColor = themeState.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = themeState.borderColor,
                        selectedBorderColor = themeState.primaryColor,
                        enabled = true,
                        selected = selectedDocType == "SLIDES"
                    )
                )

                if (favoritesOnly) {
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.toggleFavoritesOnly() },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("Favorites") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = themeState.primaryColor,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = themeState.primaryColor,
                            selectedBorderColor = themeState.primaryColor,
                            enabled = true,
                            selected = true
                        )
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = themeState.primaryColor)
                }
            }

            // Hierarchical Syllabus List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("syllabus_tree_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = SubjectCategory.entries,
                    key = { it.id }
                ) { subject ->
                    SubjectSectionCard(
                        subject = subject,
                        notes = visibleNotes,
                        searchQuery = searchQuery,
                        themeState = themeState,
                        isExpanded = expandedSubjects[subject.id] == true,
                        onToggleExpand = {
                            expandedSubjects[subject.id] = !(expandedSubjects[subject.id] ?: false)
                        },
                        unitExpandedMap = expandedUnits,
                        onToggleUnitExpand = { unitKey ->
                            expandedUnits[unitKey] = !(expandedUnits[unitKey] ?: false)
                        },
                        onOpenNote = { viewModel.viewPdf(it) },
                        onAddDocumentToChapter = { unitName, chapterName ->
                            viewModel.openAddDialogForChapter(subject.title, unitName, chapterName)
                        },
                        onEditNote = { viewModel.startEditing(it) },
                        onDeleteNote = { viewModel.confirmDelete(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
    }
    }

    // Modal PDF & Slides Viewer
    if (activeViewingNote != null) {
        PdfViewerDialog(
            note = activeViewingNote!!,
            onDismiss = { viewModel.closePdfViewer() }
        )
    }

    // Add Note / Slides Dialog
    if (showAddDialog) {
        AddNoteDialog(
            initialSubject = selectedSubject,
            initialUnit = selectedUnit,
            initialChapter = targetChapterForAdd,
            onDismiss = { viewModel.closeAddDialog() },
            onSaveImportedDocument = { uri, title, subject, unit, chapter, description, tags, docType ->
                viewModel.saveImportedDocument(uri, title, subject, unit, chapter, description, tags, docType)
            },
            onCreateSummaryPdf = { title, subject, unit, chapter, content, tags ->
                viewModel.createAndSaveSummaryPdf(title, subject, unit, chapter, content, tags)
            },
            onCreateSlideDeck = { title, subject, unit, chapter, slides, description, tags ->
                viewModel.createAndSaveSlideDeck(title, subject, unit, chapter, slides, description, tags)
            }
        )
    }

    // Edit Note Dialog
    if (editingNote != null) {
        EditNoteDialog(
            note = editingNote!!,
            onDismiss = { viewModel.closeEditing() },
            onSave = { id, title, subject, unit, chapter, description, tags ->
                viewModel.updateNoteDetails(id, title, subject, unit, chapter, description, tags)
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingNote != null) {
        DeleteConfirmDialog(
            note = deletingNote!!,
            onConfirm = { viewModel.executeDelete(it) },
            onDismiss = { viewModel.cancelDelete() }
        )
    }
}

@Composable
fun SubjectSectionCard(
    subject: SubjectCategory,
    notes: List<NotePdf>,
    searchQuery: String,
    themeState: AppThemeState,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    unitExpandedMap: Map<String, Boolean>,
    onToggleUnitExpand: (String) -> Unit,
    onOpenNote: (NotePdf) -> Unit,
    onAddDocumentToChapter: (unitName: String, chapterName: String) -> Unit,
    onEditNote: (NotePdf) -> Unit,
    onDeleteNote: (NotePdf) -> Unit,
    onToggleFavorite: (NotePdf) -> Unit
) {
    // Subject level notes
    val subjectNotes = remember(notes, subject) {
        notes.filter { it.subject.equals(subject.title, ignoreCase = true) }
    }

    // Count chapters that have notes
    val allChapters = remember(subject) {
        subject.syllabusUnits.flatMap { it.chapters }
    }
    val totalChaptersCount = allChapters.size

    val coveredChaptersCount = remember(allChapters, subjectNotes) {
        allChapters.count { chapter ->
            subjectNotes.any { note ->
                note.chapter.equals(chapter.name, ignoreCase = true) ||
                note.title.contains(chapter.name, ignoreCase = true)
            }
        }
    }

    val progress = if (totalChaptersCount > 0) coveredChaptersCount.toFloat() / totalChaptersCount else 0f
    val percentage = (progress * 100).toInt()

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron_anim"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isExpanded) themeState.primaryColor else themeState.borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("subject_card_${subject.id}"),
        color = themeState.surfaceColor,
        shadowElevation = if (isExpanded) 3.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        ) {
            // Subject Header Row (Clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subject Icon container
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(themeState.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = subject.getIcon(),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Subject Title
                Text(
                    text = subject.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeState.onBackgroundColor,
                    modifier = Modifier.weight(1f)
                )

                // Coverage Ratio / Percentage
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$coveredChaptersCount/$totalChaptersCount ready",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (coveredChaptersCount > 0) themeState.onBackgroundColor else themeState.onSurfaceVariant
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelSmall,
                        color = themeState.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Chevron icon
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = themeState.onBackgroundColor,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronRotation)
                )
            }

            // Subject Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = themeState.primaryColor,
                trackColor = themeState.borderColor,
                strokeCap = StrokeCap.Round
            )

            // Subject Content: List of Units
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(themeState.backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    subject.syllabusUnits.forEach { unit ->
                        val unitKey = "${subject.id}_${unit.name}"
                        val isUnitExpanded = unitExpandedMap[unitKey] == true

                        UnitSectionCard(
                            subject = subject,
                            unit = unit,
                            notes = subjectNotes,
                            searchQuery = searchQuery,
                            themeState = themeState,
                            isExpanded = isUnitExpanded,
                            onToggleExpand = { onToggleUnitExpand(unitKey) },
                            onOpenNote = onOpenNote,
                            onAddDocumentToChapter = onAddDocumentToChapter,
                            onEditNote = onEditNote,
                            onDeleteNote = onDeleteNote,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitSectionCard(
    subject: SubjectCategory,
    unit: SyllabusUnit,
    notes: List<NotePdf>,
    searchQuery: String,
    themeState: AppThemeState,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenNote: (NotePdf) -> Unit,
    onAddDocumentToChapter: (unitName: String, chapterName: String) -> Unit,
    onEditNote: (NotePdf) -> Unit,
    onDeleteNote: (NotePdf) -> Unit,
    onToggleFavorite: (NotePdf) -> Unit
) {
    val unitNotes = remember(notes, unit) {
        notes.filter { it.unit.equals(unit.name, ignoreCase = true) }
    }

    val unitCoveredCount = remember(unit, unitNotes) {
        unit.chapters.count { chapter ->
            unitNotes.any { note ->
                note.chapter.equals(chapter.name, ignoreCase = true) ||
                note.title.contains(chapter.name, ignoreCase = true)
            }
        }
    }

    val progress = if (unit.chapters.isNotEmpty()) unitCoveredCount.toFloat() / unit.chapters.size else 0f

    val unitChevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "unit_chevron"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isExpanded) themeState.primaryColor else themeState.borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("unit_card_${unit.name}"),
        color = themeState.surfaceColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        ) {
            // Unit Header Row (Clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unit Title
                Text(
                    text = unit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeState.onBackgroundColor,
                    modifier = Modifier.weight(1f)
                )

                // Coverage Ratio
                Text(
                    text = "$unitCoveredCount/${unit.chapters.size}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (unitCoveredCount > 0) themeState.onBackgroundColor else themeState.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = themeState.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(unitChevronRotation)
                )
            }

            // Subtle unit progress line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = themeState.primaryColor.copy(alpha = 0.7f),
                trackColor = themeState.borderColor
            )

            // Unit Chapters list
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unit.chapters.forEach { chapter ->
                        ChapterRowItem(
                            subject = subject,
                            unit = unit,
                            chapter = chapter,
                            allNotes = unitNotes,
                            searchQuery = searchQuery,
                            themeState = themeState,
                            onOpenNote = onOpenNote,
                            onAddDocument = { onAddDocumentToChapter(unit.name, chapter.name) },
                            onEditNote = onEditNote,
                            onDeleteNote = onDeleteNote,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChapterRowItem(
    subject: SubjectCategory,
    unit: SyllabusUnit,
    chapter: SyllabusChapter,
    allNotes: List<NotePdf>,
    searchQuery: String,
    themeState: AppThemeState,
    onOpenNote: (NotePdf) -> Unit,
    onAddDocument: () -> Unit,
    onEditNote: (NotePdf) -> Unit,
    onDeleteNote: (NotePdf) -> Unit,
    onToggleFavorite: (NotePdf) -> Unit
) {
    // Find all notes/slides matching this chapter
    val matchingNotes = remember(allNotes, chapter) {
        allNotes.filter { note ->
            note.chapter.equals(chapter.name, ignoreCase = true) ||
            note.title.contains(chapter.name, ignoreCase = true) ||
            note.tags.contains(chapter.name, ignoreCase = true)
        }
    }

    var showMenu by remember { mutableStateOf(false) }
    val cardPadding = if (themeState.compactDensity) 8.dp else 12.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = themeState.borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .testTag("chapter_item_${chapter.name}"),
        color = themeState.surfaceColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding)
        ) {
            // Chapter Title Header Row (Title on left, Add and Menu actions on right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chapter Title
                Text(
                    text = chapter.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeState.onBackgroundColor,
                    modifier = Modifier.weight(1f)
                )

                // Quick Add Document Button for this Chapter
                IconButton(
                    onClick = onAddDocument,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("add_doc_${chapter.name}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add PDF to ${chapter.name}",
                        tint = themeState.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("menu_${chapter.name}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = themeState.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add PDF / Slides to this Chapter") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAddDocument()
                            }
                        )

                        if (matchingNotes.isNotEmpty()) {
                            HorizontalDivider()
                            matchingNotes.forEach { note ->
                                DropdownMenuItem(
                                    text = { Text("Open: ${note.title}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = {
                                        Icon(
                                            if (note.docType == "SLIDES") Icons.Default.Slideshow else Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = themeState.primaryColor
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        onOpenNote(note)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (note.isFavorite) "Remove from Favorites" else "Add to Favorites", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        onToggleFavorite(note)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Edit: ${note.title}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        onEditNote(note)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete: ${note.title}", color = MaterialTheme.colorScheme.error, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        onDeleteNote(note)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Below Chapter Title: Display PDFs / Documents with their names
            if (matchingNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    matchingNotes.forEach { note ->
                        val isHighlighted = searchQuery.isNotBlank() && note.title.contains(searchQuery, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenNote(note) }
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        isHighlighted -> themeState.primaryColor
                                        note.isFavorite -> themeState.primaryColor.copy(alpha = 0.6f)
                                        else -> themeState.borderColor
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .testTag("open_doc_${note.id}"),
                            color = themeState.backgroundColor
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // PDF / Slides type pill badge
                                Surface(
                                    shape = RoundedCornerShape(5.dp),
                                    color = if (note.docType == "SLIDES") themeState.surfaceColor else themeState.primaryColor,
                                    border = BorderStroke(1.dp, themeState.primaryColor)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (note.docType == "SLIDES") Icons.Default.Slideshow else Icons.Default.PictureAsPdf,
                                            contentDescription = note.docType,
                                            tint = if (note.docType == "SLIDES") themeState.primaryColor else Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = note.docType,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (note.docType == "SLIDES") themeState.primaryColor else Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // PDF Name
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = themeState.onBackgroundColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                if (note.isFavorite) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Favorite",
                                        tint = themeState.primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Open",
                                    tint = themeState.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // When no PDF is attached yet, show a clean prompt below title
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAddDocument() }
                        .border(
                            width = 1.dp,
                            color = themeState.borderColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .testTag("add_doc_prompt_${chapter.name}"),
                    color = themeState.backgroundColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add PDF",
                            tint = themeState.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "No PDFs yet • Tap to attach note",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeState.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
