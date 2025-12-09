package expo.modules.printerdrivers.drivers

import android.content.Context
import android.graphics.BitmapFactory
import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import expo.modules.printerdrivers.utils.constants.PR3Command
import expo.modules.printerdrivers.utils.helpers.CommonHelper
import honeywell.printer.DocumentLP
import java.io.File

class HoneywellPR3Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName = "HoneywellPR3Driver"
    override var printerPageWidth: Int = 53
    override var separateLineLength: Int = 71

    override fun initPrinter() {
        // buffer.put(PR3Command.INIT)
    }

    override fun addAlignedStringToBuffer(
        string: String,
        align: Int,
        bold: Boolean,
        doubleFontSize: Boolean
    ) {
        val wrappedString = CommonHelper.createWrappedString(string, printerPageWidth)
        
        buffer.put(wrappedString.toByteArray())
    }

    override fun addBitmapToBuffer(fileName: String) {
        val docLP = DocumentLP("!")

        try {
            val qrImagePath = "${context.cacheDir}/$fileName"
            val imageFile = File(qrImagePath)

            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(qrImagePath)
                if (bitmap != null) {
                    // PR3 print head width is 576 dots
                    docLP.writeImage(bitmap, 576)
                    bitmap.recycle()
                } else {
                    docLP.writeText("ERROR: Failed to decode image")
                }
            } else {
                docLP.writeText("ERROR: $fileName not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            docLP.writeText("ERROR: ${e.message}")
        }

        buffer.put(docLP.documentData)
    }

    override fun addLineFeedToBuffer(lineNumber: Int) {
        for (i in 1..lineNumber) {
            buffer.put(PR3Command.NEW_LINE)
        }
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {

        addSeparateLineToBuffer()
        addAlignedStringToBuffer("Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n")
//
//        buffer.put(PR3Command.BOLD_ON)
//        buffer.put("Dòng chữ đậm\n".toByteArray())
//        buffer.put(PR3Command.BOLD_OFF)
//
//        buffer.put(PR3Command.DOUBLE_WIDE_ON)
//        buffer.put("Dòng chữ gấp đôi chiều rộng\n".toByteArray())
//        buffer.put(PR3Command.DOUBLE_WIDE_OFF)
//
//        buffer.put(PR3Command.DOUBLE_HIGH_ON)
//        buffer.put("Dòng chữ gấp đôi chiều cao\n".toByteArray())
//        buffer.put(PR3Command.DOUBLE_HIGH_OFF)
//
//        buffer.put(PR3Command.DOUBLE_WH_ON)
//        buffer.put("Dòng chữ gấp đôi chiều cao và chiều rộng\n".toByteArray())
//        buffer.put(PR3Command.DOUBLE_WH_OFF)
//
//        buffer.put("Dòng chữ bình thường\n".toByteArray())
//
//        addBitmapToBuffer("ma_qr.png")
//
        addSeparateLineToBuffer()

        buffer.put(PR3Command.NEW_LINE)
        buffer.put(PR3Command.NEW_LINE)
    }
}
