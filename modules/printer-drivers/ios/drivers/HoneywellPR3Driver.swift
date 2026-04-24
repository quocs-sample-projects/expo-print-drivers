import Foundation
import woosim302

final class HoneywellPR3Driver: BaseDriver {
    override var driverName: String { "HoneywellPR3Driver" }
    override var printerPageWidth: Int { 48 }
    override var separateLineLength: Int { printerPageWidth }

    override func initPrinter() {
        // TODO: implement with Honeywell SDK
    }

    override func addAlignedStringToBuffer(
        _ string: String,
        align: WSCmd.AlignType,
        bold: Bool,
        doubleFontSize: Bool
    ) {
        // TODO: implement with Honeywell SDK
        NSLog("--> addAlignedStringToBuffer")
    }

    override func addBitmapToBuffer(fileName: String, align: WSCmd.AlignType) {
        // TODO: implement with Honeywell SDK
        NSLog("--> addBitmapToBuffer")
    }

    override func addLineFeedsToBuffer(_ lineNumber: Int) {
        // TODO: implement with Honeywell SDK
        NSLog("--> addLineFeedsToBuffer")
    }

    override func addThreeAlignedStringsToBuffer(
        leftString: String,
        middleString: String,
        rightString: String,
        leftBold: Bool,
        middleBold: Bool,
        rightBold: Bool,
        leftDoubleHeight: Bool,
        middleDoubleHeight: Bool,
        rightDoubleHeight: Bool
    ) {
        // TODO: implement with Honeywell SDK
        NSLog("--> addThreeAlignedStringsToBuffer")
    }

    override func sendPrintData() {
        // TODO: implement with Honeywell SDK
        NSLog("--> addThreeAlignedStringsToBuffer")
    }
}
