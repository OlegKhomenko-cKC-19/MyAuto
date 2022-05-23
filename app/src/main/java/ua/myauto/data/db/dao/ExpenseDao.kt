package ua.myauto.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ua.myauto.data.db.entities.expense.ExpenseEntity
import ua.myauto.data.db.entities.expense.ExpensesUsersDataEntity

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM Expenses INNER JOIN Expenses_Users ON Expenses_Users.expenseId = Expenses.id WHERE Expenses_Users.expenseUsername = :username")
    suspend fun getAllUserExpenses(username: String): List<ExpenseEntity>

    @Query("SELECT * FROM Expenses INNER JOIN Expenses_Users ON Expenses_Users.expenseId = Expenses.id WHERE Expenses_Users.expenseUsername = :username AND Expenses.issuedAt >= :periodStart AND Expenses.issuedAt <= :periodEnd")
    suspend fun getUserExpensesByPeriod(
        username: String,
        periodStart: Long,
        periodEnd: Long
    ): List<ExpenseEntity>

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert
    suspend fun insertExpenseUsers(expensesUsersData: ExpensesUsersDataEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
}