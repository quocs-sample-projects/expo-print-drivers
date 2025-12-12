package expo.modules.printerdrivers.drivers

import android.content.Context
import expo.modules.printerdrivers.services.bluetooth.BluetoothService

class Honeywell0188Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName: String = "Honeywell0188Driver"
    override var printerPageWidth: Int = 36

    override fun initPrinter() {
        TODO("Not yet implemented")
    }

    override fun addAlignedStringToBuffer(
        string: String,
        align: Int,
        bold: Boolean,
        doubleFontSize: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun addBitmapToBuffer(fileName: String, align: Int) {
        TODO("Not yet implemented")
    }

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        TODO("Not yet implemented")
    }
}