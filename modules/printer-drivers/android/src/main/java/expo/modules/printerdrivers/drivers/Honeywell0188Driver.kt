package expo.modules.printerdrivers.drivers

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import android.util.Log
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import java.nio.ByteBuffer

class Honeywell0188Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName: String = "Honeywell0188Driver"

    override fun initPrinter() {
        TODO("Not yet implemented")
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        Log.d(driverName, "--> giayBaoTienNuocGiaDinhVer1: $jsonData")
    }
}