package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService

class HoneywellPR3Driver(bluetoothService: BluetoothService) : BaseDriver(bluetoothService) {
    override var driverName = "HoneywellPR3Driver"

    override fun initPrinter() {
        TODO("Not yet implemented")
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        println("--> HoneywellPR3Driver: $jsonData")
    }
}
