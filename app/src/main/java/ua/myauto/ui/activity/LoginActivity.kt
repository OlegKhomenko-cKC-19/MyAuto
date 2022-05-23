package ua.myauto.ui.activity

import android.content.Intent
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_login.login_btn
import kotlinx.android.synthetic.main.activity_login.login_password_et
import kotlinx.android.synthetic.main.activity_login.login_register_tv
import kotlinx.android.synthetic.main.activity_login.login_username_et
import ua.myauto.R
import ua.myauto.ui.activity.base.BaseActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.LoginVM

class LoginActivity : BaseActivity(), InputFieldsPage {
    override val layoutId: Int get() = R.layout.activity_login
    private val viewModel: LoginVM by viewModels()

    override fun initViews() {
        login_username_et.addTextChangedListener(this, viewModel)
        login_password_et.addTextChangedListener(this, viewModel)

        login_register_tv.setOnClickListener {
            viewModel.onRegisterClick()
        }

        login_btn.setOnClickListener {
            viewModel.login(
                username = login_username_et.input,
                password = login_password_et.input
            )
        }
    }

    override fun observeVM() {
        with(viewModel) {
            onLoginFailureLiveData.observe(lifecycleOwner) { isFailure ->
                if (isFailure) toast(R.string.failure_msg)
            }

            onLoginSuccessLiveData.observe(lifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }

            isDataInputCorrectLiveData.observe(lifecycleOwner) { isCorrect ->
                login_btn.isUserClickable = isCorrect
            }

            navigateToRegisterLiveData.observe(lifecycleOwner) { navigate ->
                if (navigate) {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    finish()
                }
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@LoginActivity)
                else
                    DialogLoading.dismiss()
            }
        }
    }

    override fun getInputFieldsData(): Array<String> = arrayOf(
        login_username_et.input,
        login_password_et.input
    )
}