package ua.myauto.domain.models.expense.statistics

import java.io.Serializable
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.domain.models.period.Period
import ua.myauto.domain.models.period.Period.Companion
import kotlin.math.exp

data class ExpenseCategoryStatisticsItem(
    val category: ExpenseCategory,
    val percent: Float,
    val expenses: MutableList<Expense>,
    var period: Period = Period.default
) : Serializable {
    val amount: Int get() = expenses.size
    val categorySum: Float get() = expenses.sumOf { it.sum.toDouble() }.toFloat()
}