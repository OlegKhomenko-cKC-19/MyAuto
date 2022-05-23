package ua.myauto.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.extensions.call

class InsurancesVM : ViewModel() {

    val insurances = MutableLiveData<List<Insurance>>()
    val openNewInsuranceScreen = MutableLiveData(false)
    val openInsuranceDetails = MutableLiveData<Insurance?>(null)

    init {
        loadUserInsurances()
    }

    fun onResume() = loadUserInsurances()

    fun onAddInsuranceClick() = openNewInsuranceScreen.call()

    fun onInsuranceClick(insurance: Insurance) = with(openInsuranceDetails) {
        value = insurance
        value = null
    }

    private fun loadUserInsurances() {
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            val insurances = InsuranceRepository.newInstance(user).getInsurances()

            withContext(Dispatchers.Main) {
                this@InsurancesVM.insurances.value = insurances
            }
        }
    }

}