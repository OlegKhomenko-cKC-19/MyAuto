package ua.myauto.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.activity_new_insurance.new_insurance_btn
import kotlinx.android.synthetic.main.activity_new_insurance.new_insurance_car_number_et
import kotlinx.android.synthetic.main.activity_new_insurance.new_insurance_casco_type_rb
import kotlinx.android.synthetic.main.activity_new_insurance.new_insurance_toolbar
import ua.myauto.R
import ua.myauto.domain.models.insurance.InsuranceType.CASCO
import ua.myauto.domain.models.insurance.InsuranceType.OSCE
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.NewInsuranceVM
import ua.myauto.utils.KEY_NEW_INSURANCE

class NewInsuranceActivity : ToolbarActivity(), InputFieldsPage {
    override val layoutId: Int get() = R.layout.activity_new_insurance
    override val toolbarTitleId: Int get() = R.string.new_insurance_title
    override val toolbarView: View get() = new_insurance_toolbar
    override val isFinishRequired: Boolean get() = false

    private val viewModel: NewInsuranceVM by viewModels()

    override fun initViews() {
        new_insurance_car_number_et.addTextChangedListener(this, viewModel)
        new_insurance_btn.setOnClickListener {
            val carNumber = new_insurance_car_number_et.input
            val insuranceType = if (new_insurance_casco_type_rb.isChecked) CASCO else OSCE

            viewModel.onCreateClick(carNumber, insuranceType)
        }
    }

    override fun observeVM() {
        with(viewModel) {
            isDataInputCorrectLiveData.observe(lifecycleOwner) { isCorrect ->
                new_insurance_btn.isUserClickable = isCorrect
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@NewInsuranceActivity)
                else
                    DialogLoading.dismiss()
            }

            onCreateFailureLiveData.observe(lifecycleOwner) { failure ->
                if (failure) toast(R.string.new_insurance_exists_msg)
            }

            onCreateSuccessLiveData.observe(lifecycleOwner) { newInsurance ->
                newInsurance ?: return@observe
                startActivity(
                    Intent(
                        this@NewInsuranceActivity,
                        NewInsuranceInputActivity::class.java
                    ).apply {
                        putExtras(bundleOf(KEY_NEW_INSURANCE to newInsurance))
                    }
                )
            }
        }
    }

    override fun getInputFieldsData(): Array<String> = arrayOf(new_insurance_car_number_et.input)
}