import Foundation
import UIKit
import woosim302
import HoneywellPrinterSDK

/// Driver for Honeywell PR3 using the Honeywell Printer SDK (Fingerprint).
///
/// The PR3 must be set to **Fingerprint** as its default command language
/// (SYSVAR(48)=0 or via the on-device menu / PrintSet Mobile). The SDK emits
/// a Fingerprint program; if the printer is in Line Printer/ESC-P mode the
/// payload is printed as literal text.
///
/// Coordinate system: positions are in **dots**. At 203 dpi the PR3 3" head
/// has 576 printable dots per row. We advance a `rowCursor` manually so the
/// `BaseDriver.giayBaoTienNuocBenThanh` flow can work in terms of "lines".
final class HoneywellPR3Driver: BaseDriver {
    override var driverName: String { "HoneywellPR3Driver" }
    // Logical character width shown on one line with the default font.
    override var printerPageWidth: Int { 48 }
    override var separateLineLength: Int { 71 }

    // MARK: - Page geometry

    /// Total printable dots per row on the PR3 print head (3" @ 203 dpi).
    private let pageWidthDots: Int32 = 576

    /// Vertical space (dots) a single normal-size line occupies. The default
    /// Fingerprint font at size 12 is ~24 dots tall; we leave a bit of air.
    private let normalRowHeight: Int32 = 32
    private let doubleRowHeight: Int32 = 56

    /// Default font sizes (Fingerprint "size" parameter — in points).
    private let normalFontSize: Int32 = 12
    private let doubleFontSize: Int32 = 18

    // MARK: - State

    private var document: DocumentFP = DocumentFP()
    private var rowCursor: Int32 = 0

    // MARK: - Overrides

    override func initPrinter() {
        document = DocumentFP()
        rowCursor = 0
    }

    override func addAlignedStringToBuffer(
        _ string: String,
        align: WSCmd.AlignType,
        bold: Bool,
        doubleFontSize doubleSize: Bool
    ) {
        writeNativeText(
            string,
            align: align,
            bold: bold,
            double: doubleSize
        )
    }

    override func addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Bool,
        rightBold: Bool,
        leftDoubleHeight: Bool,
        rightDoubleHeight: Bool
    ) {
        // Place both strings on the same row: one Left-aligned, one Right-aligned.
        let rowBefore = rowCursor
        let leftRowHeight = writeNativeText(
            leftString,
            align: .Left,
            bold: leftBold,
            double: leftDoubleHeight,
            advanceRow: false
        )
        // Keep the right-side piece on the same baseline.
        rowCursor = rowBefore
        let rightRowHeight = writeNativeText(
            rightString,
            align: .Right,
            bold: rightBold,
            double: rightDoubleHeight,
            advanceRow: false
        )
        // Advance by the taller of the two.
        rowCursor = rowBefore + max(leftRowHeight, rightRowHeight)
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
        let rowBefore = rowCursor
        let lH = writeNativeText(leftString,   align: .Left,   bold: leftBold,   double: leftDoubleHeight,   advanceRow: false)
        rowCursor = rowBefore
        let mH = writeNativeText(middleString, align: .Center, bold: middleBold, double: middleDoubleHeight, advanceRow: false)
        rowCursor = rowBefore
        let rH = writeNativeText(rightString,  align: .Right,  bold: rightBold,  double: rightDoubleHeight,  advanceRow: false)
        rowCursor = rowBefore + max(lH, max(mH, rH))
    }

    override func addBitmapToBuffer(fileName: String, align: WSCmd.AlignType) {
        guard let cachesDir = NSSearchPathForDirectoriesInDomains(
            .cachesDirectory, .userDomainMask, true
        ).first else {
            NSLog("[HoneywellPR3Driver] --> caches dir unavailable")
            return
        }

        let imagePath = (cachesDir as NSString).appendingPathComponent(fileName)
        guard let image = UIImage(contentsOfFile: imagePath) else {
            NSLog("[HoneywellPR3Driver] --> failed to load image at \(imagePath)")
            return
        }

        // Fit width to half the printable area; keep aspect ratio.
        let targetWidth: Int32 = pageWidthDots / 2
        let srcWidth = max(CGFloat(1), image.size.width * image.scale)
        let srcHeight = max(CGFloat(1), image.size.height * image.scale)
        let scale = CGFloat(targetWidth) / srcWidth
        let targetHeight = Int32((srcHeight * scale).rounded())

        let column: Int32
        let alignment: AlignmentFP
        switch align {
        case .Center:
            column = pageWidthDots / 2
            alignment = .topCenter
        case .Right:
            column = pageWidthDots
            alignment = .topRight
        default:
            column = 0
            alignment = .topLeft
        }

        let params = ParametersFP()
        params.alignment = alignment

        document.writeImage(
            fromObject: image,
            atRow: rowCursor,
            atColumn: column,
            atWidth: targetWidth,
            atHeight: targetHeight,
            paramObject: params
        )

        rowCursor += targetHeight + 8 // small gap after image
    }

    override func addLineFeedsToBuffer(_ lineNumber: Int) {
        let clamped = max(0, min(lineNumber, 255))
        rowCursor += Int32(clamped) * normalRowHeight
    }

    override func sendPrintData() {
        document.printDocument()
        let data = document.getData()!
        bluetoothService.write(data)

        // Reset for the next job.
        document = DocumentFP()
        rowCursor = 0
    }

    // MARK: - Native text helper

    /// Writes `text` at the current `rowCursor` and advances the cursor.
    /// Returns the row height consumed.
    @discardableResult
    private func writeNativeText(
        _ text: String,
        align: WSCmd.AlignType,
        bold: Bool,
        double: Bool,
        advanceRow: Bool = true
    ) -> Int32 {
        // Drop trailing newlines — we manage line advance ourselves.
        let cleaned = text.trimmingCharacters(in: .newlines)

        // Skip empty writes but still consume the intended row height so
        // callers can use empty strings as spacers.
        let rowHeight = double ? doubleRowHeight : normalRowHeight

        if cleaned.isEmpty {
            if advanceRow { rowCursor += rowHeight }
            return rowHeight
        }

        let column: Int32
        let alignment: AlignmentFP
        switch align {
        case .Center:
            column = pageWidthDots / 2
            alignment = .topCenter
        case .Right:
            column = pageWidthDots
            alignment = .topRight
        default:
            column = 0
            alignment = .topLeft
        }

        let params = ParametersFP()
        params.alignment = alignment
        params.isFontBold = bold
        params.fontSize = double ? doubleFontSize : normalFontSize
        params.characterSet = "UTF8"

        document.writeText(
            cleaned,
            atRow: rowCursor,
            atColumn: column,
            paramObject: params
        )

        if advanceRow { rowCursor += rowHeight }
        return rowHeight
    }
}
