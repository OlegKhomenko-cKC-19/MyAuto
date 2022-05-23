package ua.myauto.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class InputFieldsViewModel : ViewModel() {

    val isDataInputCorrectLiveData = MutableLiveData(false)

    open fun onDataChanged(inputFieldsText: Array<String>) {
        isDataInputCorrectLiveData.value = validateInput(inputFieldsText)
    }

    open fun validateInput(inputFieldsText: Array<String>) = inputFieldsText.all { text ->
        text.isNotEmpty()
    }
}