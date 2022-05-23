package ua.myauto.ui.viewmodel

import android.content.Intent
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.myauto.data.db.repository.ExpenseRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.statistics.ExpenseCategoryStatisticsItem
import ua.myauto.domain.models.period.Period
import ua.myauto.ui.activity.KEY_EXPENSE_CATEGORY_DETAILS

class ExpenseDetailsVM : ViewModel() {

    private var currentExpenseCategory: ExpenseCategoryStatisticsItem? = null
    val periodLiveData = MutableLiveData<Period?>(null)
    val sumLiveData = MutableLiveData<Float>()
    val backgroundLiveData = MutableLiveData<@ColorRes Int>()
    val categoryNameLiveData = MutableLiveData<@StringRes Int>()
    val operationsLiveData = MutableLiveData<List<Expense>>()

    fun parseIntent(intent: Intent) {
        val categoryStatisticsItem = intent.getSerializableExtra(
            KEY_EXPENSE_CATEGORY_DETAILS
        ) as ExpenseCategoryStatisticsItem

        periodLiveData.value = categoryStatisticsItem.period
        backgroundLiveData.value = categoryStatisticsItem.category.colorId
        categoryNameLiveData.value = categoryStatisticsItem.category.nameId

        currentExpenseCategory = categoryStatisticsItem
    }

    fun onResume() {
        updateData()
    }

    fun onDelete(expense: Expense) {
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            ExpenseRepository.newInstance(user).deleteExpense(expense)
            RemoteRepository.removeExpense(expense) {}

            currentExpenseCategory?.let {
                it.expenses.remove(expense)

                if (it.expenses.size == 0) {
                    updateData()
                } else {
                    sumLiveData.value = it.categorySum
                }
            }
        }
    }

    private fun updateData() {
        currentExpenseCategory?.let {
            operationsLiveData.value = it.expenses
            sumLiveData.value = it.categorySum
        }
    }
}