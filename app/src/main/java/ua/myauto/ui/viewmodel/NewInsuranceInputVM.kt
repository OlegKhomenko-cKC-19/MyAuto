package ua.myauto.ui.viewmodel

import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.insurance.FuelType
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.domain.models.insurance.InsuranceType.CASCO
import ua.myauto.domain.models.insurance.NewInsurance
import ua.myauto.domain.models.insurance.NewInsuranceParams
import ua.myauto.domain.models.insurance.VehicleType
import ua.myauto.domain.models.period.Period
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call
import ua.myauto.utils.DEFAULT_INT
import ua.myauto.utils.KEY_NEW_INSURANCE
import ua.myauto.utils.TimeUtils

class NewInsuranceInputVM : InputFieldsViewModel() {

    val titleLiveDataResLiveData = MutableLiveData<@StringRes Int>(DEFAULT_INT)
    val showDatePickerDialogLiveData = MutableLiveData(false)
    val showCascoFieldsLiveData = MutableLiveData(false)
    val showConfirmationDialogWithParamsLiveData = MutableLiveData<NewInsuranceParams?>()
    val expiredAtLiveData = MutableLiveData<String>()
    val showLoadingLiveData = MutableLiveData(false)
    val onCreateSuccessLiveData = MutableLiveData<Insurance?>()
    val onCreateFailedLiveData = MutableLiveData(false)

    private var newInsuranceParams: NewInsuranceParams? = null
    private var currentPeriodSelected: Period = Period.Week()

    fun parseIntent(intent: Intent) {
        val insurance = intent.getSerializableExtra(KEY_NEW_INSURANCE) as NewInsurance
        newInsuranceParams = NewInsuranceParams(insurance)
        setPeriod(currentPeriodSelected)

        with(insurance.insuranceType) {
            titleLiveDataResLiveData.value = nameId
            showCascoFieldsLiveData.value = (this == CASCO)
        }
    }

    fun onVehicleTypeSelected(vehicleType: VehicleType) {
        newInsuranceParams?.vehicleType = vehicleType
    }

    fun onEngineVolumeChanged(engineVolume: Int) {
        newInsuranceParams?.engineVolumeCubes = engineVolume
    }

    fun onFuelTypeSelected(fuelType: FuelType) {
        newInsuranceParams?.fuelType = fuelType
    }

    fun onDistanceChanged(distance: Int) {
        newInsuranceParams?.distanceK = distance
    }

    fun onPriceChanged(price: Int) {
        newInsuranceParams?.priceUSD = price
    }

    fun onPeriodSelected(period: Period) {
        if (period == currentPeriodSelected) return

        if (period is Period.Custom) {
            showDatePickerDialogLiveData.call()
        } else {
            setPeriod(period)
        }
    }

    fun onCustomPeriodReceived(period: Period.Custom) {
        setPeriod(period)
    }

    private fun setPeriod(period: Period) {
        currentPeriodSelected = period
        newInsuranceParams?.period = period
        expiredAtLiveData.value = TimeUtils.formatMillis(period.periodEnd)
    }

    fun onCreateButtonClick() {
        showConfirmationDialogWithParamsLiveData.value = newInsuranceParams
        showConfirmationDialogWithParamsLiveData.value = null
    }

    fun onConfirmed() {
        newInsuranceParams?.let { params ->
            showLoadingLiveData.value = true
            val insurance = Insurance.from(params)
            viewModelScope.launch {
                val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
                InsuranceRepository.newInstance(user).addInsurance(insurance)
                RemoteRepository.addInsurance(insurance, onInsuranceAdded = { isAdded ->
                    if (!isAdded) {
                        onCreateFailedLiveData.call()
                    }

                    showLoadingLiveData.value = false
                    onCreateSuccessLiveData.value = insurance
                    onCreateSuccessLiveData.value = null
                })
            }
        }
    }

    override fun validateInput(inputFieldsText: Array<String>): Boolean {
        val arrayToCheck = if (newInsuranceParams?.newInsurance?.insuranceType == CASCO)
            inputFieldsText
        else
            arrayOf(inputFieldsText.first())

        return super.validateInput(arrayToCheck)
    }
}