package ua.myauto.ui.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun Context.toast(@StringRes messageId: Int) =
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()

fun Context.drawable(@DrawableRes drawableId: Int) =
    ContextCompat.getDrawable(this, drawableId)

fun Context.getString(@StringRes messageId: Int?) = messageId?.let { getString(it) } ?: ""