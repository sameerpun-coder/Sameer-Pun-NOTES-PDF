package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppThemeState
import com.example.data.NotePdf
import com.example.data.NotePdfRepository
import com.example.data.SlideItem
import com.example.data.ThemePreferences
import com.example.data.ThemePreset
import com.example.util.PdfFileManager
import com.example.util.SlidePresentationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotePdfRepository
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    private val _selectedUnit = MutableStateFlow<String?>(null)
    val selectedUnit: StateFlow<String?> = _selectedUnit.asStateFlow()

    private val _targetChapterForAdd = MutableStateFlow<String?>(null)
    val targetChapterForAdd: StateFlow<String?> = _targetChapterForAdd.asStateFlow()

    private val _selectedDocType = MutableStateFlow<String?>("ALL") // "ALL", "PDF", "SLIDES"
    val selectedDocType: StateFlow<String?> = _selectedDocType.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly: StateFlow<Boolean> = _favoritesOnly.asStateFlow()

    private val _activeViewingNote = MutableStateFlow<NotePdf?>(null)
    val activeViewingNote: StateFlow<NotePdf?> = _activeViewingNote.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _editingNote = MutableStateFlow<NotePdf?>(null)
    val editingNote: StateFlow<NotePdf?> = _editingNote.asStateFlow()

    private val _deletingNote = MutableStateFlow<NotePdf?>(null)
    val deletingNote: StateFlow<NotePdf?> = _deletingNote.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val themePreferences = ThemePreferences(application)
    val themeState: StateFlow<AppThemeState> = themePreferences.themeState

    private val _showSettingsScreen = MutableStateFlow(false)
    val showSettingsScreen: StateFlow<Boolean> = _showSettingsScreen.asStateFlow()

    fun openSettings() {
        _showSettingsScreen.value = true
    }

    fun closeSettings() {
        _showSettingsScreen.value = false
    }

    fun selectThemePreset(preset: ThemePreset) {
        themePreferences.setPreset(preset)
    }

    fun selectCustomBackground(hex: String?) {
        themePreferences.setCustomBackgroundColor(hex)
    }

    fun selectCustomAccent(hex: String?) {
        themePreferences.setCustomAccentColor(hex)
    }

    fun toggleShowMicroTopics(show: Boolean) {
        themePreferences.setShowMicroTopics(show)
    }

    fun toggleCompactDensity(compact: Boolean) {
        themePreferences.setCompactDensity(compact)
    }

    fun resetThemeDefaults() {
        themePreferences.resetToDefault()
    }

    val allNotes: StateFlow<List<NotePdf>>

    init {
        val database = AppDatabase.getInstance(application)
        repository = NotePdfRepository(database.notePdfDao(), application)

        allNotes = repository.allNotes
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Prepopulate default high-yield starter sample study notes and slide decks if database is empty
        viewModelScope.launch {
            val currentList = repository.allNotes.first()
            if (currentList.isEmpty()) {
                _isLoading.value = true
                repository.prepopulateSamplesIfEmpty()
                _isLoading.value = false
            }
        }
    }

    // Filtered notes flow
    private data class FilterCriteria(
        val query: String,
        val subject: String?,
        val unit: String?,
        val docType: String?,
        val favOnly: Boolean
    )

    private val filterCriteria = combine(
        _searchQuery,
        _selectedSubject,
        _selectedUnit,
        _selectedDocType,
        _favoritesOnly
    ) { query, subject, unit, docType, favOnly ->
        FilterCriteria(query, subject, unit, docType, favOnly)
    }

    val filteredNotes: StateFlow<List<NotePdf>> = combine(
        allNotes,
        filterCriteria
    ) { notes, filter ->
        notes.filter { note ->
            val matchesSubject = filter.subject == null || note.subject.equals(filter.subject, ignoreCase = true)
            val matchesUnit = filter.unit == null || note.unit.equals(filter.unit, ignoreCase = true)
            val matchesFav = !filter.favOnly || note.isFavorite
            val matchesDocType = filter.docType == null || filter.docType == "ALL" || note.docType.equals(filter.docType, ignoreCase = true)
            val matchesQuery = filter.query.isBlank() || (
                note.title.contains(filter.query, ignoreCase = true) ||
                note.chapter.contains(filter.query, ignoreCase = true) ||
                note.description.contains(filter.query, ignoreCase = true) ||
                note.unit.contains(filter.query, ignoreCase = true) ||
                note.subject.contains(filter.query, ignoreCase = true) ||
                note.tags.contains(filter.query, ignoreCase = true) ||
                note.fileName.contains(filter.query, ignoreCase = true)
            )
            matchesSubject && matchesUnit && matchesFav && matchesDocType && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSubjectSelected(subject: String?) {
        _selectedSubject.value = subject
        _selectedUnit.value = null // reset unit when subject changes
    }

    fun onUnitSelected(unit: String?) {
        _selectedUnit.value = unit
    }

    fun onDocTypeFilterSelected(type: String) {
        _selectedDocType.value = type
    }

    fun toggleFavoritesOnly() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    fun openAddDialog() {
        _targetChapterForAdd.value = null
        _showAddDialog.value = true
    }

    fun openAddDialogForChapter(subject: String, unit: String, chapter: String) {
        _selectedSubject.value = subject
        _selectedUnit.value = unit
        _targetChapterForAdd.value = chapter
        _showAddDialog.value = true
    }

    fun closeAddDialog() {
        _targetChapterForAdd.value = null
        _showAddDialog.value = false
    }

    fun startEditing(note: NotePdf) {
        _editingNote.value = note
    }

    fun closeEditing() {
        _editingNote.value = null
    }

    fun confirmDelete(note: NotePdf) {
        _deletingNote.value = note
    }

    fun cancelDelete() {
        _deletingNote.value = null
    }

    fun viewPdf(note: NotePdf) {
        _activeViewingNote.value = note
    }

    fun closePdfViewer() {
        _activeViewingNote.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun toggleFavorite(note: NotePdf) {
        viewModelScope.launch {
            repository.toggleFavorite(note.id, note.isFavorite)
        }
    }

    fun executeDelete(note: NotePdf) {
        viewModelScope.launch {
            repository.deleteNote(note)
            if (_activeViewingNote.value?.id == note.id) {
                _activeViewingNote.value = null
            }
            _deletingNote.value = null
            _snackbarMessage.value = "Deleted \"${note.title}\""
        }
    }

    fun saveImportedDocument(
        uri: Uri,
        title: String,
        subject: String,
        unit: String,
        chapter: String = "",
        description: String,
        tags: String,
        docType: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val saveResult = PdfFileManager.saveUriToInternalStorage(
                context = getApplication(),
                uri = uri,
                desiredFileName = title,
                forceDocType = docType
            )
            if (saveResult != null) {
                val newNote = NotePdf(
                    title = title.ifBlank { saveResult.fileName },
                    subject = subject,
                    unit = unit,
                    chapter = chapter,
                    description = description,
                    filePath = saveResult.file.absolutePath,
                    fileName = saveResult.fileName,
                    fileSizeBytes = saveResult.fileSizeBytes,
                    pageCount = saveResult.pageCount,
                    docType = saveResult.docType,
                    slidesDataJson = saveResult.extractedSlidesJson,
                    tags = tags
                )
                repository.insertNote(newNote)
                _snackbarMessage.value = "Added \"${newNote.title}\" successfully!"
            } else {
                _snackbarMessage.value = "Failed to import file."
            }
            _isLoading.value = false
            _targetChapterForAdd.value = null
            _showAddDialog.value = false
        }
    }

    fun createAndSaveSummaryPdf(
        title: String,
        subject: String,
        unit: String,
        chapter: String = "",
        notesContent: String,
        tags: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val saveResult = PdfFileManager.createQuickPdfDocument(
                context = getApplication(),
                title = title,
                subject = subject,
                unit = unit,
                notesContent = notesContent
            )
            if (saveResult != null) {
                val newNote = NotePdf(
                    title = title,
                    subject = subject,
                    unit = unit,
                    chapter = chapter,
                    description = notesContent.take(200),
                    filePath = saveResult.file.absolutePath,
                    fileName = saveResult.fileName,
                    fileSizeBytes = saveResult.fileSizeBytes,
                    pageCount = saveResult.pageCount,
                    docType = "PDF",
                    tags = tags
                )
                repository.insertNote(newNote)
                _snackbarMessage.value = "Created PDF \"$title\"!"
            } else {
                _snackbarMessage.value = "Failed to generate PDF."
            }
            _isLoading.value = false
            _targetChapterForAdd.value = null
            _showAddDialog.value = false
        }
    }

    fun createAndSaveSlideDeck(
        title: String,
        subject: String,
        unit: String,
        chapter: String = "",
        slides: List<SlideItem>,
        description: String,
        tags: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val saveResult = SlidePresentationHelper.generatePresentationPdf(
                context = getApplication(),
                presentationTitle = title,
                subject = subject,
                unit = unit,
                slides = slides
            )
            if (saveResult != null) {
                val newNote = NotePdf(
                    title = title,
                    subject = subject,
                    unit = unit,
                    chapter = chapter,
                    description = description.ifBlank { "Presentation deck with ${slides.size} slides." },
                    filePath = saveResult.file.absolutePath,
                    fileName = saveResult.fileName,
                    fileSizeBytes = saveResult.fileSizeBytes,
                    pageCount = saveResult.pageCount,
                    docType = "SLIDES",
                    slidesDataJson = SlidePresentationHelper.serializeSlides(slides),
                    tags = tags
                )
                repository.insertNote(newNote)
                _snackbarMessage.value = "Created Slide Deck \"$title\" with ${slides.size} slides!"
            } else {
                _snackbarMessage.value = "Failed to create slide deck."
            }
            _isLoading.value = false
            _targetChapterForAdd.value = null
            _showAddDialog.value = false
        }
    }

    fun updateNoteDetails(
        id: Long,
        title: String,
        subject: String,
        unit: String,
        chapter: String = "",
        description: String,
        tags: String
    ) {
        val note = _editingNote.value ?: return
        viewModelScope.launch {
            val updated = note.copy(
                title = title,
                subject = subject,
                unit = unit,
                chapter = chapter.ifBlank { note.chapter },
                description = description,
                tags = tags,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateNote(updated)
            _editingNote.value = null
            if (_activeViewingNote.value?.id == id) {
                _activeViewingNote.value = updated
            }
            _snackbarMessage.value = "Note updated!"
        }
    }
}
