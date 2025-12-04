package expo.modules.printerdrivers.drivers

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import expo.modules.printerdrivers.utils.helpers.PrinterHelper
import expo.modules.printerdrivers.utils.helpers.DateHelper
import expo.modules.printerdrivers.utils.helpers.WoosimHelper
import expo.modules.printerdrivers.utils.constants.PrinterCharacter.M3
import expo.modules.printerdrivers.utils.constants.PrinterCharacter.VND
import expo.modules.printerdrivers.utils.constants.MemoryUnit.KiB
import java.nio.ByteBuffer
import java.util.Date

class WoosimWSPi350Driver(
    private val bluetoothService: BluetoothService,
    private val context: Context
) :
    BaseDriver(bluetoothService) {
    override var driverName: String = "WoosimWSPi350Driver"
    override var buffer: ByteBuffer = ByteBuffer.allocate(50 * KiB)

    private fun initPrinter() = buffer.put(WoosimCmd.initPrinter())

    private fun sendPrintData() = bluetoothService.write(buffer.array())

    private fun clearBuffer() = buffer.clear()

    private fun addAlignStringToBuffer(
        str: String,
        align: Int = WoosimCmd.ALIGN_LEFT,
        bold: Boolean = false,
        doubleHeight: Boolean = false
    ) {
        buffer.put(
            WoosimHelper.addAlignedString(
                PrinterHelper.wordWrapStr(str),
                align,
                bold,
                doubleHeight
            )
        )
    }

    private fun addTwoMarginedStringsToBuffer(
        str1: String,
        str2: String,
        bold: Boolean = false,
        doubleHeight: Boolean = false
    ) {
        buffer.put(WoosimHelper.addTwoMarginedStrings(str1, str2, bold, doubleHeight))
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
            )
        )
    }

    private fun addSeparateLineToBuffer() {
        buffer.put(PrinterHelper.addSeparateLine())
    }

    private fun addLineFeedToBuffer(lineNumber: Int = 1) {
        buffer.put(WoosimHelper.addLineFeed(lineNumber))
    }

    private fun addBitmapToBuffer(fileName: String) {
        buffer.put(WoosimHelper.addImage(context, fileName))
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        val currentDate = DateHelper.formatDate(Date())
        val kyNam = "04/2024"
        val tuNgayDocSo = "15/03/2024"
        val denNgayDocSo = "14/04/2024"
        val soDanhBo = "20112330001"
        val khachHang = "CONG TY CO PHAN CONG TRINH DO THI CAN GIUOC"
        val diaChi = "SỐ 4 (RANH TPHCM LONG AN) QUOC LO 50 (QDU) QUOC LO 50, XÃ QUY ĐỨC"
        val dienThoai = "02723875052"
        val giaBieu = "55"
        val dinhMuc = "0"
        val kyHieuDocSo = "4"
        val chiSoNuocKyNay = "153384"
        val chiSoNuocKyTruoc = "101091"
        val tieuThuNuocKyNay = "51293"
        val tienNuoc = "375.157.002"
        val thueGTGT = "18.757.850"
        val tienDVTN = "0"
        val thueDVTN = "0"
        val tongCong = "393.914.852"
        val thoiHanThanhToan = "21/03/2024"

        val hoaDonSize = "4"
        val kyNo = "12/2023+1+2+3/2024"
        val tongTienNo = "2.371.680"
        val tongTienCanThanhToan = "2.371.680"

        val hoTenNguoiDung = "Dương Văn Dẫn"
        val diDongNguoiDung = "0918145295"
        val maQRFileName = "ma_qr.png"


        clearBuffer()
        initPrinter()

        addSeparateLineToBuffer()
        addAlignStringToBuffer(
            "XÍ NGHIỆP CẤP NƯỚC SINH HOẠT\n",
            WoosimCmd.ALIGN_CENTER,
            bold = true
        )
        addAlignStringToBuffer(
            "NÔNG THÔN THÀNH PHỐ HỒ CHÍ MINH\n",
            WoosimCmd.ALIGN_CENTER,
            bold = true
        )
        addSeparateLineToBuffer()
        addAlignStringToBuffer("27 Hữu Nghị, P.Thủ Đức, TP.HCM\n", WoosimCmd.ALIGN_CENTER)
        addSeparateLineToBuffer()
        addAlignStringToBuffer("GIẤY BÁO TIỀN NƯỚC\n", WoosimCmd.ALIGN_CENTER, bold = true)
        addAlignStringToBuffer("(Ngày gửi thông báo: $currentDate)\n", WoosimCmd.ALIGN_CENTER)
        addAlignStringToBuffer(
            "THÁNG: ${kyNam}\n",
            WoosimCmd.ALIGN_CENTER,
            bold = true,
            doubleHeight = true
        )
        addAlignStringToBuffer(
            "(Từ ngày $tuNgayDocSo đến ngày $denNgayDocSo)\n",
            WoosimCmd.ALIGN_CENTER
        )
        addSeparateLineToBuffer()
        addAlignStringToBuffer("SỐ DANH BỘ: $soDanhBo\n", bold = true, doubleHeight = true)
        addAlignStringToBuffer("KH: $khachHang\n")
        addAlignStringToBuffer("ĐC: $diaChi\n")
        addAlignStringToBuffer("SĐT KH: $dienThoai\n")
        addThreeMarginedStringsToBuffer(
            "GB: $giaBieu",
            "ĐM: $dinhMuc",
            "MTT: $kyHieuDocSo"
        )
        addTwoMarginedStringsToBuffer(
            "Chỉ số mới:$chiSoNuocKyNay",
            "Chỉ số cũ:$chiSoNuocKyTruoc\n"
        )
        addThreeMarginedStringsToBuffer("Tiêu thụ:", tieuThuNuocKyNay, "$M3\n", allBold = true)
        addThreeMarginedStringsToBuffer("Tiền nước:", tienNuoc, "$VND\n")
        addThreeMarginedStringsToBuffer("Thuế VAT:", thueGTGT, "$VND\n")
        addThreeMarginedStringsToBuffer("Phí DVTN:", tienDVTN, "$VND\n")
        addThreeMarginedStringsToBuffer("Thuế DVTN:", thueDVTN, "$VND\n")
        addThreeMarginedStringsToBuffer("TỔNG CỘNG:", tongCong, "$VND\n", middleBold = true)
        addSeparateLineToBuffer()
        addAlignStringToBuffer("* Hóa đơn này sẽ được phát hành sau 1-3 ngày.\n")
        addAlignStringToBuffer("* Quý khách  vui lòng thanh toán  trước ngày $thoiHanThanhToan và có thể thanh toán bằng mã QR in trên giấy báo\n")
        addSeparateLineToBuffer()
        addAlignStringToBuffer(
            "* Thông tin hoá đơn còn nợ (chưa thanh toán):\n",
            WoosimCmd.ALIGN_CENTER,
            true
        )
        addTwoMarginedStringsToBuffer("Số hoá đơn nợ:", "$hoaDonSize\n")
        addTwoMarginedStringsToBuffer("Kỳ nợ:", "$kyNo\n")
        addTwoMarginedStringsToBuffer("Tổng số tiền nợ:", "$tongTienNo\n")
        addTwoMarginedStringsToBuffer(
            "Tổng số tiền cần thanh toán:",
            "$tongTienCanThanhToan\n",
            bold = true
        )
        addAlignStringToBuffer("* Để")
        addAlignStringToBuffer(" Không bị ngừng dịch vụ cấp nước", bold = true)
        addAlignStringToBuffer("đề nghị Quý khách")
        addAlignStringToBuffer(" thanh toán ngay tiền nợ đã quá hạn", bold = true)
        addAlignStringToBuffer(" của các tháng tiền nước trên.\n")
        addAlignStringToBuffer("* Quý khách vui lòng cài đặt và sử dụng ứng dụng")
        addAlignStringToBuffer(" Sawaco CSKH", bold = true)
        addAlignStringToBuffer(" (mặt sau thông báo)\n")
        addSeparateLineToBuffer()
        addAlignStringToBuffer("Nhân viên: $hoTenNguoiDung\n", bold = true)
        addAlignStringToBuffer("Số ĐT-Zalo: $diDongNguoiDung\n", bold = true)
        addSeparateLineToBuffer()
        addAlignStringToBuffer("Quét mã để thanh toán\n", WoosimCmd.ALIGN_CENTER)
        addBitmapToBuffer(maQRFileName)


        addLineFeedToBuffer(3)
        sendPrintData()
    }
}