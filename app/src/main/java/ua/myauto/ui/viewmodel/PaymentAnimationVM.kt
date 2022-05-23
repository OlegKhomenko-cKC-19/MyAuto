package ua.myauto.ui.viewmodel

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.myauto.R
import ua.myauto.data.db.repository.ExpenseRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.expense.Expense
import ua.myauto.domain.models.expense.ExpenseCategory.INSURANCE
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.extensions.call
import ua.myauto.utils.DEFAULT_INT
import ua.myauto.utils.KEY_INSURANCE_DETAILS

class PaymentAnimationVM : ViewModel() {

    private var insurance: Insurance? = null

    val navigateToMainLiveData = MutableLiveData(false)
    val showDetailsTextLiveData = MutableLiveData<Insurance?>()
    val showSuccessTextLiveData = MutableLiveData<@StringRes Int>(DEFAULT_INT)

    fun parseIntent(intent: Intent) {
        val payInsurance = intent.getSerializableExtra(KEY_INSURANCE_DETAILS) as Insurance

        insurance = payInsurance
        processData(payInsurance)
        showDetailsTextLiveData.value = payInsurance
    }

    private fun processData(insurance: Insurance) {
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            val expense = Expense(
                category = INSURANCE,
                sum = insurance.price,
                comment = "Страхування на авто ${insurance.carNumber}"
            )

            ExpenseRepository.newInstance(user).addExpense(expense)
            RemoteRepository.addExpense(expense) { }
        }
    }

    fun onFirstAnimationDone() {
        showSuccessTextLiveData.value = R.string.insurance_payment_success
        showSuccessTextLiveData.value = DEFAULT_INT
    }

    fun onSecondAnimationDone() {
        navigateToMainLiveData.call()
    }
}