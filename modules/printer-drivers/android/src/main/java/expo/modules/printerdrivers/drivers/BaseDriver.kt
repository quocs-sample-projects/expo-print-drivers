package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService

abstract class BaseDriver (private val bluetoothService: BluetoothService) {
    abstract var driverName: String
    abstract fun giayBaoTienNuocNongThon(jsonData: ReadableMap)
}