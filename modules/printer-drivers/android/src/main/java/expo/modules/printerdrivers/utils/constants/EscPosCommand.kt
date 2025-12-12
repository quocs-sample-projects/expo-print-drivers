package expo.modules.printerdrivers.utils.constants

object EscPosCommand {
    private val ESC = 0x1B.toByte()
    private val GS = 0x1D.toByte()
    private val LF = 0x0A.toByte()

    // Basic
    val INIT = byteArrayOf(ESC, '@'.code.toByte())
    val LINE_FEED = byteArrayOf(LF)

    // Multiple feed options - try different methods
    fun FEED_N(n: Int) = byteArrayOf(ESC, 'd'.code.toByte(), n.toByte())  // ESC d n
    fun FEED_LINES(n: Int): ByteArray {
        // Send multiple LF characters
        return ByteArray(n) { LF }
    }

    fun FEED_DOTS(n: Int) =
        byteArrayOf(ESC, 'J'.code.toByte(), n.toByte())  // ESC J n - feed n dots

    // Character set selection
    fun SET_CODE_PAGE(page: Int) = byteArrayOf(ESC, 't'.code.toByte(), page.toByte())

    // Common code pages:
    val CODE_PAGE_CP437 = SET_CODE_PAGE(0)
    val CODE_PAGE_CP850 = SET_CODE_PAGE(2)
    val CODE_PAGE_CP860 = SET_CODE_PAGE(3)
    val CODE_PAGE_CP863 = SET_CODE_PAGE(4)
    val CODE_PAGE_CP865 = SET_CODE_PAGE(5)
    val CODE_PAGE_WPC1252 = SET_CODE_PAGE(16)
    val CODE_PAGE_CP866 = SET_CODE_PAGE(17)
    val CODE_PAGE_CP852 = SET_CODE_PAGE(18)
    val CODE_PAGE_CP858 = SET_CODE_PAGE(19)

    // Text styles
    val BOLD_ON = byteArrayOf(ESC, 'E'.code.toByte(), 1.toByte())
    val BOLD_OFF = byteArrayOf(ESC, 'E'.code.toByte(), 0.toByte())
    val UNDERLINE_OFF = byteArrayOf(ESC, '-'.code.toByte(), 0.toByte())
    val UNDERLINE_1 = byteArrayOf(ESC, '-'.code.toByte(), 1.toByte())
    val UNDERLINE_2 = byteArrayOf(ESC, '-'.code.toByte(), 2.toByte())

    val ITALIC_ON = byteArrayOf(ESC, '4'.code.toByte(), 1.toByte())
    val ITALIC_OFF = byteArrayOf(ESC, '4'.code.toByte(), 0.toByte())

    val INVERSE_ON = byteArrayOf(GS, 'B'.code.toByte(), 1.toByte())
    val INVERSE_OFF = byteArrayOf(GS, 'B'.code.toByte(), 0.toByte())

    // Font / size
    val FONT_A = byteArrayOf(ESC, 'M'.code.toByte(), 0.toByte())
    val FONT_B = byteArrayOf(ESC, 'M'.code.toByte(), 1.toByte())
    val FONT_C = byteArrayOf(ESC, 'M'.code.toByte(), 2.toByte())

    fun SET_SIZE(widthMultiplier: Int, heightMultiplier: Int): ByteArray {
        val n = ((widthMultiplier - 1) and 0x07) or (((heightMultiplier - 1) and 0x07) shl 4)
        return byteArrayOf(GS, '!'.code.toByte(), n.toByte())
    }

    val SIZE_NORMAL = SET_SIZE(1, 1)
    val SIZE_DOUBLE_W = SET_SIZE(2, 1)
    val SIZE_DOUBLE_H = SET_SIZE(1, 2)
    val SIZE_DOUBLE_WH = SET_SIZE(2, 2)
    val SIZE_TRIPLE_W = SET_SIZE(3, 1)
    val SIZE_TRIPLE_H = SET_SIZE(1, 3)
    val SIZE_TRIPLE_WH = SET_SIZE(3, 3)

    // Alignment
    val ALIGN_LEFT = byteArrayOf(ESC, 'a'.code.toByte(), 0.toByte())
    val ALIGN_CENTER = byteArrayOf(ESC, 'a'.code.toByte(), 1.toByte())
    val ALIGN_RIGHT = byteArrayOf(ESC, 'a'.code.toByte(), 2.toByte())

    // Line spacing
    fun SET_LINE_SPACING(n: Int) = byteArrayOf(ESC, '3'.code.toByte(), n.toByte())
    val DEFAULT_LINE_SPACING = byteArrayOf(ESC, '2'.code.toByte())

    // Cut paper - try different methods
    val CUT_FULL = byteArrayOf(GS, 'V'.code.toByte(), 0x00.toByte())
    val CUT_PARTIAL = byteArrayOf(GS, 'V'.code.toByte(), 0x01.toByte())
    fun CUT_WITH_FEED(nFeed: Int) = byteArrayOf(GS, 'V'.code.toByte(), 66.toByte(), nFeed.toByte())

    // Alternative cut commands for compatibility
    val CUT_FULL_ALT = byteArrayOf(0x1D.toByte(), 0x56.toByte(), 0x00.toByte())
    val CUT_PARTIAL_ALT = byteArrayOf(0x1D.toByte(), 0x56.toByte(), 0x01.toByte())

    // Print image
    fun IMAGE_HEADER(m: Int, xL: Int, xH: Int, yL: Int, yH: Int): ByteArray {
        return byteArrayOf(
            GS, 'v'.code.toByte(), 0x30.toByte(), m.toByte(),
            xL.toByte(), xH.toByte(), yL.toByte(), yH.toByte()
        )
    }

    // Barcode
    fun BARCODE_HEIGHT(h: Int) = byteArrayOf(GS, 'h'.code.toByte(), h.toByte())
    fun BARCODE_WIDTH(w: Int) = byteArrayOf(GS, 'w'.code.toByte(), w.toByte())
    fun BARCODE_TEXT_POSITION(position: Int) = byteArrayOf(GS, 'H'.code.toByte(), position.toByte())

    const val BARCODE_TEXT_NONE = 0
    const val BARCODE_TEXT_ABOVE = 1
    const val BARCODE_TEXT_BELOW = 2
    const val BARCODE_TEXT_BOTH = 3

    fun BARCODE_CODE128(data: ByteArray): ByteArray {
        val header = byteArrayOf(GS, 'k'.code.toByte(), 73.toByte(), data.size.toByte())
        return header + data
    }

    fun BARCODE_CODE39(data: ByteArray): ByteArray {
        val header = byteArrayOf(GS, 'k'.code.toByte(), 69.toByte(), data.size.toByte())
        return header + data
    }

    fun BARCODE_EAN13(data: ByteArray): ByteArray {
        val header = byteArrayOf(GS, 'k'.code.toByte(), 67.toByte(), data.size.toByte())
        return header + data
    }

    // QR code
    val QR_MODEL = byteArrayOf(
        GS,
        '('.code.toByte(),
        'k'.code.toByte(),
        4.toByte(),
        0.toByte(),
        49.toByte(),
        65.toByte(),
        50.toByte(),
        0.toByte()
    )

    fun QR_SIZE(size: Int) = byteArrayOf(
        GS,
        '('.code.toByte(),
        'k'.code.toByte(),
        3.toByte(),
        0.toByte(),
        49.toByte(),
        67.toByte(),
        size.toByte()
    )

    fun QR_ERROR_CORRECTION(level: Int) = byteArrayOf(
        GS,
        '('.code.toByte(),
        'k'.code.toByte(),
        3.toByte(),
        0.toByte(),
        49.toByte(),
        69.toByte(),
        (48 + level).toByte()
    )

    const val QR_LEVEL_L = 0
    const val QR_LEVEL_M = 1
    const val QR_LEVEL_Q = 2
    const val QR_LEVEL_H = 3

    fun QR_STORE_DATA(data: ByteArray): ByteArray {
        val pL = ((data.size + 3) and 0xFF).toByte()
        val pH = (((data.size + 3) shr 8) and 0xFF).toByte()
        val header = byteArrayOf(
            GS,
            '('.code.toByte(),
            'k'.code.toByte(),
            pL,
            pH,
            49.toByte(),
            80.toByte(),
            48.toByte()
        )
        return header + data
    }

    val QR_PRINT = byteArrayOf(
        GS,
        '('.code.toByte(),
        'k'.code.toByte(),
        3.toByte(),
        0.toByte(),
        49.toByte(),
        81.toByte(),
        48.toByte()
    )

    // Status
    val REAL_TIME_STATUS = byteArrayOf(16.toByte(), 4.toByte(), 1.toByte())
    val GET_PAPER_STATUS = byteArrayOf(ESC, 'v'.code.toByte())

    // Drawer kick
    fun OPEN_DRAWER(pin: Int = 0, onTime: Int = 100, offTime: Int = 100): ByteArray {
        return byteArrayOf(ESC, 'p'.code.toByte(), pin.toByte(), onTime.toByte(), offTime.toByte())
    }

    // Horizontal tab
    val TAB = byteArrayOf(0x09.toByte())
    fun SET_TAB_POSITIONS(positions: IntArray): ByteArray {
        val result = ByteArray(positions.size + 3)
        result[0] = ESC
        result[1] = 'D'.code.toByte()
        positions.forEachIndexed { index, pos ->
            result[index + 2] = pos.toByte()
        }
        result[result.size - 1] = 0x00.toByte()
        return result
    }
}
