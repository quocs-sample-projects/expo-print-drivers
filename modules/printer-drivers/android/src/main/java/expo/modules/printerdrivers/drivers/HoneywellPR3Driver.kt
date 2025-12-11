package expo.modules.printerdrivers.drivers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.constants.PR3Command
import honeywell.printer.DocumentLP
import java.io.File
import androidx.core.graphics.createBitmap

private object FontSize {
    const val NORMAL = 24f
    const val DOUBLE = 32f
}

class HoneywellPR3Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName = "HoneywellPR3Driver"
    override var printerPageWidth: Int = 44
    var imageHeadWidth: Int = 576 // in dots

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
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                } else {
                    lines.add(word)
                }
            }
        }

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
            textSize = if (doubleFontSize) FontSize.DOUBLE else FontSize.NORMAL
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

    private fun createTwoAlignedTextBitmap(
        leftText: String,
        rightText: String,
        leftBold: Boolean,
        rightBold: Boolean,
        leftDoubleSize: Boolean,
        rightDoubleSize: Boolean
    ): Bitmap {
        // Configure paints for left and right text
        val leftPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
            textSize = if (leftDoubleSize) FontSize.DOUBLE else FontSize.NORMAL
            typeface = if (leftBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        val rightPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
            textSize = if (rightDoubleSize) FontSize.DOUBLE else FontSize.NORMAL
            typeface = if (rightBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        // Calculate heights
        val leftHeight = leftPaint.fontMetrics.let { it.descent - it.ascent }
        val rightHeight = rightPaint.fontMetrics.let { it.descent - it.ascent }
        val maxHeight = maxOf(leftHeight, rightHeight).toInt() + 8

        val textPadding = 4f

        // Create bitmap
        val bitmap = createBitmap(imageHeadWidth, maxHeight)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // Draw left text
        if (leftText.isNotEmpty()) {
            val leftY = -leftPaint.fontMetrics.ascent + 4f
            canvas.drawText(leftText, textPadding, leftY, leftPaint)
        }

        // Draw right text
        if (rightText.isNotEmpty()) {
            val rightWidth = rightPaint.measureText(rightText)
            val rightX = imageHeadWidth - rightWidth - textPadding
            val rightY = -rightPaint.fontMetrics.ascent + 4f
            canvas.drawText(rightText, rightX, rightY, rightPaint)
        }

        return bitmap
    }

    private fun createAlignedBitmap(originalBitmap: Bitmap, align: Int): Bitmap {
        val alignedBitmap = createBitmap(imageHeadWidth, originalBitmap.height)
        val canvas = Canvas(alignedBitmap)
        canvas.drawColor(Color.WHITE)

        // Calculate x position based on alignment
        val x = when (align) {
            WoosimCmd.ALIGN_CENTER -> ((imageHeadWidth - originalBitmap.width) / 2).toFloat()
            WoosimCmd.ALIGN_RIGHT -> (imageHeadWidth - originalBitmap.width).toFloat()
            else -> 0f // Left aligned
        }
        canvas.drawBitmap(originalBitmap, x, 0f, null)

        return alignedBitmap
    }

    override fun initPrinter() {
        buffer.put(PR3Command.INIT)
    }

    /**
     * Because we can't align normal texts, we have to print them as bitmaps so that we can align them
     * .When printing "text image" like this. We don't have to add "\n" to the end of the string to add new line
     * ,the current text will automatically add new line after printing
     */
    override fun addAlignedStringToBuffer(
        string: String, align: Int, bold: Boolean, doubleFontSize: Boolean
    ) {
        val text = string.trimEnd('\n')
        if (text.isEmpty()) return

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

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        repeat(lineNumber) {
            buffer.put(PR3Command.NEW_LINE)

        }
    }

    override fun addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Boolean,
        rightBold: Boolean,
        leftDoubleHeight: Boolean,
        rightDoubleHeight: Boolean,
    ) {
        val leftText = leftString.trimEnd('\n')
        val rightText = rightString.trimEnd('\n')

        if (leftText.isEmpty() && rightText.isEmpty()) return

        val bitmap = createTwoAlignedTextBitmap(
            leftText, rightText, leftBold, rightBold, leftDoubleHeight, rightDoubleHeight
        )

        val docLP = DocumentLP("!")
        docLP.writeImage(bitmap, imageHeadWidth)
        buffer.put(docLP.documentData)
        bitmap.recycle()
    }

    override fun addThreeAlignedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Boolean,
        middleBold: Boolean,
        rightBold: Boolean,
        leftDoubleHeight: Boolean,
        middleDoubleHeight: Boolean,
        rightDoubleHeight: Boolean,
    ) {
        TODO("Not yet implemented")
    }
}
