package ua.myauto.ui.activity

import android.view.View
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_personal_email_et
import kotlinx.android.synthetic.main.activity_support.support_company_group
import kotlinx.android.synthetic.main.activity_support.support_email_et
import kotlinx.android.synthetic.main.activity_support.support_knyazha_rb
import kotlinx.android.synthetic.main.activity_support.support_oranta_rb
import kotlinx.android.synthetic.main.activity_support.support_question_et
import kotlinx.android.synthetic.main.activity_support.support_send_btn
import kotlinx.android.synthetic.main.activity_support.support_tas_rb
import kotlinx.android.synthetic.main.activity_support.support_toolbar
import kotlinx.android.synthetic.main.activity_support.support_unika_rb
import ua.myauto.R
import ua.myauto.data.local.LocalRepository
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.SupportVM
import ua.myauto.utils.DEFAULT_INT

class SupportActivity : ToolbarActivity(), InputFieldsPage {
    override val layoutId: Int
        get() = R.layout.activity_support
    override val toolbarTitleId: Int
        get() = R.string.category_support
    override val toolbarView: View
        get() = support_toolbar

    private val viewModel: SupportVM by viewModels()

    override fun getInputFieldsData(): Array<String> = arrayOf(
        support_email_et.input,
        support_question_et.input
    )

    private fun getCompanyName(): String {
        return when (support_company_group.checkedRadioButtonId) {
            support_unika_rb.id -> support_unika_rb.text.toString()
            support_tas_rb.id -> support_tas_rb.text.toString()
            support_knyazha_rb.id -> support_knyazha_rb.text.toString()
            support_oranta_rb.id -> support_oranta_rb.text.toString()
            else -> ""
        }
    }

    private fun getReplyEmailText(): String {
        return getString(
            R.string.support_reply_text_placeholder,
            LocalRepository.username,
            support_question_et.input
        )
    }

    override fun initViews() {
        support_email_et.addTextChangedListener(this, viewModel)
        support_question_et.addTextChangedListener(this, viewModel)
        support_send_btn.setOnClickListener {
            viewModel.onSendClick(
                getCompanyName(),
                support_email_et.input,
                getReplyEmailText()
            )
        }
    }

    override fun observeVM() {
        with(viewModel) {
            isDataInputCorrectLiveData.observe(lifecycleOwner) {
                support_send_btn.isUserClickable = it
            }

            emailLiveData.observe(lifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    support_email_et.setText(it)
                }
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@SupportActivity)
                else
                    DialogLoading.dismiss()
            }

            sendMessageLiveData.observe(lifecycleOwner) {
                if (it != DEFAULT_INT) toast(it)
            }

            finishActivityLiveData.observe(lifecycleOwner) {
                if (it) onBackPressed()
            }
        }
    }
}