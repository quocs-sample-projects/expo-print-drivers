package expo.modules.printerdrivers.drivers

import android.content.Context
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

    abstract fun initPrinter()

    abstract fun addAlignedStringToBuffer(
        string: String,
        align: Int = WoosimCmd.ALIGN_LEFT,
        bold: Boolean = false,
        doubleFontSize: Boolean = false
    )

    abstract fun addBitmapToBuffer(fileName: String, align: Int = WoosimCmd.ALIGN_CENTER)

    abstract fun addLineFeedsToBuffer(lineNumber: Int = 1)

    open fun addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Boolean = false,
        rightBold: Boolean = false,
        leftDoubleHeight: Boolean = false,
        rightDoubleHeight: Boolean = false,
    ) {
        addAlignedStringToBuffer(leftString, WoosimCmd.ALIGN_LEFT, leftBold, leftDoubleHeight)
        addAlignedStringToBuffer(rightString, WoosimCmd.ALIGN_RIGHT, rightBold, rightDoubleHeight)
    }

    abstract fun addThreeAlignedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Boolean = false,
        middleBold: Boolean = false,
        rightBold: Boolean = false,
        leftDoubleHeight: Boolean = false,
        middleDoubleHeight: Boolean = false,
        rightDoubleHeight: Boolean = false,
    )

    fun addSeparateLineToBuffer(
        lineLength: Int = separateLineLength, align: Int = WoosimCmd.ALIGN_LEFT
    ) {
        addAlignedStringToBuffer("${"-".repeat(lineLength)}\n", align, bold = true)
    }

    open fun clearBuffer() {
        buffer.clear()
    }

    open fun sendPrintData() {
        // Only send the data that was actually written to the buffer
        val data = ByteArray(buffer.position())
        buffer.rewind()
        buffer.get(data)
        bluetoothService.write(data)
    }

    fun giayBaoTienNuocBenThanh(jsonData: ReadableMap) {
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
        val chiSoMoi = getStringValueByKey(jsonData, "chiSoMoi")
        val chiSoCu = getStringValueByKey(jsonData, "chiSoCu")
        val tieuThu = getStringValueByKey(jsonData, "tieuThu")
        val tienNuoc = getStringValueByKey(jsonData, "tienNuoc")
        val thueVat = getStringValueByKey(jsonData, "thueVat")
        val dvtn = getStringValueByKey(jsonData, "dvtn")
        val vatDvtn = getStringValueByKey(jsonData, "vatDvtn")
        val tienKyMoi = getStringValueByKey(jsonData, "tienKyMoi")
        val nhanVien = getStringValueByKey(jsonData, "nhanVien")
        val dienThoaiNhanVien = getStringValueByKey(jsonData, "dienThoaiNhanVien")
        val maQR = getStringValueByKey(jsonData, "maQR")

        addAlignedStringToBuffer(driverName)
        addLineFeedsToBuffer()

        addAlignedStringToBuffer("$tenCongTy\n", WoosimCmd.ALIGN_CENTER, bold = true)
        addSeparateLineToBuffer()
        addAlignedStringToBuffer(
            "$tenPhieu\n",
            WoosimCmd.ALIGN_CENTER,
            bold = true,
            doubleFontSize = true
        )
        addAlignedStringToBuffer("KỲ $ky\n", WoosimCmd.ALIGN_CENTER, bold = true)
        addAlignedStringToBuffer("($tuNgay - $denNgay)\n", WoosimCmd.ALIGN_CENTER)
        addAlignedStringToBuffer("DB: $mdb - MLT: $mlt\n")
        addAlignedStringToBuffer("KH: $khachHang\n")
        addAlignedStringToBuffer("Điện thoại KH: $soDienThoai\n")
        addAlignedStringToBuffer("ĐC: $diaChi\n")
        addAlignedStringToBuffer("Giá biểu: $giaBieu - Định mức: $dinhMuc\n")
        addTwoAlignedStringsToBuffer("Chỉ số mới:", "$chiSoMoi ${PrinterCharacter.M3}\n")
        addTwoAlignedStringsToBuffer("Chỉ số cũ:", "$chiSoCu ${PrinterCharacter.M3}\n")
        addTwoAlignedStringsToBuffer("Tiêu thụ:", "$tieuThu ${PrinterCharacter.M3}\n")
        addTwoAlignedStringsToBuffer("Tiền nước:", "$tienNuoc ${PrinterCharacter.VND}\n")
        addTwoAlignedStringsToBuffer("Thuế VAT (5%):", "$thueVat ${PrinterCharacter.VND}\n")
        addTwoAlignedStringsToBuffer("DVTN (30%):", "$dvtn ${PrinterCharacter.VND}\n")
        addTwoAlignedStringsToBuffer("VAT DVTN (8%):", "$vatDvtn ${PrinterCharacter.VND}\n")
        addSeparateLineToBuffer(15, WoosimCmd.ALIGN_RIGHT)
        addTwoAlignedStringsToBuffer("Số tiền (Kỳ mới):", "$tienKyMoi ${PrinterCharacter.VND}\n")
        addSeparateLineToBuffer()
        addAlignedStringToBuffer("-NV: $nhanVien\n")
        addAlignedStringToBuffer("-ĐT: $dienThoaiNhanVien\n")
        addAlignedStringToBuffer("Sau 3 ngày làm việc, kể từ ngày ghi chỉ số nước, dữ liệu hoá đơn sẽ được cập nhật tại website:\n")
        addAlignedStringToBuffer("www.capnuocthuduc.com\n")
        addAlignedStringToBuffer("Quý khách vui lòng kiểm tra lại số điện thoại trên phiếu báo này liên hệ đội QLGTN:\n")
        addAlignedStringToBuffer("(028) 38.001.002 để cập nhật lại nếu chưa chính xác.\n")
        addAlignedStringToBuffer(
            "Quét mã QR để thanh toán\n", WoosimCmd.ALIGN_CENTER, bold = true
        )
        addBitmapToBuffer(maQR)

        addLineFeedsToBuffer(3)
    }
}