package ua.myauto.ui.extensions

import androidx.lifecycle.MutableLiveData

fun MutableLiveData<Boolean>.call() {
    value = true
    value = false
}