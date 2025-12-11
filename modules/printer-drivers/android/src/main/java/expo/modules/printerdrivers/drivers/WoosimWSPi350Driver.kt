package expo.modules.printerdrivers.drivers

import android.content.Context
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.helpers.CommonHelper
import expo.modules.printerdrivers.utils.helpers.WoosimHelper

class WoosimWSPi350Driver(
    bluetoothService: BluetoothService, context: Context
) : BaseDriver(bluetoothService, context) {
    override var driverName: String = "WoosimWSPi350Driver"
    override var printerPageWidth: Int = 36

    override fun initPrinter() {
        buffer.put(WoosimCmd.initPrinter())
    }

    override fun addAlignedStringToBuffer(
        string: String, align: Int, bold: Boolean, doubleFontSize: Boolean
    ) {
        val wrappedString = CommonHelper.createWrappedString(string, printerPageWidth)

        buffer.put(
            WoosimHelper.addAlignedString(
                wrappedString, align, bold, doubleFontSize
            )
        )
    }

    private fun addTwoMarginedStringsToBuffer(
        str1: String, str2: String, bold: Boolean = false, doubleHeight: Boolean = false
    ) {
        buffer.put(
            WoosimHelper.addTwoMarginedStrings(
                str1,
                str2,
                bold,
                doubleHeight,
                printerPageWidth
            )
        )
    }

    private fun addThreeMarginedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Boolean = false,
        middleBold: Boolean = false,
        rightBold: Boolean = false,
        allBold: Boolean = false
    ) {
        buffer.put(
            WoosimHelper.addThreeMarginedStrings(
                leftString,
                middleString,
                rightString,
                leftBold = leftBold,
                middleBold = middleBold,
                rightBold = rightBold,
                allBold = allBold,
                len = printerPageWidth - 1
            )
        )
    }

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        buffer.put(WoosimHelper.addLineFeed(lineNumber))
    }

    override fun addBitmapToBuffer(fileName: String, align: Int) {
        WoosimCmd.setTextAlign(align)
        buffer.put(WoosimHelper.addImage(context, fileName))
        if (align != WoosimCmd.ALIGN_LEFT) {
            WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT)
        }
    }
}