package ua.myauto.data.db.repository

import java.lang.Exception
import ua.myauto.App
import ua.myauto.data.db.entities.expense.ExpensesUsersDataEntity
import ua.myauto.domain.mappers.toExpense
import ua.myauto.domain.mappers.toExpenseEntity
import ua.myauto.domain.models.User
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.statistics.ExpenseStatistics
import ua.myauto.domain.models.period.Period

class ExpenseRepository(application: App, private val user: User) {
    private val expenseDao = application.database.expenseDao()

    suspend fun getAllExpenses() =
        expenseDao.getAllUserExpenses(user.username).map { it.toExpense() }

    suspend fun getExpenseStatistics(period: Period): ExpenseStatistics {
        val expensesByPeriod = getExpensesFor(period)
        return ExpenseStatistics(period, expensesByPeriod.toMutableList())
    }

    private suspend fun getExpensesFor(expensePeriod: Period): List<Expense> =
        expenseDao.getUserExpensesByPeriod(
            user.username,
            periodStart = expensePeriod.periodStart,
            periodEnd = expensePeriod.periodEnd
        ).map {
            it.toExpense()
        }

    suspend fun addExpense(expense: Expense) {
        try {
            val expenseEntity = expense.toExpenseEntity()
            expenseDao.insertExpense(expenseEntity)
            expenseDao.insertExpenseUsers(
                ExpensesUsersDataEntity(
                    expenseId = expenseEntity.id,
                    expenseUsername = user.username
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun deleteExpense(expense: Expense) =
        expenseDao.deleteExpense(expense.toExpenseEntity())

    suspend fun deleteUserExpenses() {
        val expenses = getAllExpenses()
        expenses.forEach {
            deleteExpense(it)
        }
    }

    companion object {
        fun newInstance(user: User) = ExpenseRepository(App.instance, user)
    }
}