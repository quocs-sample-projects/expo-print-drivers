package expo.modules.printerdrivers.utils.constants

object PR3Command {
    private const val ESC = 0x1b.toByte() // ESC
    private const val EM = 0x21.toByte() // ! (Exclamation Mark)
    private const val NLL = 0x00.toByte() // NULL
    private const val CR = 0x0d.toByte() // Carriage Return

    val INIT = byteArrayOf(
        NLL, NLL, NLL, NLL,
        ESC, 0x40,
        NLL, NLL, NLL
    )

    val NORMAL_FONT = byteArrayOf(
        ESC, 0x77, EM,
        ESC, EM, NLL
    )

    val BOLD_ON = byteArrayOf(ESC, 0x77, 0x6D)
    val BOLD_OFF = byteArrayOf(ESC, 0x77, EM)

    val DOUBLE_WIDE_ON = byteArrayOf(ESC, EM, 0x20)
    val DOUBLE_WIDE_OFF = byteArrayOf(ESC, EM, NLL)

    val DOUBLE_HIGH_ON = byteArrayOf(ESC, EM, 0x10)
    val DOUBLE_HIGH_OFF = byteArrayOf(ESC, EM, NLL)

    val DOUBLE_WH_ON = byteArrayOf(ESC, EM, 0x30)
    val DOUBLE_WH_OFF = byteArrayOf(ESC, EM, NLL)

    val NEW_LINE = byteArrayOf(CR)

    val FORM_FEED = byteArrayOf(CR, CR, CR, CR, CR) // = 5 NEW_LINE
}
