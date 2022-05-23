package ua.myauto.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.android.synthetic.main.activity_main.main_categories_rv
import kotlinx.android.synthetic.main.activity_main.main_insurance_rv
import kotlinx.android.synthetic.main.activity_main.main_insurances_tv
import kotlinx.android.synthetic.main.activity_main.main_toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sargue.mailgun.Configuration
import net.sargue.mailgun.Mail
import ua.myauto.R
import ua.myauto.domain.models.categories.ScreenCategory.EXPENSES
import ua.myauto.domain.models.categories.ScreenCategory.FINALIZATION
import ua.myauto.domain.models.categories.ScreenCategory.FINES
import ua.myauto.domain.models.categories.ScreenCategory.RULES
import ua.myauto.domain.models.categories.ScreenCategory.SUPPORT
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.adapter.recycler.CategoriesRecyclerAdapter
import ua.myauto.ui.adapter.recycler.InsurancesCardsRecyclerAdapter
import ua.myauto.ui.viewmodel.InsurancesVM
import ua.myauto.ui.viewmodel.MainVM
import ua.myauto.utils.KEY_INSURANCE_DETAILS


class MainActivity : ToolbarActivity(isArrowBackVisible = false) {
    override val layoutId: Int get() = R.layout.activity_main
    override val toolbarTitleId: Int get() = R.string.app_name
    override val toolbarView: View get() = main_toolbar

    private val viewModel: MainVM by viewModels()
    private val insurancesViewModel: InsurancesVM by viewModels()
    private lateinit var insurancesCardsRecyclerAdapter: InsurancesCardsRecyclerAdapter
    private lateinit var categoriesRecyclerAdapter: CategoriesRecyclerAdapter

    override fun initViews() {
        main_insurances_tv.setOnClickListener {
            viewModel.onMyInsurancesClick()
        }

        with(main_categories_rv) {
            categoriesRecyclerAdapter = CategoriesRecyclerAdapter(
                viewModel::onScreenCategoryClick
            ).also {
                adapter = it
            }
        }

        with(main_insurance_rv) {
            PagerSnapHelper().attachToRecyclerView(this)
            insurancesCardsRecyclerAdapter = InsurancesCardsRecyclerAdapter(
                insurancesViewModel::onInsuranceClick,
                insurancesViewModel::onAddInsuranceClick
            ).also {
                adapter = it
            }
        }
    }

    override fun observeVM() {
        with(viewModel) {
            openInsurancesScreen.observe(lifecycleOwner) { navigate ->
                if (navigate)
                    startActivity(Intent(this@MainActivity, InsuranceListActivity::class.java))
            }

            openScreenCategory.observe(lifecycleOwner) { category ->
                category ?: return@observe
                startActivity(
                    Intent(
                        this@MainActivity, when (category) {
                            FINES -> {
                                ActivityFines::class.java
                            }
                            RULES -> {
                                NewsActivity::class.java
                            }
                            EXPENSES -> {
                                ActivityExpenses::class.java
                            }
                            FINALIZATION -> {
                                InsuranceHelpActivity::class.java
                            }
                            SUPPORT -> {
                                SupportActivity::class.java
                            }
                        }
                    )
                )
            }
        }

        with(insurancesViewModel) {
            insurances.observe(lifecycleOwner) { insurances ->
                insurancesCardsRecyclerAdapter.insurances = insurances
            }

            openNewInsuranceScreen.observe(lifecycleOwner) { navigate ->
                if (navigate)
                    startActivity(Intent(this@MainActivity, NewInsuranceActivity::class.java))
            }

            openInsuranceDetails.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                startActivity(
                    Intent(this@MainActivity, InsuranceDetailsActivity::class.java).apply {
                        putExtras(bundleOf(KEY_INSURANCE_DETAILS to insurance))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        insurancesViewModel.onResume()
    }
}