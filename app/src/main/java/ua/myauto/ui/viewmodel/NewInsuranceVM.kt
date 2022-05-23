package ua.myauto.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.insurance.InsuranceType
import ua.myauto.domain.models.insurance.NewInsurance
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call

class NewInsuranceVM : InputFieldsViewModel() {

    val showLoadingLiveData = MutableLiveData(false)
    val onCreateSuccessLiveData = MutableLiveData<NewInsurance?>(null)
    val onCreateFailureLiveData = MutableLiveData(false)

    fun onCreateClick(carNumber: String, insuranceType: InsuranceType) {
        showLoadingLiveData.value = true
        RemoteRepository.isInsuranceExists(carNumber) { exists ->
            if (!exists) {
                viewModelScope.launch {
                    val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
                    val insuranceRepository = InsuranceRepository.newInstance(user)

                    val isCarWithInsurance = insuranceRepository.isInsuranceExistsFor(carNumber)
                    withContext(Dispatchers.Main) {
                        showLoadingLiveData.value = false
                        if (isCarWithInsurance) {
                            onCreateFailureLiveData.call()
                        } else {
                            onCreateSuccessLiveData.value = NewInsurance(insuranceType, carNumber)
                            onCreateSuccessLiveData.value = null
                        }
                    }
                }
            } else {
                showLoadingLiveData.value = false
                onCreateFailureLiveData.call()
            }
        }
    }

    override fun validateInput(inputFieldsText: Array<String>): Boolean {
        return inputFieldsText.all {
            it.matches("[A-ZА-Я]{2}[0-9]{4}[A-ZА-Я]{2}".toRegex()) // ua car number style
        }
    }
}