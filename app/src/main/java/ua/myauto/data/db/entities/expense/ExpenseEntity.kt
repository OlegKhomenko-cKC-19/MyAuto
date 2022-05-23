package ua.myauto.data.db.entities.expense

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: Int,
    var categoryId: Int,
    var sum: Float,
    var comment: String,
    var issuedAt: Long
)