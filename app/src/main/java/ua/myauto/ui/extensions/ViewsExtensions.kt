package ua.myauto.ui.extensions

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.common.InputFieldsViewModel

val EditText.input get() = text.toString()
val EditText.numberInput get() = input.toIntOrNull() ?: 0
val EditText.floatInput get() = input.toFloatOrNull() ?: 0f

fun EditText.addTextChangedListener(
    inputFieldsPage: InputFieldsPage,
    inputFieldsViewModel: InputFieldsViewModel
) {
    addTextChangedListener {
        inputFieldsViewModel.onDataChanged(inputFieldsPage.getInputFieldsData())
    }
}

fun WebView.load(url: String) {
    settings.javaScriptEnabled = true
    webViewClient = WebViewClient()
    loadUrl(url)
}
