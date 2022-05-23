package ua.myauto.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_new_insurance_create.new_insurance_dialog_no_btn
import kotlinx.android.synthetic.main.dialog_new_insurance_create.new_insurance_dialog_tv
import kotlinx.android.synthetic.main.dialog_new_insurance_create.new_insurance_dialog_yes_btn
import ua.myauto.R
import ua.myauto.domain.models.insurance.NewInsuranceParams
import ua.myauto.utils.TimeUtils

class NewInsuranceConfirmationDialog : DialogFragment() {

    var params: NewInsuranceParams? = null
    var onYesClicked: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_new_insurance_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    override fun onStart() {
        super.onStart()

        with(dialog?.window ?: return) {
            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))

            attributes = this.attributes.apply {
                gravity = Gravity.CENTER
            }
        }
    }

    private fun initViews() {
        val ctx = context ?: return
        params?.let { parameters ->
            val insuranceType = ctx.getString(parameters.newInsurance.insuranceType.shortNameId)
            val expirationTime = TimeUtils.formatMillis(parameters.period.periodEnd)
            val carNumber = parameters.newInsurance.carNumber
            val price = parameters.calculatePrice()

            new_insurance_dialog_tv.text = ctx.getString(
                R.string.new_insurance_dialog_text,
                insuranceType,
                carNumber,
                expirationTime,
                price
            )
        }
    }

    private fun initListeners() {
        new_insurance_dialog_yes_btn.setOnClickListener {
            onYesClicked()
            dismiss()
        }

        new_insurance_dialog_no_btn.setOnClickListener {
            dismiss()
        }
    }
}