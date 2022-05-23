package ua.myauto.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_insurance_help.insurance_help_rv
import kotlinx.android.synthetic.main.activity_insurance_help.insurance_help_toolbar
import ua.myauto.R
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.adapter.recycler.InsuranceHelpRecyclerAdapter
import ua.myauto.ui.viewmodel.InsuranceHelpVM

class InsuranceHelpActivity : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_insurance_help
    override val toolbarTitleId: Int get() = R.string.insurance_help_title
    override val toolbarView: View get() = insurance_help_toolbar

    private val viewModel: InsuranceHelpVM by viewModels()
    private lateinit var insuranceHelpItemsAdapter: InsuranceHelpRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(resources)
    }

    override fun initViews() {
        with(insurance_help_rv) {
            insuranceHelpItemsAdapter = InsuranceHelpRecyclerAdapter().also {
                adapter = it
            }
        }
    }

    override fun observeVM() {
        viewModel.itemsLiveData.observe(lifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                insuranceHelpItemsAdapter.items = it
            }
        }
    }
}