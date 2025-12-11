package expo.modules.printerdrivers.utils.helpers

import android.content.Context
import android.graphics.BitmapFactory
import com.woosim.printer.WoosimCmd
import com.woosim.printer.WoosimImage
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object WoosimHelper {
    fun addAlignedString(
        str: String = "",
        align: Int = WoosimCmd.ALIGN_LEFT,
        bold: Boolean = false,
        doubleHeight: Boolean = false,
        charset: Charset = StandardCharsets.UTF_8,
    ): ByteArray {
        val estimatedSize = 64 + str.toByteArray(charset).size
        val buffer = ByteBuffer.allocate(estimatedSize)

        // Đặt font nhỏ nhất trước khi in
        buffer.put(WoosimCmd.extendFont(1, 1))

        fun setTextAttributes(bold: Boolean, doubleHeight: Boolean, align: Int) {
            if (bold) buffer.put(WoosimCmd.setBold(true))
            if (doubleHeight) buffer.put(WoosimCmd.extendFont(1, 2))
            buffer.put(WoosimCmd.setTextAlign(align))
        }

        fun resetTextAttributes(bold: Boolean, doubleHeight: Boolean) {
            if (bold) buffer.put(WoosimCmd.setBold(false))
            if (doubleHeight) buffer.put(WoosimCmd.extendFont(1, 1))
            buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
        }

        setTextAttributes(bold, doubleHeight, align)
        buffer.put(str.toByteArray(charset))
        resetTextAttributes(bold, doubleHeight)

        return CommonHelper.convertBufferToByteArray(buffer)
    }

    fun addTwoMarginedStrings(
        str1: String = "",
        str2: String = "",
        bold: Boolean = false,
        doubleHeight: Boolean = false,
        len: Int,
        charset: Charset = StandardCharsets.UTF_8,
    ): ByteArray {
        val estimatedSize = 64 + str1.toByteArray(charset).size + str2.toByteArray(charset).size
        val buffer = ByteBuffer.allocate(estimatedSize)

        fun setTextAttributes(bold: Boolean, doubleHeight: Boolean) {
            if (bold) buffer.put(WoosimCmd.setBold(true))
            if (doubleHeight) buffer.put(WoosimCmd.extendFont(1, 2))
        }

        fun resetTextAttributes(bold: Boolean, doubleHeight: Boolean) {
            if (bold) buffer.put(WoosimCmd.setBold(false))
            if (doubleHeight) buffer.put(WoosimCmd.extendFont(1, 1))
        }

        setTextAttributes(bold, doubleHeight)

        // Calculate actual lengths
        val str1Length = str1.length
        val str2Length = str2.length

        if (str1Length + str2Length >= len) {
            // If combined length exceeds limit, try to fit by removing space
            if (str1Length + str2Length == len + 1) {
                // Case where removing one space will make it fit
                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
                buffer.put(str1.ifBlank { " " }.toByteArray(charset))
                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_RIGHT))
                buffer.put(str2.ifBlank { " " }.toByteArray(charset))
                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
            } else {
                // If still too long even without space, then wrap
                val str = CommonHelper.createWrappedString("$str1 $str2", len)
                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
                buffer.put(str.toByteArray(charset))
                buffer.put(WoosimCmd.printLineFeed(0))
            }
        } else {
            // Normal case - enough space for both strings
            buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
            buffer.put(str1.ifBlank { " " }.toByteArray(charset))
            buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_RIGHT))
            buffer.put(str2.ifBlank { " " }.toByteArray(charset))
            buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
        }

        resetTextAttributes(bold, doubleHeight)

        return CommonHelper.convertBufferToByteArray(buffer)
    }

    fun addImage(context: Context, fileName: String): ByteArray {
        val imageFilePath = "${context.cacheDir}/$fileName"
        val imageFile = File(imageFilePath)

        val bitmap = FileInputStream(imageFile).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

        val arrBitmap = WoosimImage.printStdModeBitmap(bitmap)
        bitmap.recycle()

        val buffer = ByteBuffer.allocate(64 + arrBitmap.size).apply {
            put("\n".toByteArray()) // Have to add this to make sure the image is really center
            put(WoosimCmd.setAlignment(WoosimCmd.ALIGN_CENTER))
            put(arrBitmap)
            put(WoosimCmd.setAlignment(WoosimCmd.ALIGN_LEFT))
        }

        return CommonHelper.convertBufferToByteArray(buffer)
    }

    fun addThreeMarginedStrings(
        leftString: String,
        middleString: String,
        rightString: String,
        len: Int,
        leftBold: Boolean = false,
        middleBold: Boolean = false,
        rightBold: Boolean = false,
        allBold: Boolean = false,
        charset: Charset = StandardCharsets.UTF_8
    ): ByteArray {
        val left = leftString.trim { it <= ' ' }
        val middle = middleString.trim { it <= ' ' }
        val right = rightString.trim { it <= ' ' }

        val estimatedSize = 64 + left.toByteArray(charset).size + middle.toByteArray(charset).size + right.toByteArray(charset).size
        val buffer = ByteBuffer.allocate(estimatedSize)

        try {
            val totalLen = left.length + middle.length + right.length
            if (totalLen > len) {
                // Overflow: wrap the combined text
                val wrapped = CommonHelper.createWrappedString("$left $middle $right", len)
                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
                buffer.put(wrapped.toByteArray(charset))
                buffer.put(WoosimCmd.printLineFeed(0))
            } else {
                // Calculate spaces
                val sp = len / 2 + middle.length / 2 - (left.length + middle.length)
                val sp1 = maxOf(1, sp)
                val tempRe = (left + " ".repeat(sp1) + middle).trim { it <= ' ' }
                val sp2 = maxOf(1, len - (tempRe.length + right.length))

                val space1 = " ".repeat(sp1)
                val space2 = " ".repeat(sp2)

                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))

                // Left
                if (allBold || leftBold) buffer.put(WoosimCmd.setBold(true))
                buffer.put(left.ifBlank { " " }.toByteArray(charset))
                if (allBold || leftBold) buffer.put(WoosimCmd.setBold(false))

                // Space between left and middle
                buffer.put(space1.toByteArray(charset))

                // Middle
                if (allBold || middleBold) buffer.put(WoosimCmd.setBold(true))
                buffer.put(middle.ifBlank { " " }.toByteArray(charset))
                if (allBold || middleBold) buffer.put(WoosimCmd.setBold(false))

                // Space between middle and right
                buffer.put(space2.toByteArray(charset))

                // Right
                if (allBold || rightBold) buffer.put(WoosimCmd.setBold(true))
                buffer.put(right.ifBlank { " " }.toByteArray(charset))
                if (allBold || rightBold) buffer.put(WoosimCmd.setBold(false))

                buffer.put(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return CommonHelper.convertBufferToByteArray(buffer)
    }

    fun addLineFeed(lines: Int = 1): ByteArray {
        return WoosimCmd.printLineFeed(lines)
    }
}