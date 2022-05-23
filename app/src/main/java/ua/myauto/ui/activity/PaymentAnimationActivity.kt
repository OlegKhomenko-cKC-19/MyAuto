package ua.myauto.ui.activity

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_payment_animation.payment_animation_details_tv
import kotlinx.android.synthetic.main.activity_payment_animation.payment_animation_view
import ua.myauto.R
import ua.myauto.ui.activity.base.BaseActivity
import ua.myauto.ui.viewmodel.PaymentAnimationVM
import ua.myauto.utils.DEFAULT_INT

class PaymentAnimationActivity : BaseActivity() {
    override val layoutId: Int get() = R.layout.activity_payment_animation
    private val viewModel: PaymentAnimationVM by viewModels()
    private var animationNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { viewModel.parseIntent(intent) }
    }

    override fun onBackPressed() {}

    override fun initViews() {
        payment_animation_view.addAnimatorListener(createAnimatorListener())
    }

    private fun createAnimatorListener() = object : AnimatorListener {
        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            when (animationNumber) {
                1 -> {
                    animationNumber++
                    viewModel.onFirstAnimationDone()
                    payment_animation_view.setAnimation(R.raw.lottie_payment_success)
                    payment_animation_view.playAnimation()
                }

                2 -> {
                    viewModel.onSecondAnimationDone()
                }
            }
        }

        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {}
    }

    override fun observeVM() {
        with(viewModel) {
            showDetailsTextLiveData.observe(lifecycleOwner) {
                it?.let { insurance ->
                    payment_animation_details_tv.text = getString(
                        R.string.insurance_payment_details,
                        insurance.serial,
                        insurance.number,
                        insurance.price
                    )
                }
            }

            showSuccessTextLiveData.observe(lifecycleOwner) {
                if (it != DEFAULT_INT) {
                    payment_animation_details_tv.text = getString(it)
                }
            }

            navigateToMainLiveData.observe(lifecycleOwner) { navigate ->
                if (navigate) {
                    startActivity(
                        Intent(
                            this@PaymentAnimationActivity,
                            MainActivity::class.java
                        ).addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                }
            }
        }
    }
}