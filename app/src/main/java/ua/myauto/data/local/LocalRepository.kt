package ua.myauto.data.local

import android.content.Context
import ua.myauto.App
import ua.myauto.utils.DEFAULT_INT

object LocalRepository {
    private const val SHARED_PREFS_NAME = "shared_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_DAYS = "days"

    private val sharedPrefs
        get() = App.instance.getSharedPreferences(
            SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )

    val isUserLoggedIn: Boolean
        get() = username != ""

    var username: String
        get() = sharedPrefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) {
            sharedPrefs.edit().putString(KEY_USER_NAME, value).apply()
        }

    var daysBeforePush: Int
        get() = sharedPrefs.getInt(KEY_DAYS, 1)
        set(value) {
            sharedPrefs.edit().putInt(KEY_DAYS, value).apply()
        }

    fun logout() {
        username = ""
    }
}