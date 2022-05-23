package ua.myauto.ui.activity

import android.content.Intent
import androidx.activity.viewModels
import ua.myauto.R
import ua.myauto.ui.activity.base.BaseActivity
import ua.myauto.ui.viewmodel.SplashVM

class SplashActivity : BaseActivity() {
    override val layoutId: Int get() = R.layout.activity_splash
    private val viewModel: SplashVM by viewModels()

    override fun initViews() {}

    override fun observeVM() {
        with(viewModel) {
            navigateToMainLiveData.observe(lifecycleOwner) { navigate ->
                if (navigate) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }

            navigateToLoginLiveData.observe(lifecycleOwner) { navigate ->
                if (navigate) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}