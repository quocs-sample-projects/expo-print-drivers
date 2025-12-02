package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import android.util.Log
import expo.modules.printerdrivers.bluetoothService.BluetoothService

class Honeywell0188Driver(private val bluetoothService: BluetoothService): BaseDriver(bluetoothService) {
    override var driverName: String = "Honeywell0188Driver"

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        Log.d(driverName, "--> giayBaoTienNuocGiaDinhVer1: ${jsonData.toString()}")
    }
}