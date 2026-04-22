import Foundation
import woosim302

final class WoosimWSPi350: BaseDriver {
    override var driverName: String { "WoosimWSPi350Driver" }
    override var printerPageWidth: Int { 35 }
    override var separateLineLength: Int { printerPageWidth }

    override func initPrinter() {
        buffer.append(WSCmd.initPrinter())
    }

    override func addAlignedStringToBuffer(
        _ string: String,
        align: WSCmd.AlignType,
        bold: Bool,
        doubleFontSize: Bool
    ) {
        var actualString = string
        if align == .Right {
            if actualString.hasSuffix("\n") {
                actualString = String(actualString.dropLast()) + " \n"
            } else {
                actualString += " "
            }
        }

        let wrapped = CommonHelper.createWrappedString(actualString, len: printerPageWidth)

        buffer.append(
            WoosimHelper.addAlignedString(
                wrapped, align: align, bold: bold, doubleHeight: doubleFontSize
            )
        )
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
        buffer.append(
            WoosimHelper.addThreeMarginedStrings(
                leftString,
                middleString,
                rightString,
                pageLength: printerPageWidth,
                leftBold: leftBold,
                middleBold: middleBold,
                rightBold: rightBold,
                allBold: leftBold && middleBold && rightBold
            )
        )
    }

    override func addLineFeedsToBuffer(_ lineNumber: Int) {
        buffer.append(WSCmd.printFeed(line: UInt8(max(0, min(lineNumber, 255)))))
    }

    override func addBitmapToBuffer(fileName: String, align: WSCmd.AlignType) {
        buffer.append(WoosimHelper.addImage(fileName: fileName, align: align))
    }
}
