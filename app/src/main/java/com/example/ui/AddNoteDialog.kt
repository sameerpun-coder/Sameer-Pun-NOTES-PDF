package com.example.ui

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SlideItem
import com.example.data.SubjectCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    initialSubject: String? = null,
    initialUnit: String? = null,
    initialChapter: String? = null,
    onDismiss: () -> Unit,
    onSaveImportedDocument: (uri: Uri, title: String, subject: String, unit: String, chapter: String, description: String, tags: String, docType: String) -> Unit,
    onCreateSummaryPdf: (title: String, subject: String, unit: String, chapter: String, content: String, tags: String) -> Unit,
    onCreateSlideDeck: (title: String, subject: String, unit: String, chapter: String, slides: List<SlideItem>, description: String, tags: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Import File, 1: Summary PDF, 2: Presentation Slides

    // Import file state
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileSize by remember { mutableStateOf<Long?>(null) }
    var detectedDocType by remember { mutableStateOf("PDF") } // "PDF" or "SLIDES"

    var title by remember { mutableStateOf("") }
    var selectedSubject by remember {
        mutableStateOf(
            if (initialSubject != null) SubjectCategory.fromTitle(initialSubject) else SubjectCategory.PHYSICS
        )
    }

    val availableUnits = selectedSubject.units
    var selectedUnit by remember(selectedSubject) {
        mutableStateOf(
            if (initialUnit != null && availableUnits.contains(initialUnit)) initialUnit else availableUnits.first()
        )
    }

    val availableChapters = remember(selectedSubject, selectedUnit) {
        SubjectCategory.getDefaultChapters(selectedUnit)
    }
    var selectedChapter by remember(availableChapters) {
        mutableStateOf(
            if (initialChapter != null && availableChapters.contains(initialChapter)) initialChapter
            else availableChapters.firstOrNull() ?: ""
        )
    }
    var chapterMenuExpanded by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }
    var writtenNotesContent by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    // Slide Builder state
    val builderSlides = remember {
        mutableStateListOf(
            SlideItem(
                slideNumber = 1,
                title = "Introduction & Overview",
                bullets = listOf("Key concept definitions", "First principles analysis"),
                formulaOrCallout = "",
                presenterNotes = "Review before exam"
            )
        )
    }

    var subjectMenuExpanded by remember { mutableStateOf(false) }
    var unitMenuExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // File picker contract accepting PDF, PPTX, and PPT
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        val name = if (nameIndex != -1) cursor.getString(nameIndex) else "Document"
                        val size = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
                        selectedFileName = name
                        selectedFileSize = size
                        
                        val isSlides = name.endsWith(".pptx", ignoreCase = true) || 
                                       name.endsWith(".ppt", ignoreCase = true) ||
                                       name.contains("slide", ignoreCase = true) ||
                                       name.contains("presentation", ignoreCase = true)
                        detectedDocType = if (isSlides) "SLIDES" else "PDF"

                        if (title.isBlank()) {
                            title = name.substringBeforeLast(".")
                        }
                    }
                }
            } catch (e: Exception) {
                selectedFileName = "Selected file"
            }
        }
    }

    var dialogVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        dialogVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = dialogVisible,
            enter = scaleIn(
                initialScale = 0.85f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.85f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(animationSpec = tween(150))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .heightIn(max = 680.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("add_note_dialog_surface"),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Dialog Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Add Note / Document",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close dialog")
                        }
                    }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: 0: Import (PDF/Slides), 1: Write PDF, 2: Create Slides
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Import File") },
                        icon = { Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Write PDF") },
                        icon = { Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Create Slides") },
                        icon = { Icon(Icons.Default.Slideshow, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Common Field: Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Title *") },
                    placeholder = {
                        Text(
                            when (selectedTab) {
                                0 -> "e.g. Wave Optics & Interference Notes"
                                1 -> "e.g. Organic Chemistry Reaction Summary"
                                else -> "e.g. Zoology Circulation Lecture Slides"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subject Selection
                Text(
                    text = "Subject Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { subjectMenuExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("subject_selector_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = selectedSubject.lightContainerColor
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = selectedSubject.getIcon(),
                                    contentDescription = null,
                                    tint = selectedSubject.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedSubject.title,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedSubject.accentColor
                                )
                            }
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = selectedSubject.accentColor
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = subjectMenuExpanded,
                        onDismissRequest = { subjectMenuExpanded = false }
                    ) {
                        SubjectCategory.entries.forEach { subject ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = subject.getIcon(),
                                            contentDescription = null,
                                            tint = subject.accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(subject.title, fontWeight = FontWeight.Medium)
                                    }
                                },
                                onClick = {
                                    selectedSubject = subject
                                    selectedUnit = subject.units.first()
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Unit / Subcategory Selection
                Text(
                    text = "Unit / Subcategory",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { unitMenuExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unit_selector_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedUnit,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = unitMenuExpanded,
                        onDismissRequest = { unitMenuExpanded = false }
                    ) {
                        availableUnits.forEach { unitItem ->
                            DropdownMenuItem(
                                text = { Text(unitItem) },
                                onClick = {
                                    selectedUnit = unitItem
                                    unitMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chapter / Topic Selection
                Text(
                    text = "Syllabus Chapter / Topic",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { chapterMenuExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chapter_selector_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedChapter.ifBlank { "General / All Chapters" },
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = chapterMenuExpanded,
                        onDismissRequest = { chapterMenuExpanded = false }
                    ) {
                        availableChapters.forEach { chapterItem ->
                            DropdownMenuItem(
                                text = { Text(chapterItem) },
                                onClick = {
                                    selectedChapter = chapterItem
                                    chapterMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TAB CONTENT
                when (selectedTab) {
                    0 -> {
                        // IMPORT DOCUMENT / PRESENTATION
                        Text(
                            text = "Import PDF or PowerPoint Presentation",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = if (selectedUri != null) selectedSubject.accentColor else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    documentPickerLauncher.launch(
                                        arrayOf(
                                            "application/pdf",
                                            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                                            "application/vnd.ms-powerpoint"
                                        )
                                    )
                                },
                            color = if (selectedUri != null) selectedSubject.lightContainerColor else MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (selectedUri != null) selectedSubject.accentColor else MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (selectedUri != null) Icons.Default.Check else Icons.Default.FileOpen,
                                            contentDescription = null,
                                            tint = if (selectedUri != null) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedFileName ?: "Tap to choose a PDF or Slides file (.pdf, .pptx)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (selectedFileSize != null && selectedFileSize!! > 0)
                                            "${com.example.util.PdfFileManager.formatFileSize(selectedFileSize!!)} • Ready to import"
                                        else "Supports PDF, PowerPoint (.pptx, .ppt)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Document Type Selection (PDF vs Slides)
                        Text(
                            text = "Display Format",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip(
                                selected = detectedDocType == "PDF",
                                onClick = { detectedDocType = "PDF" },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("PDF Document") }
                            )
                            FilterChip(
                                selected = detectedDocType == "SLIDES",
                                onClick = { detectedDocType = "SLIDES" },
                                leadingIcon = { Icon(Icons.Default.Slideshow, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("Presentation Slides") }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description / Summary") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 70.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    1 -> {
                        // WRITE SUMMARY PDF
                        OutlinedTextField(
                            value = writtenNotesContent,
                            onValueChange = {
                                writtenNotesContent = it
                                errorMessage = null
                            },
                            label = { Text("Notes Content *") },
                            placeholder = { Text("Write formulas, reactions, definitions, or exam pointers here...\nWill be automatically compiled into a formatted PDF document.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 150.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    2 -> {
                        // CREATE SLIDE DECK
                        Text(
                            text = "Slides in Deck (${builderSlides.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        builderSlides.forEachIndexed { index, slide ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Slide ${index + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = selectedSubject.accentColor
                                        )
                                        if (builderSlides.size > 1) {
                                            IconButton(
                                                onClick = { builderSlides.removeAt(index) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete slide", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = slide.title,
                                        onValueChange = { newTitle ->
                                            builderSlides[index] = slide.copy(title = newTitle)
                                        },
                                        label = { Text("Slide Title") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    var bulletsText by remember(slide.bullets) {
                                        mutableStateOf(slide.bullets.joinToString("\n"))
                                    }
                                    OutlinedTextField(
                                        value = bulletsText,
                                        onValueChange = { text ->
                                            bulletsText = text
                                            builderSlides[index] = slide.copy(
                                                bullets = text.split("\n").filter { it.isNotBlank() }
                                            )
                                        },
                                        label = { Text("Bullet Points (one per line)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 70.dp)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = slide.formulaOrCallout,
                                        onValueChange = { f ->
                                            builderSlides[index] = slide.copy(formulaOrCallout = f)
                                        },
                                        label = { Text("Key Formula / Takeaway Box (optional)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                builderSlides.add(
                                    SlideItem(
                                        slideNumber = builderSlides.size + 1,
                                        title = "Topic ${builderSlides.size + 1}",
                                        bullets = listOf("Key observation or theorem"),
                                        formulaOrCallout = "",
                                        presenterNotes = ""
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Another Slide")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tags
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags / Keywords") },
                    placeholder = { Text("e.g. Physics, Exam, Revision") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Error alert
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.testTag("save_note_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter a title for your document."
                                return@Button
                            }

                            when (selectedTab) {
                                0 -> {
                                    if (selectedUri == null) {
                                        errorMessage = "Please select a PDF or Slides file to import."
                                        return@Button
                                    }
                                    onSaveImportedDocument(
                                        selectedUri!!,
                                        title.trim(),
                                        selectedSubject.title,
                                        selectedUnit,
                                        selectedChapter,
                                        description.trim(),
                                        tags.trim(),
                                        detectedDocType
                                    )
                                }
                                1 -> {
                                    if (writtenNotesContent.isBlank()) {
                                        errorMessage = "Please write some notes content."
                                        return@Button
                                    }
                                    onCreateSummaryPdf(
                                        title.trim(),
                                        selectedSubject.title,
                                        selectedUnit,
                                        selectedChapter,
                                        writtenNotesContent.trim(),
                                        tags.trim()
                                    )
                                }
                                2 -> {
                                    if (builderSlides.isEmpty()) {
                                        errorMessage = "Please add at least one slide."
                                        return@Button
                                    }
                                    onCreateSlideDeck(
                                        title.trim(),
                                        selectedSubject.title,
                                        selectedUnit,
                                        selectedChapter,
                                        builderSlides.toList(),
                                        description.ifBlank { "Presentation deck with ${builderSlides.size} slides." },
                                        tags.trim()
                                    )
                                }
                            }
                        }
                    ) {
                        Text(
                            when (selectedTab) {
                                0 -> "Save Document"
                                1 -> "Compile PDF Note"
                                else -> "Create Slide Deck"
                            }
                        )
                    }
                }
            }
        }
    }
}
}
