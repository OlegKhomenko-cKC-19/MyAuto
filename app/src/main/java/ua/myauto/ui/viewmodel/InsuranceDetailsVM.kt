package ua.myauto.ui.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.utils.KEY_INSURANCE_DETAILS
import ua.myauto.utils.ONE_DAY_MILLIS

class InsuranceDetailsVM : ViewModel() {

    val currentInsurance = MutableLiveData<Insurance?>(null)
    val insuranceReports = MutableLiveData<List<InsuranceReport>>()
    val openNewReport = MutableLiveData<Insurance?>(null)
    val paymentRequired = MutableLiveData(false)
    val payInsurance = MutableLiveData<Insurance?>(null)
    val reportDetails = MutableLiveData<InsuranceReport?>(null)

    fun parseIntent(intent: Intent) {
        currentInsurance.value = intent.getSerializableExtra(KEY_INSURANCE_DETAILS) as Insurance
        paymentRequired.value = currentInsurance.value?.isPaymentRequired ?: false
    }

    fun onResume() {
        loadInsuranceReports()
    }

    fun onNewReportClick() {
        openNewReport.value = currentInsurance.value
        openNewReport.value = null
    }

    fun onReportClick(insuranceReport: InsuranceReport) {
        reportDetails.value = insuranceReport
        reportDetails.value = null
    }

    fun onPayClick() {
        val insurance = currentInsurance.value ?: return

        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            if (insurance.isPaymentRequired) {
                insurance.expiresAt += insurance.days * ONE_DAY_MILLIS
                InsuranceRepository.newInstance(user).setInsurancePaid(insurance)
                RemoteRepository.updateInsurance(insurance) {}
            }

            payInsurance.value = insurance
            payInsurance.value = null
        }
    }

    private fun loadInsuranceReports() {
        val insurance = currentInsurance.value ?: return

        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            val reports = InsuranceRepository.newInstance(user).getInsuranceReportsFor(insurance)

            Log.i("TAG", "show $reports")
            withContext(Dispatchers.Main) {
                insuranceReports.value = reports
            }
        }
    }
}