package ua.myauto.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.myauto.data.db.dao.ExpenseDao
import ua.myauto.data.db.dao.InsuranceDao
import ua.myauto.data.db.dao.UserDao
import ua.myauto.data.db.entities.UserEntity
import ua.myauto.data.db.entities.expense.ExpenseEntity
import ua.myauto.data.db.entities.expense.ExpensesUsersDataEntity
import ua.myauto.data.db.entities.insurance.InsuranceEntity
import ua.myauto.data.db.entities.insurance.InsuranceReportEntity
import ua.myauto.data.db.entities.insurance.InsuranceReportsDataEntity
import ua.myauto.data.db.entities.insurance.InsuranceUsersDataEntity

@Database(
    entities = [
        InsuranceEntity::class,
        InsuranceReportEntity::class,
        InsuranceReportsDataEntity::class,
        InsuranceUsersDataEntity::class,
        ExpenseEntity::class,
        ExpensesUsersDataEntity::class,
        UserEntity::class
    ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun expenseDao(): ExpenseDao

    abstract fun insuranceDao(): InsuranceDao
}