package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.data.SlideItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.regex.Pattern
import java.util.zip.ZipInputStream

object SlidePresentationHelper {

    fun serializeSlides(slides: List<SlideItem>): String {
        val jsonArray = JSONArray()
        for (slide in slides) {
            val obj = JSONObject().apply {
                put("slideNumber", slide.slideNumber)
                put("title", slide.title)
                val bulletsArr = JSONArray()
                slide.bullets.forEach { bulletsArr.put(it) }
                put("bullets", bulletsArr)
                put("formulaOrCallout", slide.formulaOrCallout)
                put("presenterNotes", slide.presenterNotes)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    fun deserializeSlides(jsonStr: String): List<SlideItem> {
        if (jsonStr.isBlank()) return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<SlideItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val bulletsList = mutableListOf<String>()
                val bulletsArr = obj.optJSONArray("bullets")
                if (bulletsArr != null) {
                    for (b in 0 until bulletsArr.length()) {
                        bulletsList.add(bulletsArr.getString(b))
                    }
                }
                list.add(
                    SlideItem(
                        slideNumber = obj.optInt("slideNumber", i + 1),
                        title = obj.optString("title", "Slide ${i + 1}"),
                        bullets = bulletsList,
                        formulaOrCallout = obj.optString("formulaOrCallout", ""),
                        presenterNotes = obj.optString("presenterNotes", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Extracts slides from a PowerPoint .pptx file directly using ZipInputStream.
     */
    fun extractSlidesFromPptx(inputStream: InputStream): List<SlideItem> {
        val extractedSlides = mutableMapOf<Int, MutableList<String>>()
        try {
            val zipIn = ZipInputStream(inputStream)
            var entry = zipIn.nextEntry
            val slidePattern = Pattern.compile("ppt/slides/slide(\\d+)\\.xml")
            val textPattern = Pattern.compile("<a:t[^>]*>(.*?)</a:t>")

            while (entry != null) {
                val matcher = slidePattern.matcher(entry.name)
                if (matcher.matches()) {
                    val slideNum = matcher.group(1)?.toIntOrNull() ?: 1
                    val bos = ByteArrayOutputStream()
                    val buffer = ByteArray(2048)
                    var len: Int
                    while (zipIn.read(buffer).also { len = it } > 0) {
                        bos.write(buffer, 0, len)
                    }
                    val xmlContent = bos.toString("UTF-8")
                    val textMatcher = textPattern.matcher(xmlContent)
                    val texts = mutableListOf<String>()
                    while (textMatcher.find()) {
                        val t = textMatcher.group(1)?.trim() ?: ""
                        if (t.isNotBlank()) {
                            texts.add(t)
                        }
                    }
                    if (texts.isNotEmpty()) {
                        extractedSlides[slideNum] = texts
                    }
                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
            zipIn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (extractedSlides.isEmpty()) return emptyList()

        return extractedSlides.toSortedMap().map { (num, textList) ->
            val title = textList.firstOrNull() ?: "Slide $num"
            val bullets = if (textList.size > 1) textList.subList(1, textList.size) else emptyList()
            SlideItem(
                slideNumber = num,
                title = title,
                bullets = bullets,
                formulaOrCallout = "",
                presenterNotes = ""
            )
        }
    }

    /**
     * Renders a list of SlideItems into a 16:9 widescreen presentation PDF document.
     * Dimensions: 960 x 540 pt (16:9).
     */
    fun generatePresentationPdf(
        context: Context,
        presentationTitle: String,
        subject: String,
        unit: String,
        slides: List<SlideItem>
    ): SavedPdfResult? {
        return try {
            val pdfDocument = PdfDocument()
            val slideWidth = 960
            val slideHeight = 540

            val subjectColor = when (subject.lowercase()) {
                "physics" -> Color.rgb(29, 78, 216)
                "chemistry" -> Color.rgb(13, 148, 136)
                "zoology" -> Color.rgb(234, 88, 12)
                "botany" -> Color.rgb(22, 163, 74)
                "mat" -> Color.rgb(124, 58, 237)
                else -> Color.rgb(30, 41, 59)
            }

            slides.forEachIndexed { index, slide ->
                val pageInfo = PdfDocument.PageInfo.Builder(slideWidth, slideHeight, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Background: elegant dark modern slide canvas or clean contrast
                canvas.drawColor(Color.rgb(15, 23, 42)) // Slate 900

                val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                // Top Accent Header Stripe
                paint.color = subjectColor
                canvas.drawRect(0f, 0f, slideWidth.toFloat(), 12f, paint)

                // Breadcrumb Pill: Subject • Unit
                paint.color = Color.rgb(51, 65, 85)
                val pillRect = RectF(48f, 36f, 260f, 68f)
                canvas.drawRoundRect(pillRect, 8f, 8f, paint)

                paint.color = Color.rgb(241, 245, 249)
                paint.textSize = 14f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("$subject • $unit", 60f, 57f, paint)

                // Slide Title
                paint.color = Color.WHITE
                paint.textSize = 28f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(slide.title, 48f, 118f, paint)

                // Divider Line
                paint.color = Color.rgb(51, 65, 85)
                paint.strokeWidth = 2f
                canvas.drawLine(48f, 138f, (slideWidth - 48).toFloat(), 138f, paint)

                // Bullets Content
                paint.color = Color.rgb(226, 232, 240)
                paint.textSize = 18f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                var yOffset = 180f
                for (bullet in slide.bullets) {
                    // Draw custom bullet circle
                    paint.color = subjectColor
                    canvas.drawCircle(58f, yOffset - 6f, 4f, paint)

                    paint.color = Color.rgb(241, 245, 249)
                    // Wrap text if needed
                    val words = bullet.split(" ")
                    var line = ""
                    for (word in words) {
                        val testLine = if (line.isEmpty()) word else "$line $word"
                        if (paint.measureText(testLine) > 780f) {
                            canvas.drawText(line, 74f, yOffset, paint)
                            yOffset += 28f
                            line = word
                        } else {
                            line = testLine
                        }
                    }
                    if (line.isNotEmpty()) {
                        canvas.drawText(line, 74f, yOffset, paint)
                        yOffset += 32f
                    }
                }

                // Formula / Key Callout Box if present
                if (slide.formulaOrCallout.isNotBlank()) {
                    val calloutRect = RectF(48f, (slideHeight - 130).toFloat(), (slideWidth - 48).toFloat(), (slideHeight - 56).toFloat())
                    paint.color = Color.rgb(30, 41, 59)
                    canvas.drawRoundRect(calloutRect, 12f, 12f, paint)

                    // Accent left bar
                    paint.color = subjectColor
                    canvas.drawRoundRect(RectF(48f, (slideHeight - 130).toFloat(), 56f, (slideHeight - 56).toFloat()), 4f, 4f, paint)

                    paint.color = Color.rgb(248, 250, 252)
                    paint.textSize = 15f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    canvas.drawText("KEY FORMULA / TAKEAWAY:", 72f, (slideHeight - 96).toFloat(), paint)

                    paint.color = Color.rgb(147, 197, 253)
                    paint.textSize = 16f
                    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    canvas.drawText(slide.formulaOrCallout, 72f, (slideHeight - 72).toFloat(), paint)
                }

                // Footer: Slide Number & App Branding
                paint.color = Color.rgb(100, 116, 139)
                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(presentationTitle, 48f, (slideHeight - 24).toFloat(), paint)

                val slideIndicator = "Slide ${index + 1} of ${slides.size}"
                val indicatorWidth = paint.measureText(slideIndicator)
                canvas.drawText(slideIndicator, slideWidth - 48f - indicatorWidth, (slideHeight - 24).toFloat(), paint)

                pdfDocument.finishPage(page)
            }

            val safeTitle = presentationTitle.replace("[^a-zA-Z0-9]".toRegex(), "_").take(20)
            val fileName = "slides_${safeTitle}_${System.currentTimeMillis()}.pdf"
            val destFile = File(PdfFileManager.getPdfFolder(context), fileName)
            val fos = FileOutputStream(destFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            SavedPdfResult(
                file = destFile,
                fileName = fileName,
                fileSizeBytes = destFile.length(),
                pageCount = slides.size
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
