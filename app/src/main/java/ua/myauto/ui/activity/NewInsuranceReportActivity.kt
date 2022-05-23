package ua.myauto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_asked_organs_group
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_control_group
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_did_asked_organs_no_contract_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_did_asked_organs_no_protocol_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_did_asked_organs_yes_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_address_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_car_body_number_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_car_number_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_cars_amount_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_compensation_type_bank_account_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_compensation_type_fix_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_compensation_type_group
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_control_parked_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_crash_details_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_crash_input_ll
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_crash_place_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_damage_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_guilty_details_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_guilty_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_has_victims_yes_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_insurance_number_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_personal_email_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_personal_first_name_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_personal_last_name_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_personal_phone_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_place_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_place_ll
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_registration_place_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_registration_place_switch
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_risk_animals_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_risk_disaster_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_risk_group
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_risk_pdto_rb
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_send_btn
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_time_et
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_toolbar
import kotlinx.android.synthetic.main.activity_new_insurance_report.new_insurance_report_was_fixed_yes_rb
import ua.myauto.R
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.domain.models.report.ReportRiskType
import ua.myauto.domain.models.report.ReportRiskType.Animals
import ua.myauto.domain.models.report.ReportRiskType.Crash
import ua.myauto.domain.models.report.ReportRiskType.Disaster
import ua.myauto.domain.models.report.ReportRiskType.PDTO
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.numberInput
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.NewInsuranceReportVM
import ua.myauto.utils.DIALOG_TAG

class NewInsuranceReportActivity : ToolbarActivity(), InputFieldsPage {
    override val layoutId: Int get() = R.layout.activity_new_insurance_report
    override val toolbarTitleId: Int get() = R.string.new_insurance_report_title
    override val toolbarView: View get() = new_insurance_report_toolbar

    private val viewModel: NewInsuranceReportVM by viewModels()
    private val datePicker: MaterialDatePicker<Long>

    init {
        datePicker = createDatePicker()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { viewModel.parseIntent(intent) }
    }

    override fun initViews() {
        new_insurance_report_personal_first_name_et.addTextChangedListener(this, viewModel)
        new_insurance_report_personal_last_name_et.addTextChangedListener(this, viewModel)
        new_insurance_report_personal_phone_et.addTextChangedListener(this, viewModel)
        new_insurance_report_personal_email_et.addTextChangedListener(this, viewModel)
        new_insurance_report_registration_place_et.addTextChangedListener(this, viewModel)
        new_insurance_report_place_et.addTextChangedListener(this, viewModel)
        new_insurance_report_time_et.addTextChangedListener(this, viewModel)
        new_insurance_report_car_number_et.addTextChangedListener(this, viewModel)
        new_insurance_report_car_body_number_et.addTextChangedListener(this, viewModel)
        new_insurance_report_insurance_number_et.addTextChangedListener(this, viewModel)
        new_insurance_report_crash_place_et.addTextChangedListener(this, viewModel)
        new_insurance_report_crash_details_et.addTextChangedListener(this, viewModel)
        new_insurance_report_damage_et.addTextChangedListener(this, viewModel)
        new_insurance_report_address_et.addTextChangedListener(this, viewModel)
        new_insurance_report_cars_amount_et.addTextChangedListener(this, viewModel)
        new_insurance_report_guilty_et.addTextChangedListener(this, viewModel)
        new_insurance_report_guilty_details_et.addTextChangedListener(this, viewModel)

        new_insurance_report_risk_group.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onRiskTypeChanged(
                when (checkedId) {
                    new_insurance_report_risk_pdto_rb.id -> PDTO()
                    new_insurance_report_risk_animals_rb.id -> Animals()
                    new_insurance_report_risk_disaster_rb.id -> Disaster()

                    else -> ReportRiskType.default
                }
            )
        }

        new_insurance_report_time_et.setOnClickListener {
            viewModel.onDateClick()
        }

        new_insurance_report_registration_place_et.addTextChangedListener {
            if (new_insurance_report_registration_place_switch.isChecked) {
                new_insurance_report_place_et.text = it
            }
        }

        new_insurance_report_registration_place_switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAddressesEqualsSwitchChanged(isChecked)
        }

        new_insurance_report_send_btn.setOnClickListener {
            createReport()?.let {
                viewModel.onSendClick(it)
            }
        }
    }

    override fun observeVM() {
        with(viewModel) {
            firstNameLiveData.observe(lifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    new_insurance_report_personal_first_name_et.setText(it)
                }
            }

            emailLiveData.observe(lifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    new_insurance_report_personal_email_et.setText(it)
                }
            }

            lastNameLiveData.observe(lifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    new_insurance_report_personal_last_name_et.setText(it)
                }
            }

            insuranceLiveData.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                new_insurance_report_car_number_et.setText(insurance.carNumber)
                new_insurance_report_insurance_number_et.setText(
                    getString(
                        R.string.insurance_number_placeholder,
                        insurance.serial,
                        insurance.number
                    )
                )
            }

            isPlaceEqualsRegistrationLiveData.observe(lifecycleOwner) {
                new_insurance_report_place_ll.isVisible = !it
            }

            riskTypeLiveData.observe(lifecycleOwner) {
                new_insurance_report_crash_input_ll.isVisible = it is Crash
            }

            isDataInputCorrectLiveData.observe(lifecycleOwner) {
                new_insurance_report_send_btn.isUserClickable = it
            }

            dateLiveData.observe(lifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    new_insurance_report_time_et.setText(it)
                }
            }

            showDatePickerLiveData.observe(lifecycleOwner) { show ->
                if (show) {
                    datePicker.show(supportFragmentManager, DIALOG_TAG.plus("date"))
                }
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@NewInsuranceReportActivity)
                else
                    DialogLoading.dismiss()
            }

            onSendSuccessLiveData.observe(lifecycleOwner) { success ->
                if (success) finish()
            }

            onSendFailedLiveData.observe(lifecycleOwner) {
                if (it) toast(R.string.data_saved_locally)
            }
        }
    }

    override fun getInputFieldsData(): Array<String> = arrayOf(
        new_insurance_report_personal_first_name_et.input,
        new_insurance_report_personal_last_name_et.input,
        new_insurance_report_personal_phone_et.input,
        new_insurance_report_personal_email_et.input,
        new_insurance_report_registration_place_et.input,
        new_insurance_report_place_et.input,
        new_insurance_report_time_et.input,
        new_insurance_report_car_number_et.input,
        new_insurance_report_car_body_number_et.input,
        new_insurance_report_insurance_number_et.input,
        new_insurance_report_crash_place_et.input,
        new_insurance_report_crash_details_et.input,
        new_insurance_report_damage_et.input,
        new_insurance_report_address_et.input,
        new_insurance_report_cars_amount_et.input,
        new_insurance_report_guilty_et.input,
        new_insurance_report_guilty_details_et.input,
    )

    private fun createDatePicker(): MaterialDatePicker<Long> {
        val calendar = Calendar.getInstance()

        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .setOpenAt(calendar.timeInMillis)
            .setStart(calendar.timeInMillis)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setCalendarConstraints(calendarConstraints)
            .setSelection(calendar.timeInMillis)
            .build()

        with(datePicker) {
            addOnPositiveButtonClickListener {
                viewModel.onDateChanged(it)
            }
        }

        return datePicker
    }

    private fun createReport(): InsuranceReport? {
        val insuranceId = viewModel.insuranceLiveData.value?.id ?: return null
        val insuranceNumber = viewModel.insuranceLiveData.value?.number ?: return null
        val insuranceSerial = viewModel.insuranceLiveData.value?.serial ?: return null

        val fullName =
            "${new_insurance_report_personal_first_name_et.input} ${new_insurance_report_personal_last_name_et.input}"

        val liveAddress =
            if (new_insurance_report_place_ll.isVisible) new_insurance_report_place_et.input
            else new_insurance_report_registration_place_et.input

        val riskType = when {
            new_insurance_report_risk_pdto_rb.isChecked -> PDTO()
            new_insurance_report_risk_disaster_rb.isChecked -> Disaster()
            new_insurance_report_risk_animals_rb.isChecked -> Animals()

            else -> {
                Crash(
                    carsAmount = new_insurance_report_cars_amount_et.numberInput,
                    wasVictims = new_insurance_report_has_victims_yes_rb.isChecked,
                    guilty = new_insurance_report_guilty_et.input,
                    guiltyDetails = new_insurance_report_guilty_details_et.input,
                    wasFixed = new_insurance_report_was_fixed_yes_rb.isChecked,
                    compensation = getSelectedCompensation()
                )
            }
        }

        return InsuranceReport(
            insuranceId = insuranceId,
            insuranceNumber = insuranceNumber,
            insuranceSerial = insuranceSerial,
            issuedAt = System.currentTimeMillis(),
            fullName = fullName,
            phoneNumber = new_insurance_report_personal_phone_et.input,
            email = new_insurance_report_personal_email_et.input,
            registrationAddress = new_insurance_report_registration_place_et.input,
            liveAddress = liveAddress,
            reportRiskType = riskType,
            dateTime = new_insurance_report_time_et.input,
            carNumber = new_insurance_report_car_number_et.input,
            carBodyNumber = new_insurance_report_car_body_number_et.input,
            crashPlace = new_insurance_report_crash_place_et.input,
            crashDetails = new_insurance_report_crash_details_et.input,
            wasAskedOrgans = getAskedOrgans(),
            carControl = getCarControl(),
            damage = new_insurance_report_damage_et.input,
            address = new_insurance_report_address_et.input
        )
    }

    private fun getSelectedCompensation() =
        getString(
            when (new_insurance_report_compensation_type_group.checkedRadioButtonId) {
                new_insurance_report_compensation_type_fix_rb.id -> R.string.new_insurance_report_compensation_type_fix
                new_insurance_report_compensation_type_bank_account_rb.id -> R.string.new_insurance_report_compensation_type_bank_account
                else -> R.string.new_insurance_report_compensation_type_other
            }
        )

    private fun getAskedOrgans() =
        getString(
            when (new_insurance_asked_organs_group.checkedRadioButtonId) {
                new_insurance_did_asked_organs_yes_rb.id -> R.string.yes
                new_insurance_did_asked_organs_no_protocol_rb.id -> R.string.new_insurance_report_did_asked_no_protocol
                new_insurance_did_asked_organs_no_contract_rb.id -> R.string.new_insurance_report_did_asked_no_contract
                else -> R.string.new_insurance_report_did_asked_no_glass
            }
        )

    private fun getCarControl() =
        getString(
            when (new_insurance_control_group.checkedRadioButtonId) {
                new_insurance_report_control_parked_rb.id -> R.string.new_insurance_report_control_parked
                else -> R.string.new_insurance_report_control_person
            }
        )
}