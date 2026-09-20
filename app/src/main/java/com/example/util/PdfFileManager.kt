package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import com.example.data.SlideItem
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfFileManager {

    private const val PDF_FOLDER = "notes_pdf"

    fun getPdfFolder(context: Context): File {
        val folder = File(context.filesDir, PDF_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    fun saveUriToInternalStorage(
        context: Context,
        uri: Uri,
        desiredFileName: String? = null,
        forceDocType: String? = null
    ): SavedDocumentResult? {
        return try {
            val contentResolver = context.contentResolver
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val cleanName = desiredFileName?.replace("[^a-zA-Z0-9._-]".toRegex(), "_") ?: "document_$timeStamp"
            
            val isPptx = cleanName.endsWith(".pptx", ignoreCase = true) || cleanName.endsWith(".ppt", ignoreCase = true)
            val extension = if (isPptx) ".pptx" else ".pdf"
            val actualFileName = if (cleanName.endsWith(".pdf", ignoreCase = true) || cleanName.endsWith(".pptx", ignoreCase = true)) {
                cleanName
            } else {
                "$cleanName$extension"
            }
            
            val destFile = File(getPdfFolder(context), "doc_${System.currentTimeMillis()}_$actualFileName")
            
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            var docType = forceDocType ?: if (isPptx) "SLIDES" else "PDF"
            var slidesJson = ""
            var targetViewerPdfFile = destFile
            var pageCount = 1

            if (isPptx) {
                // Parse slides from PPTX
                val extractedSlides = try {
                    FileInputStream(destFile).use { fis ->
                        SlidePresentationHelper.extractSlidesFromPptx(fis)
                    }
                } catch (e: Exception) {
                    emptyList()
                }

                if (extractedSlides.isNotEmpty()) {
                    slidesJson = SlidePresentationHelper.serializeSlides(extractedSlides)
                    pageCount = extractedSlides.size
                    // Generate companion PDF for built-in visual slide rendering
                    val pdfGenResult = SlidePresentationHelper.generatePresentationPdf(
                        context = context,
                        presentationTitle = cleanName.removeSuffix(".pptx").removeSuffix(".ppt"),
                        subject = "Presentation",
                        unit = "Lecture Slides",
                        slides = extractedSlides
                    )
                    if (pdfGenResult != null) {
                        targetViewerPdfFile = pdfGenResult.file
                    }
                }
            } else {
                pageCount = getPdfPageCount(destFile)
            }

            val sizeBytes = destFile.length()

            SavedDocumentResult(
                file = targetViewerPdfFile,
                fileName = actualFileName,
                fileSizeBytes = sizeBytes,
                pageCount = pageCount,
                docType = docType,
                extractedSlidesJson = slidesJson
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPdfPageCount(file: File): Int {
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        return try {
            if (!file.exists()) return 1
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)
            val count = renderer.pageCount
            if (count > 0) count else 1
        } catch (e: Exception) {
            1
        } finally {
            try {
                renderer?.close()
                pfd?.close()
            } catch (_: Exception) {}
        }
    }

    fun renderPageToBitmap(file: File, pageIndex: Int, destWidth: Int = 1200): Bitmap? {
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null
        return try {
            if (!file.exists()) return null
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)
            if (pageIndex < 0 || pageIndex >= renderer.pageCount) return null
            
            page = renderer.openPage(pageIndex)
            val pageWidth = page.width
            val pageHeight = page.height
            val scale = destWidth.toFloat() / pageWidth.toFloat()
            val destHeight = (pageHeight * scale).toInt().coerceAtLeast(1)

            val bitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                page?.close()
                renderer?.close()
                pfd?.close()
            } catch (_: Exception) {}
        }
    }

    fun createQuickPdfDocument(
        context: Context,
        title: String,
        subject: String,
        unit: String,
        notesContent: String
    ): SavedPdfResult? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            
            // Background
            canvas.drawColor(Color.WHITE)

            // Header Banner
            paint.color = when (subject.lowercase()) {
                "physics" -> Color.rgb(29, 78, 216)
                "chemistry" -> Color.rgb(13, 148, 136)
                "zoology" -> Color.rgb(234, 88, 12)
                "botany" -> Color.rgb(22, 163, 74)
                "mat" -> Color.rgb(124, 58, 237)
                else -> Color.rgb(30, 41, 59)
            }
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            // Category & Unit Pill in Header
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 20f
            canvas.drawText("$subject • $unit", 36f, 44f, paint)

            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            canvas.drawText("Study Note Vault — Generated on $dateStr", 36f, 70f, paint)

            // Title
            paint.color = Color.rgb(30, 41, 59)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 18f
            canvas.drawText(title, 36f, 130f, paint)

            // Divider
            paint.color = Color.rgb(226, 232, 240)
            paint.strokeWidth = 1.5f
            canvas.drawLine(36f, 145f, 559f, 145f, paint)

            // Body text lines with wrapping
            paint.color = Color.rgb(51, 65, 85)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            
            var yOffset = 175f
            val lineHeight = 18f
            val maxWidth = 520f

            val paragraphs = notesContent.split("\n")
            for (para in paragraphs) {
                if (para.isBlank()) {
                    yOffset += lineHeight * 0.8f
                    continue
                }
                val words = para.split(" ")
                var currentLine = ""
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val measure = paint.measureText(testLine)
                    if (measure > maxWidth) {
                        canvas.drawText(currentLine, 36f, yOffset, paint)
                        yOffset += lineHeight
                        currentLine = word
                        if (yOffset > 800f) break
                    } else {
                        currentLine = testLine
                    }
                }
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine, 36f, yOffset, paint)
                    yOffset += lineHeight
                }
                yOffset += 6f
                if (yOffset > 800f) break
            }

            // Footer
            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 10f
            canvas.drawText("Notes PDF Organizer • Page 1 of 1", 36f, 815f, paint)

            pdfDocument.finishPage(page)

            val safeTitle = title.replace("[^a-zA-Z0-9]".toRegex(), "_").take(20)
            val fileName = "${safeTitle}_${System.currentTimeMillis()}.pdf"
            val destFile = File(getPdfFolder(context), fileName)
            val fos = FileOutputStream(destFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            SavedPdfResult(
                file = destFile,
                fileName = fileName,
                fileSizeBytes = destFile.length(),
                pageCount = 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openPdfExternally(context: Context, file: File): Boolean {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val mimeType = if (file.name.endsWith(".pptx", ignoreCase = true)) {
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            } else {
                "application/pdf"
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sharePdf(context: Context, file: File, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val mimeType = if (file.name.endsWith(".pptx", ignoreCase = true)) {
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            } else {
                "application/pdf"
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Notes & Slides: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Document")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 KB"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1.0) {
            String.format(Locale.US, "%.1f MB", mb)
        } else {
            String.format(Locale.US, "%.0f KB", kb.coerceAtLeast(1.0))
        }
    }

    fun deletePdfFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}

data class SavedPdfResult(
    val file: File,
    val fileName: String,
    val fileSizeBytes: Long,
    val pageCount: Int
)

data class SavedDocumentResult(
    val file: File,
    val fileName: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val docType: String,
    val extractedSlidesJson: String = ""
)
