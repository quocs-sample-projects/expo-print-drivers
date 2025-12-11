package expo.modules.printerdrivers.drivers

import android.content.Context
import android.util.Log
import com.facebook.react.bridge.ReadableMap
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.constants.MemoryUnit.KB
import expo.modules.printerdrivers.utils.constants.PrinterCharacter
import expo.modules.printerdrivers.utils.helpers.CommonHelper.getStringValueByKey
import java.nio.ByteBuffer

abstract class BaseDriver(
    private val bluetoothService: BluetoothService, val context: Context
) {
    var buffer: ByteBuffer = ByteBuffer.allocate(50 * KB)
    abstract var driverName: String
    abstract var printerPageWidth: Int
    abstract var separateLineLength: Int

    abstract fun addSeparateLineToBuffer()
    abstract fun initPrinter()
    abstract fun addAlignedStringToBuffer(
        string: String,
        align: Int = WoosimCmd.ALIGN_LEFT,
        bold: Boolean = false,
        doubleFontSize: Boolean = false
    )

    abstract fun addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Boolean = false,
        rightBold: Boolean = false,
        leftDoubleHeight: Boolean = false,
        rightDoubleHeight: Boolean = false,
    )

    abstract fun addThreeAlignedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Boolean = false,
        middleBold: Boolean = false,
        rightBold: Boolean = false,
    )

    abstract fun addBitmapToBuffer(fileName: String, align: Int = WoosimCmd.ALIGN_LEFT)
    abstract fun addLineFeedsToBuffer(lineNumber: Int = 1)

    fun clearBuffer() {
        buffer.clear()
    }

    fun sendPrintData() {
        // Only send the data that was actually written to the buffer
        val data = ByteArray(buffer.position())
        buffer.rewind()
        buffer.get(data)
        bluetoothService.write(data)
    }

    fun testGiayBaoTienNuoc(jsonData: ReadableMap) {
        Log.d("testGiayBaoTienNuoc", "--> jsonData: $jsonData")

        val tenCongTy = getStringValueByKey(jsonData, "tenCongTy")
        val tenPhieu = getStringValueByKey(jsonData, "tenPhieu")
        val ky = getStringValueByKey(jsonData, "ky")
        val tuNgay = getStringValueByKey(jsonData, "tuNgay")
        val denNgay = getStringValueByKey(jsonData, "denNgay")
        val mdb = getStringValueByKey(jsonData, "mdb")
        val mlt = getStringValueByKey(jsonData, "mlt")
        val khachHang = getStringValueByKey(jsonData, "khachHang")
        val soDienThoai = getStringValueByKey(jsonData, "soDienThoai")
        val diaChi = getStringValueByKey(jsonData, "diaChi")
        val giaBieu = getStringValueByKey(jsonData, "giaBieu")
        val dinhMuc = getStringValueByKey(jsonData, "dinhMuc")
        val chiSo = getStringValueByKey(jsonData, "chiSo")
        val tienNuoc = getStringValueByKey(jsonData, "tienNuoc")
        val tienKyMoi = getStringValueByKey(jsonData, "tienKyMoi")
        val nhanVien = getStringValueByKey(jsonData, "nhanVien")
        val dienThoaiNhanVien = getStringValueByKey(jsonData, "dienThoaiNhanVien")
        val maQR = getStringValueByKey(jsonData, "maQR")


        addSeparateLineToBuffer()
        addAlignedStringToBuffer(
            "$tenCongTy\n", WoosimCmd.ALIGN_CENTER, bold = true
        )
        addSeparateLineToBuffer()
        addAlignedStringToBuffer(
            "$tenPhieu\n", WoosimCmd.ALIGN_CENTER, bold = true, doubleFontSize = true
        )
        addAlignedStringToBuffer("KỲ: $ky\n", WoosimCmd.ALIGN_CENTER, bold = true)
        addAlignedStringToBuffer("$tuNgay - $denNgay\n", WoosimCmd.ALIGN_CENTER)
        addAlignedStringToBuffer("DB: $mdb - MLT: $mlt\n", bold = true)
        addAlignedStringToBuffer("KH: $khachHang\n", bold = true)
        addAlignedStringToBuffer("Điện thoại KH: $soDienThoai\n")
        addAlignedStringToBuffer("ĐC: $diaChi\n")
        addAlignedStringToBuffer("Giá biểu: $giaBieu - Định mức: $dinhMuc\n")
        addTwoAlignedStringsToBuffer(
            leftString = "Chỉ số lala",
            rightString = "$chiSo ${PrinterCharacter.M3}\n",
            rightBold = true
        )
        addTwoAlignedStringsToBuffer(
            leftString = "Tiền hehe",
            rightString = "$tienNuoc ${PrinterCharacter.VND}\n",
            rightBold = true
        )
        addAlignedStringToBuffer("${"-".repeat(10)}\n", WoosimCmd.ALIGN_RIGHT)
        addTwoAlignedStringsToBuffer(
            leftString = "Số tiền (kỳ mới)",
            rightString = "$tienKyMoi ${PrinterCharacter.VND}\n",
            rightBold = true
        )
        addSeparateLineToBuffer()
        addAlignedStringToBuffer("NV: $nhanVien\n", bold = true)
        addAlignedStringToBuffer("ĐT: $dienThoaiNhanVien\n", bold = true)
        addAlignedStringToBuffer("Sau 3 ngày làm việc, kể từ ngày ghi chỉ số nước, dữ liệu hoá đơn sẽ được cập nhật tại website:\n")
        addAlignedStringToBuffer("https://www.example.com\n", bold = true)
        addAlignedStringToBuffer("Quý khách vui lòng kiểm tra lại số điện thoại trên phiếu báo này và liên hệ đội làm giàu:\n")
        addAlignedStringToBuffer("(0123) 456789 để cập nhật lại nếu chưa chính xác.\n")
        addLineFeedsToBuffer()
        addAlignedStringToBuffer(
            "Quét mã QR để thanh toán MOMO\n", WoosimCmd.ALIGN_CENTER, bold = true
        )
        addBitmapToBuffer(maQR, WoosimCmd.ALIGN_CENTER)
        addLineFeedsToBuffer(3)
    }
}