package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import android.util.Log
import expo.modules.printerdrivers.bluetoothService.BluetoothService

class WoosimWSPi350Driver(private val bluetoothService: BluetoothService) : BaseDriver(bluetoothService) {
    override var driverName: String = "WoosimWSPi350Driver"

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        Log.d(driverName, "--> giayBaoTienNuocGiaDinhVer1: ${jsonData.toString()}")
    }
}