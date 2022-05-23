package ua.myauto.domain.mappers

import ua.myauto.data.db.entities.expense.ExpenseEntity
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.ExpenseCategory

fun Expense.toExpenseEntity() = ExpenseEntity(
    id = id,
    categoryId = category.ordinal,
    sum = sum,
    comment = comment,
    issuedAt = issuedAt
)

fun ExpenseEntity.toExpense() = Expense(
    id = id,
    category = ExpenseCategory.by(categoryId),
    sum = sum,
    comment = comment,
    issuedAt = issuedAt
)