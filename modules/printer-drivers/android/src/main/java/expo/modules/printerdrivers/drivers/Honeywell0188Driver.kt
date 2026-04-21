package expo.modules.printerdrivers.drivers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.rt.printerlibrary.cmd.EscCmd
import com.rt.printerlibrary.cmd.EscFactory
import com.rt.printerlibrary.enumerate.BmpPrintMode
import com.rt.printerlibrary.enumerate.ESCFontTypeEnum
import com.rt.printerlibrary.enumerate.SettingEnum
import com.rt.printerlibrary.setting.BitmapSetting
import com.rt.printerlibrary.setting.CommonSetting
import com.rt.printerlibrary.setting.TextSetting
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.helpers.CommonHelper
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import java.io.File

class Honeywell0188Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName: String = "Honeywell0188Driver"
    override var printerPageWidth: Int = 46
    override var separateLineLength: Int = printerPageWidth

    val escCmd: EscCmd = EscFactory().create()
    val commonSetting = CommonSetting()

    private fun createAlignedBitmap(originalBitmap: Bitmap, align: Int): Bitmap {
        // Calculate printer width in pixels (assuming 203 DPI)
        // printerPageWidth is in characters, roughly 36 chars * 12 dots = 432 dots
        val printerWidthDots = printerPageWidth * 12

        // Create a full-width bitmap with white background
        val alignedBitmap = createBitmap(printerWidthDots, originalBitmap.height)
        val canvas = Canvas(alignedBitmap)
        canvas.drawColor(Color.WHITE)

        // Calculate x position based on alignment
        val x = when (align) {
            WoosimCmd.ALIGN_CENTER -> ((printerWidthDots - originalBitmap.width) / 2).toFloat()
            WoosimCmd.ALIGN_RIGHT -> (printerWidthDots - originalBitmap.width).toFloat()
            else -> 0f // Left aligned
        }

        // Draw the original bitmap at the calculated position
        canvas.drawBitmap(originalBitmap, x.coerceAtLeast(0f), 0f, null)

        return alignedBitmap
    }

    override fun initPrinter() {
        commonSetting.align = WoosimCmd.ALIGN_LEFT

        escCmd.append(escCmd.headerCmd)
        escCmd.chartsetName = "UTF-8"
        escCmd.append(escCmd.getCommonSettingCmd(commonSetting))
    }

    override fun addAlignedStringToBuffer(
        string: String,
        align: Int,
        bold: Boolean,
        doubleFontSize: Boolean
    ) {
        try {
            var actualString = string
            if (align == WoosimCmd.ALIGN_RIGHT) {
                actualString = actualString.trimEnd('\n') + "  \n"
            }
            val wrappedString = CommonHelper.createWrappedString(actualString, printerPageWidth)

            val setting = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                this.align = align
                this.bold = if (bold) SettingEnum.Enable else SettingEnum.Disable
                doubleHeight = if (doubleFontSize) SettingEnum.Enable else SettingEnum.Disable
                doubleWidth = if (doubleFontSize) SettingEnum.Enable else SettingEnum.Disable
            }

            escCmd.append(escCmd.getTextCmd(setting, wrappedString))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun addBitmapToBuffer(fileName: String, align: Int) {
        try {
            val imagePath = "${context.cacheDir}/$fileName"
            val imageFile = File(imagePath)

            if (!imageFile.exists()) {
                val errorSetting = TextSetting()
                errorSetting.align = WoosimCmd.ALIGN_LEFT
                escCmd.append(escCmd.getTextCmd(errorSetting, "ERROR: $fileName not found\n"))
                return
            }

            val originalBitmap = BitmapFactory.decodeFile(imagePath)
            if (originalBitmap == null) {
                val errorSetting = TextSetting()
                errorSetting.align = WoosimCmd.ALIGN_LEFT
                escCmd.append(escCmd.getTextCmd(errorSetting, "ERROR: Failed to decode image\n"))
                return
            }

            val bitmapToUse = if (align != WoosimCmd.ALIGN_LEFT) {
                createAlignedBitmap(originalBitmap, align)
            } else {
                originalBitmap
            }

            val bitmapSetting = BitmapSetting()
            bitmapSetting.bimtapLimitWidth = printerPageWidth * 8
            bitmapSetting.bmpPrintMode = BmpPrintMode.MODE_SINGLE_COLOR

            escCmd.append(escCmd.getBitmapCmd(bitmapSetting, bitmapToUse))

            if (bitmapToUse != originalBitmap) {
                bitmapToUse.recycle()
            }
            originalBitmap.recycle()

        } catch (e: Exception) {
            e.printStackTrace()
            val errorSetting = TextSetting()
            errorSetting.align = WoosimCmd.ALIGN_LEFT
            escCmd.append(escCmd.getTextCmd(errorSetting, "ERROR: ${e.message}\n"))
        }
    }

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        repeat(lineNumber) {
            escCmd.append(escCmd.lfcrCmd)
        }
    }

    override fun addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Boolean,
        rightBold: Boolean,
        leftDoubleHeight: Boolean,
        rightDoubleHeight: Boolean
    ) {
        try {
            // Calculate spacing manually
            val totalLength = leftString.length + rightString.length
            val spacesNeeded = (printerPageWidth - totalLength).coerceAtLeast(1)

            // Format the line with spaces between left and right
            val formattedLine = leftString + " ".repeat(spacesNeeded) + rightString

            // Use combined styling (if either is bold/double, apply to whole line)
            val useBold = leftBold || rightBold
            val useDouble = leftDoubleHeight || rightDoubleHeight

            val setting = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                align = WoosimCmd.ALIGN_LEFT
                bold = if (useBold) SettingEnum.Enable else SettingEnum.Disable
                doubleHeight = if (useDouble) SettingEnum.Enable else SettingEnum.Disable
                doubleWidth = if (useDouble) SettingEnum.Enable else SettingEnum.Disable
            }

            escCmd.append(escCmd.getTextCmd(setting, formattedLine))
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        rightDoubleHeight: Boolean
    ) {
        try {
            // Calculate middle text starting position (centered in printer width)
            val middleStartPos = (printerPageWidth / 2 - middleString.length / 2).coerceAtLeast(0)

            // Calculate left spacing (from end of left text to start of middle text)
            val leftSpaces = (middleStartPos - leftString.length).coerceAtLeast(1)

            // Calculate right spacing (from end of middle text to start of right text)
            val middleEndPos = middleStartPos + middleString.length
            val rightStartPos = printerPageWidth - rightString.length
            val rightSpaces = (rightStartPos - middleEndPos).coerceAtLeast(1)

            // Format the line with three columns
            val formattedLine =
                leftString + " ".repeat(leftSpaces) + middleString + " ".repeat(rightSpaces) + rightString

            // Use combined styling (if any is bold/double, apply to whole line)
            val useBold = leftBold || middleBold || rightBold
            val useDouble = leftDoubleHeight || middleDoubleHeight || rightDoubleHeight

            val setting = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                align = WoosimCmd.ALIGN_LEFT
                bold = if (useBold) SettingEnum.Enable else SettingEnum.Disable
                doubleHeight = if (useDouble) SettingEnum.Enable else SettingEnum.Disable
                doubleWidth = if (useDouble) SettingEnum.Enable else SettingEnum.Disable
            }

            escCmd.append(escCmd.getTextCmd(setting, formattedLine))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clearBuffer() {
        escCmd.clear()
        super.clearBuffer()
    }

    override fun sendPrintData() {
        buffer.put(escCmd.appendCmds)
        super.sendPrintData()
    }
}