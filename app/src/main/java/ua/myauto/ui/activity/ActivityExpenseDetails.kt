package ua.myauto.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.sql.Time
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_no_history_tv
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_operations_rv
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_period_tv
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_sum_tv
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_toolbar_back_ib
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_toolbar_large_ll
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_toolbar_ll
import kotlinx.android.synthetic.main.activity_expenses_details.expenses_details_toolbar_title_tv
import ua.myauto.R
import ua.myauto.domain.models.expense.Expense
import ua.myauto.ui.activity.base.BaseActivity
import ua.myauto.ui.adapter.recycler.ExpensesRecyclerAdapter
import ua.myauto.ui.viewmodel.ExpenseDetailsVM
import ua.myauto.utils.TimeUtils

const val KEY_EXPENSE_CATEGORY_DETAILS = "category_details"

class ActivityExpenseDetails : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_expenses_details

    private lateinit var expensesAdapter: ExpensesRecyclerAdapter
    private val viewModel: ExpenseDetailsVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { viewModel.parseIntent(it) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun initViews() {
        expenses_details_toolbar_back_ib.setOnClickListener {
            onBackPressed()
        }

        with(expenses_details_operations_rv) {
            expensesAdapter = ExpensesRecyclerAdapter(viewModel::onDelete).also {
                adapter = it
            }
        }
    }

    override fun observeVM() {
        with(viewModel) {
            categoryNameLiveData.observe(lifecycleOwner) {
                expenses_details_toolbar_title_tv.text = getString(it)
            }

            periodLiveData.observe(lifecycleOwner) { period ->
                period?.let {
                    expenses_details_period_tv.text = getString(
                        R.string.expenses_short_period_placeholder,
                        TimeUtils.formatMillis(period.periodStart),
                        TimeUtils.formatMillis(period.periodEnd)
                    )
                }
            }

            sumLiveData.observe(lifecycleOwner) {
                expenses_details_sum_tv.text = getString(R.string.expenses_list_sum_placeholder, it)
            }

            backgroundLiveData.observe(lifecycleOwner) {
                val color = ContextCompat.getColor(this@ActivityExpenseDetails, it)
                expenses_details_toolbar_ll.setBackgroundColor(color)
                expenses_details_toolbar_large_ll.setBackgroundColor(color)
            }

            operationsLiveData.observe(lifecycleOwner) {
                if (it.isEmpty()) {
                    expenses_details_no_history_tv.isVisible = true
                    expenses_details_operations_rv.isVisible = false
                } else {
                    expenses_details_no_history_tv.isVisible = false
                    expenses_details_operations_rv.isVisible = true
                    expensesAdapter.expenses = mutableListOf<Expense>().apply {
                        addAll(it)
                    }
                }
            }
        }
    }
}