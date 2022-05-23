package ua.myauto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_insurance_details.details_car_number_tv
import kotlinx.android.synthetic.main.activity_insurance_details.details_container_cl
import kotlinx.android.synthetic.main.activity_insurance_details.details_expired_at_tv
import kotlinx.android.synthetic.main.activity_insurance_details.details_history_add_fab
import kotlinx.android.synthetic.main.activity_insurance_details.details_history_rv
import kotlinx.android.synthetic.main.activity_insurance_details.details_insurance_number_tv
import kotlinx.android.synthetic.main.activity_insurance_details.details_issued_at_tv
import kotlinx.android.synthetic.main.activity_insurance_details.details_no_history_tv
import kotlinx.android.synthetic.main.activity_insurance_details.details_payment_required_ll
import kotlinx.android.synthetic.main.activity_insurance_details.details_price_tv
import kotlinx.android.synthetic.main.activity_insurance_details.details_type_tv
import kotlinx.android.synthetic.main.activity_insurance_details.insurance_details_toolbar
import kotlinx.android.synthetic.main.activity_insurance_details.pay_btn
import ua.myauto.R
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.adapter.recycler.InsuranceReportRecyclerAdapter
import ua.myauto.ui.extensions.drawable
import ua.myauto.ui.viewmodel.InsuranceDetailsVM
import ua.myauto.utils.KEY_INSURANCE_DETAILS
import ua.myauto.utils.KEY_INSURANCE_REPORT

class InsuranceDetailsActivity : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_insurance_details
    override val toolbarTitleId: Int get() = R.string.insurance_details_title
    override val toolbarView: View get() = insurance_details_toolbar

    private val viewModel: InsuranceDetailsVM by viewModels()
    private lateinit var reportsAdapter: InsuranceReportRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { viewModel.parseIntent(intent) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun initViews() {
        with(details_history_rv) {
            reportsAdapter = InsuranceReportRecyclerAdapter(viewModel::onReportClick).also {
                adapter = it
            }
        }

        details_history_add_fab.setOnClickListener {
            viewModel.onNewReportClick()
        }

        pay_btn.setOnClickListener {
            viewModel.onPayClick()
        }
    }

    override fun observeVM() {
        with(viewModel) {
            insuranceReports.observe(lifecycleOwner) { reports ->
                val areNoReports = reports.isNullOrEmpty()
                details_history_rv.isVisible = !areNoReports
                details_no_history_tv.isVisible = areNoReports

                if (!areNoReports) {
                    reportsAdapter.reports = reports
                }
            }

            currentInsurance.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                fillDetails(insurance)
            }

            openNewReport.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                startActivity(
                    Intent(
                        this@InsuranceDetailsActivity,
                        NewInsuranceReportActivity::class.java
                    ).apply {
                        putExtras(bundleOf(KEY_INSURANCE_DETAILS to insurance))
                    }
                )

                finish()
            }

            paymentRequired.observe(lifecycleOwner) { isPaymentRequired ->
                details_payment_required_ll.isVisible = isPaymentRequired
            }

            payInsurance.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                startActivity(
                    Intent(
                        this@InsuranceDetailsActivity,
                        PaymentAnimationActivity::class.java
                    ).apply {
                        putExtras(bundleOf(KEY_INSURANCE_DETAILS to insurance))
                    }
                )

                finish()
            }

            reportDetails.observe(lifecycleOwner) { report ->
                report ?: return@observe
                startActivity(
                    Intent(
                        this@InsuranceDetailsActivity,
                        InsuranceReportDetailsActivity::class.java
                    ).apply {
                        putExtras(bundleOf(KEY_INSURANCE_REPORT to report))
                    }
                )
            }
        }
    }

    private fun fillDetails(insurance: Insurance) {
        with(insurance) {
            details_type_tv.text = getString(type.shortNameId)
            details_container_cl.background = drawable(type.backgroundId)

            details_car_number_tv.text = carNumber
            details_issued_at_tv.text = getString(
                R.string.insurance_issued_at,
                formattedIssueDate
            )
            details_expired_at_tv.text = getString(
                R.string.insurance_expired_at,
                formattedExpireDate
            )
            details_price_tv.text = getString(
                R.string.insurance_price_placeholder,
                price
            )
            details_insurance_number_tv.text = getString(
                R.string.insurance_number_placeholder,
                serial,
                number
            )
        }
    }
}