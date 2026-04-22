import Foundation

enum CommonHelper {
    static func createWrappedString(_ source: String?, len: Int) -> String {
        guard let source = source, len > 0 else { return source ?? "" }
        let chars = Array(source)
        guard chars.count > len else { return source }

        func lastIndex(of char: Character, in chars: [Character], from start: Int, before end: Int) -> Int {
            var i = min(end, chars.count) - 1
            while i >= start {
                if chars[i] == char { return i }
                i -= 1
            }
            return -1
        }

        var lines: [String] = []
        var cursor = 0
        while chars.count - cursor > len {
            let hardEnd = cursor + len
            let breakAt = lastIndex(of: " ", in: chars, from: cursor, before: hardEnd + 1)
            if breakAt > cursor {
                lines.append(String(chars[cursor..<breakAt]))
                // Skip the space that caused the break so the next line does
                // not start with a leading blank.
                cursor = breakAt + 1
            } else {
                lines.append(String(chars[cursor..<hardEnd]))
                cursor = hardEnd
            }
        }
        if cursor < chars.count {
            lines.append(String(chars[cursor..<chars.count]))
        }
        return lines.joined(separator: "\n")
    }

    static func getStringValueByKey(
        _ map: [String: Any],
        _ key: String,
        replaceValue: String = ""
    ) -> String {
        guard let raw = map[key] else { return replaceValue }
        if let s = raw as? String {
            let trimmed = s.trimmingCharacters(in: .whitespaces)
            return trimmed.isEmpty ? replaceValue : s
        }
        if let n = raw as? NSNumber {
            let d = n.doubleValue
            if d == Double(Int(d)) {
                return String(Int(d))
            }
            return n.stringValue
        }
        return replaceValue
    }
}
