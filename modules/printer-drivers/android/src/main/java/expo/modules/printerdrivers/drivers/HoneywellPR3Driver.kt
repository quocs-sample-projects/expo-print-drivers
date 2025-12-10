package expo.modules.printerdrivers.drivers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.facebook.react.bridge.ReadableMap
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.constants.PR3Command
import honeywell.printer.DocumentLP
import java.io.File
import androidx.core.graphics.createBitmap
import expo.modules.printerdrivers.utils.constants.PrinterCharacter

class HoneywellPR3Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName = "HoneywellPR3Driver"
    override var printerPageWidth: Int = 53
    override var separateLineLength: Int = 71
    var imageHeadWidth: Int = 576 // in dots

    override fun initPrinter() {
        buffer.put(PR3Command.INIT)
    }

    private fun wrapTextToWidth(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth <= maxWidth) {
                currentLine = StringBuilder(testLine)
            } else {
                // Current line is full, save it and start new line
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                } else {
                    // Single word is too long, need to break it
                    lines.add(word)
                }
            }
        }

        // Add the last line
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }

        return lines
    }

    private fun createTextBitmap(
        text: String, align: Int, bold: Boolean, doubleFontSize: Boolean,
    ): Bitmap {
        // Configure paint
        val paint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false // Sharper text for thermal printers
            textSize = if (doubleFontSize) 32f else 24f
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        // Measure text metrics
        val fontMetrics = paint.fontMetrics
        val lineHeight = (fontMetrics.descent - fontMetrics.ascent).toInt()
        val textPadding = 4f

        // Calculate available width for text (with margins)
        val availableWidth = imageHeadWidth - (textPadding * 2)

        // Wrap text into multiple lines that fit within available width
        val wrappedLines = wrapTextToWidth(text, paint, availableWidth)

        // Calculate total bitmap height
        val totalHeight = (lineHeight * wrappedLines.size) + 8 // 4px padding top and bottom

        // Create bitmap
        val bitmap = createBitmap(imageHeadWidth, totalHeight)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // Draw each line
        var currentY = -fontMetrics.ascent + 4f // Start position with top padding
        for (line in wrappedLines) {
            val lineWidth = paint.measureText(line)

            // Calculate x position based on alignment
            val x = when (align) {
                WoosimCmd.ALIGN_CENTER -> (imageHeadWidth - lineWidth) / 2
                WoosimCmd.ALIGN_RIGHT -> imageHeadWidth - lineWidth - textPadding
                else -> textPadding // Left with margin
            }

            canvas.drawText(line, x, currentY, paint)
            currentY += lineHeight
        }

        return bitmap
    }

    override fun addAlignedStringToBuffer(
        string: String, align: Int, bold: Boolean, doubleFontSize: Boolean
    ) {
        val text = string.trimEnd('\n')
        if (text.isEmpty()) return

        // Render text as bitmap with proper alignment
        val bitmap = createTextBitmap(text, align, bold, doubleFontSize)

        val docLP = DocumentLP("!")
        docLP.writeImage(bitmap, imageHeadWidth)
        buffer.put(docLP.documentData)

        bitmap.recycle()
    }

    override fun addBitmapToBuffer(fileName: String, align: Int) {
        val docLP = DocumentLP("!")

        try {
            val qrImagePath = "${context.cacheDir}/$fileName"
            val imageFile = File(qrImagePath)

            if (imageFile.exists()) {
                val originalBitmap = BitmapFactory.decodeFile(qrImagePath)
                if (originalBitmap != null) {
                    // Create aligned bitmap wrapper
                    val alignedBitmap = createAlignedBitmap(originalBitmap, align)
                    docLP.writeImage(alignedBitmap, imageHeadWidth)
                    alignedBitmap.recycle()
                    originalBitmap.recycle()
                } else {
                    docLP.writeText("ERROR: Failed to decode image")
                }
            } else {
                docLP.writeText("ERROR: $fileName not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            docLP.writeText("ERROR: ${e.message}")
        }

        buffer.put(docLP.documentData)
    }

    private fun createAlignedBitmap(originalBitmap: Bitmap, align: Int): Bitmap {
        // Create a full-width bitmap with white background
        val alignedBitmap = createBitmap(imageHeadWidth, originalBitmap.height)
        val canvas = Canvas(alignedBitmap)
        canvas.drawColor(Color.WHITE)

        // Calculate x position based on alignment
        val x = when (align) {
            WoosimCmd.ALIGN_CENTER -> ((imageHeadWidth - originalBitmap.width) / 2).toFloat()
            WoosimCmd.ALIGN_RIGHT -> (imageHeadWidth - originalBitmap.width).toFloat()
            else -> 0f // Left aligned
        }

        // Draw the original bitmap at the calculated position
        canvas.drawBitmap(originalBitmap, x, 0f, null)

        return alignedBitmap
    }

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        for (i in 1..lineNumber) {
            buffer.put(PR3Command.NEW_LINE)
        }
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
//        addSeparateLineToBuffer()
//        addAlignedStringToBuffer(
//            "CTY CỔ PHẦN CẤP NƯỚC CẤP CẤP", WoosimCmd.ALIGN_CENTER, bold = true
//        )
//        addSeparateLineToBuffer()
//        addAlignedStringToBuffer(
//            "PHIẾU BÁO CHỈ SỐ ĐO", WoosimCmd.ALIGN_CENTER, bold = true, doubleFontSize = true
//        )
//        addAlignedStringToBuffer("KỲ 12/2025", WoosimCmd.ALIGN_CENTER, bold = true)
        addAlignedStringToBuffer("04/11/2025 - 04/12/2025", WoosimCmd.ALIGN_CENTER)
        addAlignedStringToBuffer("DB: 0123456789 - MLT: 01234567", bold = true)
        addAlignedStringToBuffer("KH: NGUYEN VAN A", bold = true)
        addAlignedStringToBuffer("Điện thoại KH: 0123456789")
        addAlignedStringToBuffer("ĐC: 123 Đường Đi Hoài Sẽ Thấy, Phường Còn Lâu Mới Nói, TP.HCM")
        addAlignedStringToBuffer("Giá biểu: 21 - Định mức: 69")
        addTwoAlignedStringsToBuffer(
            leftString = "Chỉ số lala",
            rightString = "1600 ${PrinterCharacter.M3}",
            rightBold = true
        )
        addAlignedStringToBuffer(
            "Quét mã QR . để thanh toán MOMO", WoosimCmd.ALIGN_CENTER, bold = true
        )
//        addLineFeedsToBuffer()
//        addBitmapToBuffer("ma_qr.png", WoosimCmd.ALIGN_CENTER)
        addLineFeedsToBuffer(3)
    }
}
