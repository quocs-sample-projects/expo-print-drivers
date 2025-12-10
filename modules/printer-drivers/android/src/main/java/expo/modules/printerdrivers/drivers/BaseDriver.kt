package expo.modules.printerdrivers.drivers

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.constants.MemoryUnit.KiB
import expo.modules.printerdrivers.utils.helpers.CommonHelper
import java.nio.ByteBuffer

abstract class BaseDriver(
    private val bluetoothService: BluetoothService, val context: Context
) {
    var buffer: ByteBuffer = ByteBuffer.allocate(50 * KiB)
    abstract var driverName: String
    abstract var printerPageWidth: Int
    abstract var separateLineLength: Int

    fun clearBuffer() {
        buffer.clear()
    }

    fun sendPrintData() {
        // Only send the data that was actually written to the buffer
        val data = ByteArray(buffer.position())
        buffer.rewind()
        buffer.get(data)
        bluetoothService.write(data)
    }

    fun addSeparateLineToBuffer() {
        buffer.put(CommonHelper.createSeparatorLine(separateLineLength))
    }

    abstract fun initPrinter()
    abstract fun addAlignedStringToBuffer(
        string: String,
        align: Int = WoosimCmd.ALIGN_LEFT,
        bold: Boolean = false,
        doubleFontSize: Boolean = false
    )

    fun addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Boolean = false,
        rightBold: Boolean = false,
        leftDoubleHeight: Boolean = false,
        rightDoubleHeight: Boolean = false,
    ) {
        addAlignedStringToBuffer(
            leftString, bold = leftBold, doubleFontSize = leftDoubleHeight
        )
        addAlignedStringToBuffer(
            rightString, WoosimCmd.ALIGN_RIGHT, bold = rightBold, doubleFontSize = rightDoubleHeight
        )
    }

    abstract fun addBitmapToBuffer(fileName: String, align: Int = WoosimCmd.ALIGN_LEFT)
    abstract fun addLineFeedsToBuffer(lineNumber: Int = 1)
    abstract fun giayBaoTienNuocNongThon(jsonData: ReadableMap)
}