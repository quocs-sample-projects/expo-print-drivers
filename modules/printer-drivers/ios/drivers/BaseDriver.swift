import Foundation
import woosim302

class BaseDriver {
    let bluetoothService: BluetoothService

    var buffer = NSMutableData(capacity: 50 * ByteConstants.KB) ?? NSMutableData()

    var driverName: String { "BaseDriver" }
    var printerPageWidth: Int { 0 }
    var separateLineLength: Int { printerPageWidth }

    init(bluetoothService: BluetoothService) {
        self.bluetoothService = bluetoothService
    }

    func initPrinter() {
        fatalError("initPrinter() must be overridden by \(type(of: self))")
    }

    func addAlignedStringToBuffer(
        _ string: String,
        align: WSCmd.AlignType = .Left,
        bold: Bool = false,
        doubleFontSize: Bool = false
    ) {
        fatalError("addAlignedStringToBuffer(...) must be overridden")
    }

    func addBitmapToBuffer(fileName: String, align: WSCmd.AlignType = .Center) {
        fatalError("addBitmapToBuffer(...) must be overridden")
    }

    func addLineFeedsToBuffer(_ lineNumber: Int = 1) {
        fatalError("addLineFeedsToBuffer(_:) must be overridden")
    }

    func addThreeAlignedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Bool = false,
        middleBold: Bool = false,
        rightBold: Bool = false,
        leftDoubleHeight: Bool = false,
        middleDoubleHeight: Bool = false,
        rightDoubleHeight: Bool = false
    ) {
        fatalError("addThreeAlignedStringsToBuffer(...) must be overridden")
    }

    func addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Bool = false,
        rightBold: Bool = false,
        leftDoubleHeight: Bool = false,
        rightDoubleHeight: Bool = false
    ) {
        addAlignedStringToBuffer(leftString, align: .Left, bold: leftBold, doubleFontSize: leftDoubleHeight)
        addAlignedStringToBuffer(rightString, align: .Right, bold: rightBold, doubleFontSize: rightDoubleHeight)
    }

    func addSeparateLineToBuffer(
        lineLength: Int? = nil,
        align: WSCmd.AlignType = .Left
    ) {
        let length = lineLength ?? separateLineLength
        let line = String(repeating: "-", count: length) + "\n"
        addAlignedStringToBuffer(line, align: align, bold: true)
    }

    func clearBuffer() {
        buffer.length = 0
    }

    func sendPrintData() {
        bluetoothService.write(buffer as Data)
    }

    func giayBaoTienNuocBenThanh(jsonData: [String: Any]) {
        let tenCongTy = CommonHelper.getStringValueByKey(jsonData, "tenCongTy")
        let tenPhieu = CommonHelper.getStringValueByKey(jsonData, "tenPhieu")
        let ky = CommonHelper.getStringValueByKey(jsonData, "ky")
        let tuNgay = CommonHelper.getStringValueByKey(jsonData, "tuNgay")
        let denNgay = CommonHelper.getStringValueByKey(jsonData, "denNgay")
        let mdb = CommonHelper.getStringValueByKey(jsonData, "mdb")
        let mlt = CommonHelper.getStringValueByKey(jsonData, "mlt")
        let khachHang = CommonHelper.getStringValueByKey(jsonData, "khachHang")
        let soDienThoai = CommonHelper.getStringValueByKey(jsonData, "soDienThoai")
        let diaChi = CommonHelper.getStringValueByKey(jsonData, "diaChi")
        let giaBieu = CommonHelper.getStringValueByKey(jsonData, "giaBieu")
        let dinhMuc = CommonHelper.getStringValueByKey(jsonData, "dinhMuc")
        let chiSoMoi = CommonHelper.getStringValueByKey(jsonData, "chiSoMoi")
        let chiSoCu = CommonHelper.getStringValueByKey(jsonData, "chiSoCu")
        let tieuThu = CommonHelper.getStringValueByKey(jsonData, "tieuThu")
        let tienNuoc = CommonHelper.getStringValueByKey(jsonData, "tienNuoc")
        let thueVat = CommonHelper.getStringValueByKey(jsonData, "thueVat")
        let dvtn = CommonHelper.getStringValueByKey(jsonData, "dvtn")
        let vatDvtn = CommonHelper.getStringValueByKey(jsonData, "vatDvtn")
        let tienKyMoi = CommonHelper.getStringValueByKey(jsonData, "tienKyMoi")
        let nhanVien = CommonHelper.getStringValueByKey(jsonData, "nhanVien")
        let dienThoaiNhanVien = CommonHelper.getStringValueByKey(jsonData, "dienThoaiNhanVien")
        let maQR = CommonHelper.getStringValueByKey(jsonData, "maQR")

        addAlignedStringToBuffer(driverName)
        addLineFeedsToBuffer()

        addAlignedStringToBuffer("\(tenCongTy)\n", align: .Center, bold: true)
        addSeparateLineToBuffer()
        addAlignedStringToBuffer(
            "\(tenPhieu)\n", align: .Center, bold: true, doubleFontSize: true
        )
        addAlignedStringToBuffer("KỲ \(ky)\n", align: .Center, bold: true)
        addAlignedStringToBuffer("(\(tuNgay) - \(denNgay))\n", align: .Center)
        addAlignedStringToBuffer("DB: \(mdb) - MLT: \(mlt)\n")
        addAlignedStringToBuffer("KH: \(khachHang)\n")
        addAlignedStringToBuffer("Điện thoại KH: \(soDienThoai)\n")
        addAlignedStringToBuffer("ĐC: \(diaChi)\n")
        addAlignedStringToBuffer("Giá biểu: \(giaBieu) - Định mức: \(dinhMuc)\n")
        addTwoAlignedStringsToBuffer(
            leftString: "Chỉ số mới:",
            rightString: "\(chiSoMoi) \(PrinterCharacter.M3)\n"
        )
        addTwoAlignedStringsToBuffer(
            leftString: "Chỉ số cũ:",
            rightString: "\(chiSoCu) \(PrinterCharacter.M3)\n"
        )
        addTwoAlignedStringsToBuffer(
            leftString: "Tiêu thụ:",
            rightString: "\(tieuThu) \(PrinterCharacter.M3)\n"
        )
        addTwoAlignedStringsToBuffer(
            leftString: "Tiền nước:",
            rightString: "\(tienNuoc) \(PrinterCharacter.VND)\n"
        )
        addTwoAlignedStringsToBuffer(
            leftString: "Thuế VAT (5%):",
            rightString: "\(thueVat) \(PrinterCharacter.VND)\n"
        )
        addTwoAlignedStringsToBuffer(
            leftString: "DVTN (30%):",
            rightString: "\(dvtn) \(PrinterCharacter.VND)\n"
        )
        addTwoAlignedStringsToBuffer(
            leftString: "VAT DVTN (8%):",
            rightString: "\(vatDvtn) \(PrinterCharacter.VND)\n"
        )
        addSeparateLineToBuffer(lineLength: 15, align: .Right)
        addTwoAlignedStringsToBuffer(
            leftString: "Số tiền (Kỳ mới):",
            rightString: "\(tienKyMoi) \(PrinterCharacter.VND)\n"
        )
        addSeparateLineToBuffer()
        addAlignedStringToBuffer("-NV: \(nhanVien)\n")
        addAlignedStringToBuffer("-ĐT: \(dienThoaiNhanVien)\n")
        addAlignedStringToBuffer(
            "Sau 3 ngày làm việc, kể từ ngày ghi chỉ số nước, dữ liệu hoá đơn sẽ được cập nhật tại website:\n"
        )
        addAlignedStringToBuffer("www.capnuocthuduc.com\n")
        addAlignedStringToBuffer(
            "Quý khách vui lòng kiểm tra lại số điện thoại trên phiếu báo này liên hệ đội QLGTN:\n"
        )
        addAlignedStringToBuffer("(028) 38.001.002 để cập nhật lại nếu chưa chính xác.\n")
        addAlignedStringToBuffer("Quét mã QR để thanh toán\n", align: .Center, bold: true)
        addBitmapToBuffer(fileName: maQR)

        addLineFeedsToBuffer(3)
    }
}
