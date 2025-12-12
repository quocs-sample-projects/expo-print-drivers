package expo.modules.printerdrivers.utils.helpers

import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import java.nio.ByteBuffer

object CommonHelper {
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

    private fun checkNullEmptyBlank(strToCheck: String, replaceText: String?): String {
        val replacement = replaceText ?: ""
        return if (strToCheck.isEmpty() || strToCheck.isBlank()) replacement else strToCheck
    }

    fun getStringValueByKey(
        readableMap: ReadableMap,
        key: String?,
        replaceValue: String? = null
    ): String {
        val replacement = replaceValue ?: ""
        if (key != null && readableMap.hasKey(key)) {
            return when (readableMap.getType(key)) {
                ReadableType.Number -> {
                    val tmp = readableMap.getDouble(key)
                    if (tmp == tmp.toInt().toDouble()) {
                        checkNullEmptyBlank(tmp.toInt().toString(), replacement)
                    } else {
                        checkNullEmptyBlank(tmp.toString(), replacement)
                    }
                }

                ReadableType.String -> checkNullEmptyBlank(
                    readableMap.getString(key)!!,
                    replacement
                )

                else -> replacement
            }
        }
        return replacement
    }
}