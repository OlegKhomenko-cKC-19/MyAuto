package ua.myauto.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call

class LoginVM : InputFieldsViewModel() {

    val showLoadingLiveData = MutableLiveData(false)
    val onLoginSuccessLiveData = MutableLiveData(false)
    val onLoginFailureLiveData = MutableLiveData(false)
    val navigateToRegisterLiveData = MutableLiveData(false)

    fun onRegisterClick() = navigateToRegisterLiveData.call()

    fun login(username: String, password: String) {
        val userRepository = UserRepository.newInstance()

        showLoadingLiveData.value = true
        RemoteRepository.loginUser(username, password, onLoginResult = { user ->
            if (user == null) {
                showLoadingLiveData.value = false
                onLoginFailureLiveData.call()
                return@loginUser
            }

            LocalRepository.username = username
            viewModelScope.launch {
                if (!userRepository.isUserExists(username)) {
                    userRepository.insertUser(user)
                }

                RemoteRepository.mergeAllUserData(user) {
                    showLoadingLiveData.value = false

                    if (LocalRepository.isUserLoggedIn) {
                        onLoginSuccessLiveData.call()
                    } else {
                        onLoginFailureLiveData.call()
                    }
                }
            }
        })
    }
}