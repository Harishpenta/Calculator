package com.pentadigital.calculator.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {
    
    private const val TAG = "ExportUtils"
    
    /**
     * Export calculation history to PDF
     */
    fun exportHistoryToPdf(context: Context, history: List<String>): Result<File> {
        return try {
            val fileName = "calculator_history_${getTimestamp()}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            
            // Title
            val title = Paragraph("Calculation History")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)
            
            // Date
            val dateText = Paragraph("Generated on: ${getCurrentDate()}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(dateText)
            
            document.add(Paragraph("\n"))
            
            // Table
            if (history.isNotEmpty()) {
                val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 3f)))
                    .useAllAvailableWidth()
                
                // Header
                table.addHeaderCell(
                    Cell().add(Paragraph("#").setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                table.addHeaderCell(
                    Cell().add(Paragraph("Calculation").setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                
                // Data
                history.forEachIndexed { index, calculation ->
                    table.addCell(Cell().add(Paragraph("${index + 1}")))
                    table.addCell(Cell().add(Paragraph(calculation)))
                }
                
                document.add(table)
            } else {
                document.add(Paragraph("No calculations in history"))
            }
            
            // Footer
            document.add(Paragraph("\n"))
            val footer = Paragraph("Total Calculations: ${history.size}")
                .setFontSize(10f)
                .setItalic()
            document.add(footer)
            
            document.close()
            
            Log.d(TAG, "PDF exported successfully: ${file.absolutePath}")
            Result.success(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to PDF", e)
            Result.failure(e)
        }
    }
    
    /**
     * Export calculation history to CSV
     */
    fun exportHistoryToCsv(context: Context, history: List<String>): Result<File> {
        return try {
            val fileName = "calculator_history_${getTimestamp()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // Header
                writer.append("Number,Calculation,Date\n")
                
                // Data
                history.forEachIndexed { index, calculation ->
                    writer.append("${index + 1},\"$calculation\",${getCurrentDate()}\n")
                }
            }
            
            Log.d(TAG, "CSV exported successfully: ${file.absolutePath}")
            Result.success(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to CSV", e)
            Result.failure(e)
        }
    }
    
    /**
     * Share exported file
     */
    fun shareFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = when {
                    file.extension == "pdf" -> "application/pdf"
                    file.extension == "csv" -> "text/csv"
                    else -> "*/*"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Calculator History")
                putExtra(Intent.EXTRA_TEXT, "My calculation history from All-in-One Calculator")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Share History"))
            Log.d(TAG, "Share intent launched for: ${file.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing file", e)
        }
    }
    
    /**
     * Get current timestamp for filename
     */
    private fun getTimestamp(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Get current date for display
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
