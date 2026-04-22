import Foundation
import UIKit
import woosim302

enum WoosimHelper {
    static func addAlignedString(
        _ str: String = "",
        align: WSCmd.AlignType = .Left,
        bold: Bool = false,
        doubleHeight: Bool = false
    ) -> Data {
        var out = Data()

        out.append(WSCmd.extendFont(width: 1, height: 1))

        if bold { out.append(WSCmd.setBold(true)) }
        if doubleHeight { out.append(WSCmd.extendFont(width: 1, height: 2)) }
        out.append(WSCmd.alignText(align))

        if let bytes = str.data(using: .utf8) {
            out.append(bytes)
        }

        if bold { out.append(WSCmd.setBold(false)) }
        if doubleHeight { out.append(WSCmd.extendFont(width: 1, height: 1)) }
        out.append(WSCmd.alignText(.Left))

        return out
    }

    static func addTwoMarginedStrings(
        _ str1: String = "",
        _ str2: String = "",
        bold: Bool = false,
        doubleHeight: Bool = false,
        len: Int
    ) -> Data {
        var out = Data()

        if bold { out.append(WSCmd.setBold(true)) }
        if doubleHeight { out.append(WSCmd.extendFont(width: 1, height: 2)) }

        let l1 = str1.count
        let l2 = str2.count

        if l1 + l2 >= len {
            if l1 + l2 == len + 1 {
                out.append(WSCmd.alignText(.Left))
                out.append((str1.isEmpty ? " " : str1).data(using: .utf8) ?? Data())
                out.append(WSCmd.alignText(.Right))
                out.append((str2.isEmpty ? " " : str2).data(using: .utf8) ?? Data())
                out.append(WSCmd.alignText(.Left))
            } else {
                let wrapped = CommonHelper.createWrappedString("\(str1) \(str2)", len: len)
                out.append(WSCmd.alignText(.Left))
                out.append(wrapped.data(using: .utf8) ?? Data())
                out.append(WSCmd.printFeed(line: 0))
            }
        } else {
            out.append(WSCmd.alignText(.Left))
            out.append((str1.isEmpty ? " " : str1).data(using: .utf8) ?? Data())
            out.append(WSCmd.alignText(.Right))
            out.append((str2.isEmpty ? " " : str2).data(using: .utf8) ?? Data())
            out.append(WSCmd.alignText(.Left))
        }

        if bold { out.append(WSCmd.setBold(false)) }
        if doubleHeight { out.append(WSCmd.extendFont(width: 1, height: 1)) }

        return out
    }

    static func addThreeMarginedStrings(
        _ leftString: String,
        _ middleString: String,
        _ rightString: String,
        pageLength: Int,
        leftBold: Bool = false,
        middleBold: Bool = false,
        rightBold: Bool = false,
        allBold: Bool = false
    ) -> Data {
        let left = leftString.trimmingCharacters(in: .whitespaces)
        let middle = middleString.trimmingCharacters(in: .whitespaces)
        let right = rightString.trimmingCharacters(in: .whitespaces)

        var out = Data()
        let total = left.count + middle.count + right.count

        if total > pageLength {
            let wrapped = CommonHelper.createWrappedString(
                "\(left) \(middle) \(right)", len: pageLength
            )
            out.append(WSCmd.alignText(.Left))
            out.append(wrapped.data(using: .utf8) ?? Data())
            out.append(WSCmd.printFeed(line: 0))
            return out
        }

        let middleStart = max(pageLength / 2 - middle.count / 2, 0)
        let sp1 = max(middleStart - left.count, 1)
        let middleEnd = middleStart + middle.count
        let rightStart = pageLength - right.count
        let sp2 = max(rightStart - middleEnd, 1)

        let space1 = String(repeating: " ", count: sp1)
        let space2 = String(repeating: " ", count: sp2)

        out.append(WSCmd.alignText(.Left))

        if allBold || leftBold { out.append(WSCmd.setBold(true)) }
        out.append((left.isEmpty ? " " : left).data(using: .utf8) ?? Data())
        if allBold || leftBold { out.append(WSCmd.setBold(false)) }

        out.append(space1.data(using: .utf8) ?? Data())

        if allBold || middleBold { out.append(WSCmd.setBold(true)) }
        out.append((middle.isEmpty ? " " : middle).data(using: .utf8) ?? Data())
        if allBold || middleBold { out.append(WSCmd.setBold(false)) }

        out.append(space2.data(using: .utf8) ?? Data())

        if allBold || rightBold { out.append(WSCmd.setBold(true)) }
        let rightWithNL = right.isEmpty ? " " : "\(right)\n"
        out.append(rightWithNL.data(using: .utf8) ?? Data())
        if allBold || rightBold { out.append(WSCmd.setBold(false)) }

        out.append(WSCmd.alignText(.Left))
        return out
    }

    static func addImage(fileName: String, align: WSCmd.AlignType = .Center) -> Data {
        var out = Data()

        guard let cachesDir = NSSearchPathForDirectoriesInDomains(
            .cachesDirectory, .userDomainMask, true
        ).first else {
            NSLog("[WoosimHelper] --> caches dir unavailable")
            return out
        }

        let imagePath = (cachesDir as NSString).appendingPathComponent(fileName)
        guard let image = UIImage(contentsOfFile: imagePath) else {
            NSLog("[WoosimHelper] --> failed to load image at \(imagePath)")
            return out
        }
        
        out.append("\n".data(using: .utf8) ?? Data())
        out.append(WSCmd.alignContent(align))
        out.append(WSImage.printImage(image, dithering: true, compress: true))
        out.append(WSCmd.alignContent(.Left))

        return out
    }
}
