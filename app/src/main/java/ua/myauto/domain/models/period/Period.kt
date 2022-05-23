package ua.myauto.domain.models.period

import java.io.Serializable
import ua.myauto.utils.ONE_DAY_MILLIS

sealed class Period(
    val days: Int,
    var periodStart: Long = System.currentTimeMillis(),
    private val isReversed: Boolean = false
) : Serializable {
    class Week(isReversed: Boolean = false) : Period(WEEK_DAYS, isReversed = isReversed)
    class Month(isReversed: Boolean = false) : Period(MONTH_DAYS, isReversed = isReversed)
    class Year(isReversed: Boolean = false) : Period(YEAR_DAYS, isReversed = isReversed)
    class Custom(days: Int, isReversed: Boolean = false) : Period(days, isReversed = isReversed)

    val periodEnd: Long
        get() = periodStart + days * ONE_DAY_MILLIS

    init {
        if (isReversed) {
            periodStart = System.currentTimeMillis() - days * ONE_DAY_MILLIS
        }
    }

    companion object {
        private const val ZERO_DAYS = 0
        private const val WEEK_DAYS = 7
        private const val MONTH_DAYS = 30
        private const val YEAR_DAYS = 365

        val default get() = Custom(ZERO_DAYS)

        fun newCustomInstance(end: Long, isReversed: Boolean = false): Custom {
            val days = ((end - System.currentTimeMillis()) / ONE_DAY_MILLIS).toInt()
            return Custom(days, isReversed = isReversed)
        }

        fun newCustomInstance(start: Long, end: Long, isReversed: Boolean = false): Custom {
            val days = ((end - start) / ONE_DAY_MILLIS).toInt()
            return Custom(days, isReversed = isReversed).apply {
                periodStart = start
            }
        }
    }
}
