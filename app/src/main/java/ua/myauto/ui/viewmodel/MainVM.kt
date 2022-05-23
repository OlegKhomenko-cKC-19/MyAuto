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
import ua.myauto.domain.models.categories.ScreenCategory
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.extensions.call

class MainVM : ViewModel() {

    val openScreenCategory = MutableLiveData<ScreenCategory?>(null)
    val openInsurancesScreen = MutableLiveData(false)

    fun onMyInsurancesClick() = openInsurancesScreen.call()

    fun onScreenCategoryClick(category: ScreenCategory) = with(openScreenCategory) {
        value = category
        value = null
    }
}