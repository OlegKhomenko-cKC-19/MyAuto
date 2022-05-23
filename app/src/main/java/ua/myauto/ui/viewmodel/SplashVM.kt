package ua.myauto.ui.viewmodel

import android.os.Handler
import android.os.Looper
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
import ua.myauto.domain.models.User
import ua.myauto.ui.extensions.call

private const val SPLASH_TIME_MILLIS = 1500L //2s

class SplashVM : ViewModel() {

    val navigateToMainLiveData = MutableLiveData(false)
    val navigateToLoginLiveData = MutableLiveData(false)
    private val isUserLoggedIn = LocalRepository.isUserLoggedIn

    init {
        viewModelScope.launch {
            if (isUserLoggedIn) {
                Log.i("TAG", "username: ${LocalRepository.username}")
                val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
                clearExpiredInsurances(user)
                mergeDataWithRemote(user) {
                    startLoadingAnimDelay()
                }
            }

            withContext(Dispatchers.Main) {
                startLoadingAnimDelay()
            }
        }
    }

    private fun startLoadingAnimDelay() {
        Handler(Looper.getMainLooper()).postDelayed(::navigate, SPLASH_TIME_MILLIS)
    }

    private suspend fun clearExpiredInsurances(user: User) {
        if (isUserLoggedIn) {
            val insuranceRepository = InsuranceRepository.newInstance(user)
            val insurances = insuranceRepository.getInsurances()
            insurances.forEach {
                if (it.isExpired) {
                    insuranceRepository.deleteInsurance(it)
                    RemoteRepository.removeInsurance(it) {}
                }
            }
        }
    }

    private fun mergeDataWithRemote(user: User, onDataMerged: () -> Unit) {
        RemoteRepository.mergeAllUserData(user, onDataMerged)
    }

    private fun navigate() {
        if (isUserLoggedIn) {
            navigateToMainLiveData.value = true
        } else {
            navigateToLoginLiveData.value = true
        }
    }
}