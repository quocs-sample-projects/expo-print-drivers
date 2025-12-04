package expo.modules.printerdrivers.utils.helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {
    private  var dateFormater = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())

    fun formatDate(date: Date): String {
        return dateFormater.format(date)
    }
}