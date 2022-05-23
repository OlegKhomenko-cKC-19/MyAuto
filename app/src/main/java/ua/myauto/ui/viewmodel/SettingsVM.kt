package ua.myauto.ui.viewmodel

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.R
import ua.myauto.data.db.repository.ExpenseRepository
import ua.myauto.data.db.repository.InsuranceRepository
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call
import ua.myauto.utils.DEFAULT_INT
import ua.myauto.utils.NetworkUtils

class SettingsVM : InputFieldsViewModel() {

    val usernameLiveData = MutableLiveData(LocalRepository.username)
    val showLoadingLiveData = MutableLiveData(false)
    val restartAppLiveData = MutableLiveData(false)
    val showMessageLiveData = MutableLiveData<@StringRes Int>()

    fun onLogoutClick() {
        showLoadingLiveData.value = true
        viewModelScope.launch {
            val userRepository = UserRepository.newInstance(LocalRepository.username)
            val user = userRepository.getCurrentUser()
            val insuranceRepository = InsuranceRepository.newInstance(user)
            val expenseRepository = ExpenseRepository.newInstance(user)
            insuranceRepository.deleteUserReports()
            insuranceRepository.deleteUserInsurances()
            expenseRepository.deleteUserExpenses()
            userRepository.removeUser(user)
            LocalRepository.logout()

            withContext(Dispatchers.Main) {
                showLoadingLiveData.value = false
                restartAppLiveData.call()
            }
        }
    }

    fun onSaveDaysClick(days: Int) {
        if (days > 0) {
            LocalRepository.daysBeforePush = days
            showMessageLiveData.value = R.string.settings_saved_msg
            showMessageLiveData.value = DEFAULT_INT
            onDataChanged(arrayOf()) //block button
        }
    }

    fun onSyncClick() {
        if (!NetworkUtils.isNetworkConnected) {
            showMessageLiveData.value = R.string.settings_sync_no_internet
            showMessageLiveData.value = DEFAULT_INT
            return
        }

        showLoadingLiveData.value = true
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            RemoteRepository.mergeAllUserData(user) {
                showLoadingLiveData.value = false
                showMessageLiveData.value = R.string.settings_synced_msg
                showMessageLiveData.value = DEFAULT_INT
            }
        }
    }
}