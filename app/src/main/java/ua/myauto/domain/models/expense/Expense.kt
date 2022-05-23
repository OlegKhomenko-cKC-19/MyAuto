package ua.myauto.domain.models.expense

import java.io.Serializable

data class Expense(
    val id: Int = System.currentTimeMillis().toInt(),
    var category: ExpenseCategory,
    val sum: Float,
    val comment: String,
    val issuedAt: Long = System.currentTimeMillis()
) : Serializable