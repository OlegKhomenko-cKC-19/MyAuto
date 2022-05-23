package ua.myauto.domain.models.expense.statistics

import android.util.Log
import java.io.Serializable
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.domain.models.period.Period
import kotlin.math.roundToInt

private const val MAX_PERCENTS = 100

data class ExpenseStatistics(
    val period: Period,
    private val expenses: MutableList<Expense>
) : Serializable {

    val operations: List<Expense> get() = expenses
    val fullSum: Float get() = operations.sumOf { it.sum.toDouble() }.toFloat()
    val categoryStatistics: List<ExpenseCategoryStatisticsItem> get() = getCategorizedExpenses()

    private fun getCategorizedExpenses(): List<ExpenseCategoryStatisticsItem> {
        val items = mutableListOf<ExpenseCategoryStatisticsItem>()

        ExpenseCategory.values().forEach { category ->
            val expenseStatistics = ExpenseCategoryStatisticsItem(
                category = category,
                percent = calculatePercentageFor(category),
                expenses = expensesForCategory(category)
            )

            if (expenseStatistics.amount != 0) {
                items.add(expenseStatistics)
            }
        }

        items.sortByDescending { it.categorySum }
        return items
    }

    private fun calculatePercentageFor(category: ExpenseCategory): Float {
        val fullSum = expenses.sumOf { expense -> expense.sum.toDouble() }.toFloat()
        val categorySum = expensesForCategory(category).sumOf { it.sum.toDouble() }.toFloat()

        if (categorySum == 0f)
            return 0f
        else
            return categorySum * MAX_PERCENTS / fullSum
    }

    private fun expensesForCategory(category: ExpenseCategory) = expenses
        .filter { it.category == category }
        .toMutableList()
}