package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import java.nio.ByteBuffer

abstract class BaseDriver (private val bluetoothService: BluetoothService) {
    abstract var driverName: String
    abstract var buffer: ByteBuffer
    abstract fun giayBaoTienNuocNongThon(jsonData: ReadableMap)
}