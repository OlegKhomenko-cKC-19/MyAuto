package ua.myauto.ui.extensions

import androidx.appcompat.widget.AppCompatButton

private const val CLICKABLE_ALPHA = 1f
private const val NOT_CLICKABLE_ALPHA = 0.6f

var AppCompatButton.isUserClickable
    get() = isEnabled
    set(value) {
        isEnabled = value
        alpha = if (value) CLICKABLE_ALPHA else NOT_CLICKABLE_ALPHA
    }