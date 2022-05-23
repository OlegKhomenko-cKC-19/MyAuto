package ua.myauto.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.firebase.RemoteRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.domain.models.User
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call

class RegisterVM : InputFieldsViewModel() {

    val showLoadingLiveData = MutableLiveData(false)
    val onRegisterSuccessLiveData = MutableLiveData(false)
    val onRegisterFailureLiveData = MutableLiveData(false)
    val navigateToLoginLiveData = MutableLiveData(false)

    fun onLoginClick() = navigateToLoginLiveData.call()

    fun register(username: String, email: String, password: String, repeatedPassword: String) {
        if (password != repeatedPassword) {
            onRegisterFailureLiveData.call()
            return
        }

        val userRepository = UserRepository.newInstance()

        showLoadingLiveData.value = true
        val newUser = User(username = username, email = email, password = password)
        RemoteRepository.isUserExists(username, onCheckResult = { userExists ->
            if (!userExists) {
                RemoteRepository.registerUser(newUser) { isRegistered ->
                    if (isRegistered) {
                        viewModelScope.launch {
                            userRepository.insertUser(newUser)
                            LocalRepository.username = username

                            withContext(Dispatchers.Main) {
                                showLoadingLiveData.value = false
                                onRegisterSuccessLiveData.call()
                            }
                        }
                    } else {
                        showLoadingLiveData.value = false
                        onRegisterFailureLiveData.call()
                    }
                }
            } else {
                showLoadingLiveData.value = false
                onRegisterFailureLiveData.call()
            }
        })
    }
}