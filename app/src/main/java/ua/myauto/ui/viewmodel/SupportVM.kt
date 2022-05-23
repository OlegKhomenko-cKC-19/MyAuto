package ua.myauto.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sargue.mailgun.Configuration
import net.sargue.mailgun.Mail
import ua.myauto.R
import ua.myauto.data.db.repository.UserRepository
import ua.myauto.data.local.LocalRepository
import ua.myauto.ui.common.InputFieldsViewModel
import ua.myauto.ui.extensions.call
import ua.myauto.utils.DEFAULT_INT

class SupportVM : InputFieldsViewModel() {

    val showLoadingLiveData = MutableLiveData(false)
    val emailLiveData = MutableLiveData<String>()
    val sendMessageLiveData = MutableLiveData<@StringRes Int>(DEFAULT_INT)
    val finishActivityLiveData = MutableLiveData(false)

    init {
        viewModelScope.launch {
            val user = UserRepository.newInstance(LocalRepository.username).getCurrentUser()
            withContext(Dispatchers.Main) {
                emailLiveData.value = user.email
            }
        }
    }

    fun onSendClick(companyName: String, email: String, responseMessage: String) {
        showLoadingLiveData.value = true

        val configuration: Configuration = Configuration()
            .domain("sandbox77536cc502f14a3289cf7bdfd81b2c6e.mailgun.org")
            .apiKey("46953d434ce854ebb9f8f1b09a696b97-100b5c8d-41036c80")
            .from("Страхова компанія $companyName", "no-reply@insurance.com")

        CoroutineScope(Dispatchers.IO).launch {
            Mail.using(configuration)
                .to(email)
                .subject("Відповідь на запитання")
                .text(responseMessage)
                .build()
                .send()

            withContext(Dispatchers.Main) {
                showLoadingLiveData.value = false
                sendMessageLiveData.value = R.string.support_sent_msg
                finishActivityLiveData.call()
            }
        }
    }
}