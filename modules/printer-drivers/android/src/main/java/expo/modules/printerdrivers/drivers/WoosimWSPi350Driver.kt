package expo.modules.printerdrivers.drivers

import android.content.Context
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.helpers.CommonHelper
import expo.modules.printerdrivers.helpers.WoosimHelper
import expo.modules.printerdrivers.services.bluetooth.BluetoothService

class WoosimWSPi350Driver(
    bluetoothService: BluetoothService, context: Context
) : BaseDriver(bluetoothService, context) {
    override var driverName: String = "WoosimWSPi350Driver"
    override var printerPageWidth: Int = 35
    override var separateLineLength: Int = printerPageWidth

    override fun initPrinter() {
        buffer.put(WoosimCmd.initPrinter())
    }

    override fun addAlignedStringToBuffer(
        string: String, align: Int, bold: Boolean, doubleFontSize: Boolean
    ) {
        var actualString = string
        if (align == WoosimCmd.ALIGN_RIGHT) {
            actualString = actualString.trimEnd('\n') + " \n"
        }
        val wrappedString = CommonHelper.createWrappedString(actualString, printerPageWidth)

        buffer.put(
            WoosimHelper.addAlignedString(
                wrappedString, align, bold, doubleFontSize
            )
        )
    }

    override fun addThreeAlignedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Boolean,
        middleBold: Boolean,
        rightBold: Boolean,
        leftDoubleHeight: Boolean,
        middleDoubleHeight: Boolean,
        rightDoubleHeight: Boolean,
    ) {
        buffer.put(
            WoosimHelper.addThreeMarginedStrings(
                leftString,
                middleString,
                rightString,
                leftBold = leftBold,
                middleBold = middleBold,
                rightBold = rightBold,
                allBold = leftBold && middleBold && rightBold,
                pageLength = printerPageWidth
            )
        )
    }

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        buffer.put(WoosimCmd.printLineFeed(lineNumber))
    }

    override fun addBitmapToBuffer(fileName: String, align: Int) {
        WoosimCmd.setTextAlign(align)
        buffer.put(WoosimHelper.addImage(context, fileName))
        if (align != WoosimCmd.ALIGN_LEFT) {
            WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT)
        }
    }
}