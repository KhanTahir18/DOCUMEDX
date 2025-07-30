package com.example.documedx.utilis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfGenerator {

    fun createPdfFromBitmap(context: Context, bitmap: Bitmap, fileName: String): String? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas: Canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, Paint())

            pdfDocument.finishPage(page)

            val file = File(context.getExternalFilesDir(null), "$fileName.pdf")
            val fileOutputStream = FileOutputStream(file)

            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}