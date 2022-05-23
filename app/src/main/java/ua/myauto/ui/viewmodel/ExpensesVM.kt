package ua.myauto.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.ExpenseRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.expense.statistics.ExpenseCategoryStatisticsItem
import ua.myauto.domain.models.expense.statistics.ExpenseStatistics
import ua.myauto.domain.models.period.Period
import ua.myauto.ui.extensions.call

class ExpensesVM : ViewModel() {
    val statisticsLiveData = MutableLiveData<ExpenseStatistics?>(null)
    val periodLiveData = MutableLiveData<Period>(Period.Year(isReversed = true))
    val showProgressLiveData = MutableLiveData(true)
    val showPeriodsDialogLiveData = MutableLiveData<Period?>(null)
    val navigateNewExpenseLiveData = MutableLiveData(false)
    val navigateCategoryDetailsLiveData = MutableLiveData<ExpenseCategoryStatisticsItem?>(null)

    fun onResume() {
        loadExpenses()
    }

    fun onCalendarClicked() {
        showPeriodsDialogLiveData.postValue(periodLiveData.value)
    }

    fun onPeriodSelected(period: Period) {
        showPeriodsDialogLiveData.value = null
        periodLiveData.value = period
        loadExpenses()
    }

    fun onNewExpenseClick() {
        navigateNewExpenseLiveData.call()
    }

    fun onCategoryDetailsClick(item: ExpenseCategoryStatisticsItem) {
        navigateCategoryDetailsLiveData.value = item
        navigateCategoryDetailsLiveData.value = null
    }

    private fun loadExpenses() {
        showProgressLiveData.value = true

        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            val statistics = periodLiveData.value?.let { period ->
                ExpenseRepository.newInstance(user).getExpenseStatistics(period).apply {
                    categoryStatistics.forEach { category ->
                        category.period = period
                    }
                }
            }

            withContext(Dispatchers.Main) {
                showProgressLiveData.value = false
                statisticsLiveData.value = statistics
            }
        }
    }
}