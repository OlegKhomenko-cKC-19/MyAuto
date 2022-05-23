package ua.myauto.ui.activity

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.Toolbar
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_settings.settings_days_et
import kotlinx.android.synthetic.main.activity_settings.settings_days_update_btn
import kotlinx.android.synthetic.main.activity_settings.settings_logout_btn
import kotlinx.android.synthetic.main.activity_settings.settings_sync_btn
import kotlinx.android.synthetic.main.activity_settings.settings_toolbar
import kotlinx.android.synthetic.main.activity_settings.settings_username_tv
import ua.myauto.R
import ua.myauto.data.local.LocalRepository
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.numberInput
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.SettingsVM
import ua.myauto.utils.DEFAULT_INT

class SettingsActivity : ToolbarActivity(isSettingsButtonVisible = false), InputFieldsPage {
    override val toolbarTitleId: Int
        get() = R.string.settings_title
    override val toolbarView: View
        get() = settings_toolbar
    override val layoutId: Int
        get() = R.layout.activity_settings

    private val viewModel: SettingsVM by viewModels()

    override fun initViews() {
        settings_days_et.setText(LocalRepository.daysBeforePush.toString())
        settings_days_et.addTextChangedListener(this, viewModel)
        settings_logout_btn.setOnClickListener {
            viewModel.onLogoutClick()
        }
        settings_days_update_btn.setOnClickListener {
            viewModel.onSaveDaysClick(settings_days_et.numberInput)
        }
        settings_sync_btn.setOnClickListener {
            viewModel.onSyncClick()
        }
    }

    override fun observeVM() {
        with(viewModel) {
            isDataInputCorrectLiveData.observe(lifecycleOwner) {
                settings_days_update_btn.isUserClickable = it
            }

            showMessageLiveData.observe(lifecycleOwner) {
                if (it != DEFAULT_INT) {
                    toast(it)
                }
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@SettingsActivity)
                else
                    DialogLoading.dismiss()
            }

            restartAppLiveData.observe(lifecycleOwner) {
                if (it) {
                    startActivity(Intent(this@SettingsActivity, SplashActivity::class.java))
                    finish()
                }
            }

            usernameLiveData.observe(lifecycleOwner) {
                settings_username_tv.text = it
            }
        }
    }

    override fun getInputFieldsData(): Array<String> = arrayOf(settings_days_et.input)
}