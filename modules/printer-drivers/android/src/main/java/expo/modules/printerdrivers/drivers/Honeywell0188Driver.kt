package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import android.util.Log
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import java.nio.ByteBuffer

class Honeywell0188Driver(private val bluetoothService: BluetoothService): BaseDriver(bluetoothService) {
    override var driverName: String = "Honeywell0188Driver"
    override var buffer: ByteBuffer = ByteBuffer.allocate(512 * 30)

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        Log.d(driverName, "--> giayBaoTienNuocGiaDinhVer1: ${jsonData.toString()}")
    }
}