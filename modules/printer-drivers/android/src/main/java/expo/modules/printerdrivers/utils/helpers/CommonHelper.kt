package expo.modules.printerdrivers.utils.helpers

import java.nio.ByteBuffer

object CommonHelper {
    fun createSeparatorLine(length: Int): ByteArray {
        var line = "-".repeat(length)
        line+="\n"
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

    fun convertBufferToByteArray(buffer: ByteBuffer): ByteArray {
        val result = ByteArray(buffer.position())
        buffer.flip()
        buffer.get(result)
        return result
    }
}