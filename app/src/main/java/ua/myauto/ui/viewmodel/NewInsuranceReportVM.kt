package ua.myauto.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
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
import ua.myauto.domain.models.report.ReportRiskType
import ua.myauto.domain.models.report.ReportRiskType.Crash
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call
import ua.myauto.utils.KEY_INSURANCE_DETAILS
import ua.myauto.utils.TimeUtils

private const val PLACE_INDEX = 5
private const val CRASH_INDEX = 14

class NewInsuranceReportVM : InputFieldsViewModel() {

    val riskTypeLiveData = MutableLiveData<ReportRiskType>(ReportRiskType.default)
    val isPlaceEqualsRegistrationLiveData = MutableLiveData(false)
    val showDatePickerLiveData = MutableLiveData(false)
    val emailLiveData = MutableLiveData<String>()
    val firstNameLiveData = MutableLiveData<String>()
    val lastNameLiveData = MutableLiveData<String>()
    val dateLiveData = MutableLiveData<String>()
    val insuranceLiveData = MutableLiveData<Insurance?>(null)
    val showLoadingLiveData = MutableLiveData(false)
    val onSendSuccessLiveData = MutableLiveData(false)
    val onSendFailedLiveData = MutableLiveData(false)

    init {
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            withContext(Dispatchers.Main) {
                dateLiveData.value = TimeUtils.formatMillis(System.currentTimeMillis())
                firstNameLiveData.value = user.firstName
                lastNameLiveData.value = user.lastName
                emailLiveData.value = user.email
            }
        }
    }

    fun parseIntent(intent: Intent) {
        insuranceLiveData.value = intent.getSerializableExtra(KEY_INSURANCE_DETAILS) as Insurance
    }

    override fun validateInput(inputFieldsText: Array<String>): Boolean {
        var listOfFields = inputFieldsText.toMutableList()
        when {
            isPlaceEqualsRegistrationLiveData.value == true -> {
                listOfFields.removeAt(PLACE_INDEX)
            }

            riskTypeLiveData.value !is Crash -> {
                listOfFields = listOfFields.subList(0, CRASH_INDEX)
            }
        }

        return super.validateInput(listOfFields.toTypedArray())
    }

    fun onDateChanged(millis: Long) {
        dateLiveData.value = TimeUtils.formatMillis(millis)
    }

    fun onRiskTypeChanged(riskType: ReportRiskType) {
        riskTypeLiveData.value = riskType
    }

    fun onAddressesEqualsSwitchChanged(areEquals: Boolean) {
        isPlaceEqualsRegistrationLiveData.value = areEquals
    }

    fun onDateClick() {
        showDatePickerLiveData.call()
    }

    fun onSendClick(insuranceReport: InsuranceReport) {
        showLoadingLiveData.value = true
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            InsuranceRepository.newInstance(user).addInsuranceReport(insuranceReport)
            RemoteRepository.addInsuranceReport(insuranceReport) { isAdded ->
                if (!isAdded) {
                    onSendFailedLiveData.call()
                }

                showLoadingLiveData.value = false
                onSendSuccessLiveData.call()
            }
        }
    }
}