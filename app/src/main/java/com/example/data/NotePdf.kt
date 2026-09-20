package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_pdf")
data class NotePdf(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val unit: String,
    val chapter: String = "",
    val description: String = "",
    val filePath: String,
    val fileName: String,
    val fileSizeBytes: Long = 0L,
    val pageCount: Int = 1,
    val docType: String = "PDF", // "PDF" or "SLIDES"
    val slidesDataJson: String = "", // JSON representation of slides
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val tags: String = ""
)

data class SlideItem(
    val slideNumber: Int,
    val title: String,
    val bullets: List<String> = emptyList(),
    val formulaOrCallout: String = "",
    val presenterNotes: String = ""
)
