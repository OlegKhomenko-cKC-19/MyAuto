package ua.myauto.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar

object TimeUtils {
    private const val DATE_FORMAT = "dd.MM.yyyy"
    private const val DATE_FORMAT_SHORT = "dd.MM.yy"

    @SuppressLint("SimpleDateFormat")
    fun formatMillis(millis: Long, shortFormat: Boolean = false): String {
        val formatter = SimpleDateFormat(if (shortFormat) DATE_FORMAT_SHORT else DATE_FORMAT)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }

        return formatter.format(calendar.time)
    }
}
