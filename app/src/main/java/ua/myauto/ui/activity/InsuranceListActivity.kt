package ua.myauto.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.activity_insurances.insurances_add_fab
import kotlinx.android.synthetic.main.activity_insurances.insurances_rv
import kotlinx.android.synthetic.main.activity_insurances.insurances_toolbar
import ua.myauto.R
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.adapter.recycler.InsurancesListRecyclerAdapter
import ua.myauto.ui.viewmodel.InsurancesVM
import ua.myauto.utils.KEY_INSURANCE_DETAILS

class InsuranceListActivity : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_insurances
    override val toolbarTitleId: Int get() = R.string.insurances_title
    override val toolbarView: View get() = insurances_toolbar

    private val viewModel: InsurancesVM by viewModels()
    private lateinit var insuranceListAdapter: InsurancesListRecyclerAdapter

    override fun initViews() {
        with(insurances_rv) {
            insuranceListAdapter = InsurancesListRecyclerAdapter(
                viewModel::onInsuranceClick
            ).also {
                adapter = it
            }
        }

        insurances_add_fab.setOnClickListener {
            viewModel.onAddInsuranceClick()
        }
    }

    override fun observeVM() {
        with(viewModel) {
            insurances.observe(lifecycleOwner) { insurances ->
                insuranceListAdapter.insurances = insurances
            }

            openNewInsuranceScreen.observe(lifecycleOwner) { navigate ->
                if (navigate) {
                    startActivity(
                        Intent(
                            this@InsuranceListActivity,
                            NewInsuranceActivity::class.java
                        )
                    )
                }
            }

            openInsuranceDetails.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                startActivity(
                    Intent(this@InsuranceListActivity, InsuranceDetailsActivity::class.java).apply {
                        putExtras(bundleOf(KEY_INSURANCE_DETAILS to insurance))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}