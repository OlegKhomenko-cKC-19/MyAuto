package ua.myauto.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.myauto.data.db.repository.ExpenseRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call

class NewExpenseVM : InputFieldsViewModel() {

    val showBottomSheetWithCategoryLiveData = MutableLiveData<ExpenseCategory?>(null)
    val showLoadingLiveData = MutableLiveData(false)
    val onCreateFailedLiveData = MutableLiveData(false)
    val finishActivityLiveData = MutableLiveData(false)

    fun onCategoryClick(category: ExpenseCategory) {
        showBottomSheetWithCategoryLiveData.value = category
        showBottomSheetWithCategoryLiveData.value = null
    }

    fun onCreateExpenseClick(category: ExpenseCategory, sum: Float, comment: String) {
        val newExpense = Expense(category = category, sum = sum, comment = comment)
        showLoadingLiveData.value = true
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            ExpenseRepository.newInstance(user).addExpense(newExpense)
            RemoteRepository.addExpense(newExpense) { isAdded ->
                if (!isAdded) {
                    onCreateFailedLiveData.call()
                }

                showLoadingLiveData.value = false
                finishActivityLiveData.call()
            }
        }
    }
}