package ua.myauto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_insurance_report_details.insurance_report_toolbar
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_car_address_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_car_body_number_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_car_number_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_compensation_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_control_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_crash_ll
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_crash_people_amount_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_damage_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_details_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_guilty_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_has_victims_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_insurance_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_number_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_organs_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_person_email_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_person_full_name_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_person_phone_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_person_registration_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_place_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_time_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_type_tv
import kotlinx.android.synthetic.main.activity_insurance_report_details.report_was_fixed_tv
import ua.myauto.R
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.domain.models.report.ReportRiskType.Crash
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.viewmodel.InsuranceReportDetailsVM

class InsuranceReportDetailsActivity : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_insurance_report_details
    override val toolbarTitleId: Int get() = R.string.insurance_report_details_title
    override val toolbarView: View get() = insurance_report_toolbar

    private val viewModel: InsuranceReportDetailsVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { viewModel.parseIntent(intent) }
    }

    override fun initViews() {}

    override fun observeVM() {
        with(viewModel) {
            currentInsuranceReport.observe(lifecycleOwner) { report ->
                report?.fillViews()
            }
        }
    }

    private fun InsuranceReport.fillViews() {
        report_person_full_name_tv.text = fullName
        report_person_phone_tv.text = phoneNumber
        report_person_email_tv.text = email
        report_type_tv.text = getString(reportRiskType.titleId)
        report_number_tv.text = getString(R.string.insurance_report_number, number)
        report_person_registration_tv.text = getString(
            R.string.insurance_report_details_places,
            registrationAddress,
            liveAddress
        )

        report_car_number_tv.text = carNumber
        report_car_body_number_tv.text = carBodyNumber
        report_control_tv.text = carControl
        report_insurance_tv.text = getString(
            R.string.insurance_number_placeholder,
            insuranceSerial,
            insuranceNumber
        )

        report_place_tv.text = crashPlace
        report_time_tv.text = dateTime
        report_details_tv.text = crashDetails
        report_damage_tv.text = damage
        report_car_address_tv.text = address
        report_organs_tv.text = wasAskedOrgans

        val isCrash = reportRiskType is Crash
        report_crash_ll.isVisible = isCrash
        if (isCrash) {
            val crash = reportRiskType as Crash
            report_crash_people_amount_tv.text = crash.carsAmount.toString()
            report_was_fixed_tv.text = crash.wasFixed.getString()
            report_has_victims_tv.text = crash.wasVictims.getString()
            report_compensation_tv.text = crash.compensation
            report_guilty_tv.text = getString(
                R.string.insurance_report_guilty_placeholders,
                crash.guilty,
                crash.guiltyDetails
            )
        }
    }

    private fun Boolean.getString() = getString(
        if (this) R.string.yes
        else R.string.no
    )
}