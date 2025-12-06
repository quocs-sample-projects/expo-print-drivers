package expo.modules.printerdrivers.drivers

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import expo.modules.printerdrivers.utils.constants.MemoryUnit.KiB
import java.nio.ByteBuffer

abstract class BaseDriver(
    private val bluetoothService: BluetoothService,
    val context: Context
) {
    var buffer: ByteBuffer = ByteBuffer.allocate(50 * KiB)
    abstract var driverName: String


    protected fun clearBuffer() {
        buffer.clear()
    }

    protected fun sendPrintData() {
        // Only send the data that was actually written to the buffer
        val data = ByteArray(buffer.position())
        buffer.rewind()
        buffer.get(data)
        bluetoothService.write(data)
    }

    abstract fun initPrinter()
    abstract fun giayBaoTienNuocNongThon(jsonData: ReadableMap)
}