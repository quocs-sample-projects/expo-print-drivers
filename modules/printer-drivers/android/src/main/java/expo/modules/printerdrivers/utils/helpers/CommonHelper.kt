package expo.modules.printerdrivers.utils.helpers

import com.woosim.printer.WoosimCmd
import java.nio.ByteBuffer

object CommonHelper {
    fun createSeparatorLine(length: Int): ByteArray {
        var line = "-".repeat(length)
        line += "\n"
        return line.toByteArray()
    }

    fun createWrappedString(source: String?, len: Int): String {
        if (source == null) {
            return ""
        }

        var lastSpace = if (source.length > len) source.lastIndexOf(" ", len) else -1
        val result = StringBuilder(if (lastSpace > 0) source.take(lastSpace) else source)
        while (result.length + 1 < source.length) {
            if (result.length + len < source.length) {
                val end = minOf(result.length + 1 + len, source.length)
                lastSpace = source.lastIndexOf(" ", end)
                if (lastSpace > result.length) {
                    result.append("\n").append(source, result.length, lastSpace)
                } else {
                    result.append("\n").append(source, result.length, end)
                }
            } else {
                result.append("\n").append(source, result.length, source.length)
            }
        }
        return result.toString()
    }

    fun createWrappedStringArray(source: String, wrappedLength: Int): List<String> {
        val hasEndLine = source.endsWith("\n")
        val baseString = source.filterNot { it == '\n' }
        val wordsList: List<String> = baseString.split("\\s+".toRegex())

        val result = mutableListOf<String>()
        var i = 0
        var bufferString = ""
        while (i < wordsList.size) {
            val currentWord = wordsList[i]
            if (bufferString.length + currentWord.length < wrappedLength) {
                bufferString += "$currentWord "
                i++
            } else {
                result.add("${bufferString.trim()}\n")
                bufferString = ""
            }
        }
        if (bufferString.isNotEmpty()) {
            if (hasEndLine) result.add("${bufferString.trim()}\n")
            else result.add(bufferString.trim())
        }

        return result
    }

    fun createStringPadding(source: String, wrappedLength: Int, align: Int): String {
        var result = ""
        when (align) {
            WoosimCmd.ALIGN_LEFT
                -> result = ""
            WoosimCmd.ALIGN_CENTER
                -> result = " ".repeat((wrappedLength - source.length) / 2)
            WoosimCmd.ALIGN_RIGHT
                -> result =  " ".repeat(wrappedLength - source.length)
        }
        return result
    }

    fun convertBufferToByteArray(buffer: ByteBuffer): ByteArray {
        val result = ByteArray(buffer.position())
        buffer.flip()
        buffer.get(result)
        return result
    }
}