package ua.myauto.ui.activity

import android.content.Intent
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_register.register_btn
import kotlinx.android.synthetic.main.activity_register.register_email_et
import kotlinx.android.synthetic.main.activity_register.register_login_tv
import kotlinx.android.synthetic.main.activity_register.register_password_et
import kotlinx.android.synthetic.main.activity_register.register_repeat_password_et
import kotlinx.android.synthetic.main.activity_register.register_username_et
import ua.myauto.R
import ua.myauto.ui.activity.base.BaseActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.RegisterVM

class RegisterActivity : BaseActivity(), InputFieldsPage {
    override val layoutId: Int get() = R.layout.activity_register
    private val viewModel: RegisterVM by viewModels()

    override fun initViews() {
        register_username_et.addTextChangedListener(this, viewModel)
        register_email_et.addTextChangedListener(this, viewModel)
        register_password_et.addTextChangedListener(this, viewModel)
        register_repeat_password_et.addTextChangedListener(this, viewModel)

        register_login_tv.setOnClickListener {
            viewModel.onLoginClick()
        }

        register_btn.setOnClickListener {
            viewModel.register(
                username = register_username_et.input,
                email = register_email_et.input,
                password = register_password_et.input,
                repeatedPassword = register_repeat_password_et.input
            )
        }
    }

    override fun observeVM() {
        with(viewModel) {
            onRegisterFailureLiveData.observe(lifecycleOwner) { isFailure ->
                if (isFailure) toast(R.string.register_failure_msg)
            }

            onRegisterSuccessLiveData.observe(lifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }

            isDataInputCorrectLiveData.observe(lifecycleOwner) { isCorrect ->
                register_btn.isUserClickable = isCorrect
            }

            navigateToLoginLiveData.observe(lifecycleOwner) { navigate ->
                if (navigate) {
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@RegisterActivity)
                else
                    DialogLoading.dismiss()
            }
        }
    }

    override fun getInputFieldsData(): Array<String> = arrayOf(
        register_username_et.input,
        register_email_et.input,
        register_password_et.input,
        register_repeat_password_et.input
    )
}