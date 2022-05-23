package ua.myauto.data.db.entities.expense

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID
import ua.myauto.data.db.entities.UserEntity

@Entity(
    tableName = "Expenses_Users",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["username"],
            childColumns = ["expenseUsername"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpensesUsersDataEntity(
    @PrimaryKey
    var exp_us_id: Int = System.currentTimeMillis().toInt(),
    var expenseId: Int, var expenseUsername: String
)