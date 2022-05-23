package ua.myauto.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_new_expense.new_expense_categories_rv
import kotlinx.android.synthetic.main.activity_new_expense.new_expense_toolbar
import kotlinx.android.synthetic.main.bottom_sheet_new_expense.view.bs_new_expense_btn
import kotlinx.android.synthetic.main.bottom_sheet_new_expense.view.bs_new_expense_category_iv
import kotlinx.android.synthetic.main.bottom_sheet_new_expense.view.bs_new_expense_category_name_tv
import kotlinx.android.synthetic.main.bottom_sheet_new_expense.view.bs_new_expense_note_et
import kotlinx.android.synthetic.main.bottom_sheet_new_expense.view.bs_new_expense_sum_et
import ua.myauto.R
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.adapter.recycler.ExpenseCategoriesRecyclerAdapter
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.extensions.drawable
import ua.myauto.ui.extensions.floatInput
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.NewExpenseVM

class ActivityNewExpense : ToolbarActivity(), InputFieldsPage {
    override val toolbarTitleId: Int
        get() = R.string.expenses_new_input_title
    override val toolbarView: View
        get() = new_expense_toolbar
    override val layoutId: Int
        get() = R.layout.activity_new_expense

    private var bottomSheetLayout: View? = null
    private var categoryOfBottomSheet: ExpenseCategory? = null

    private val viewModel: NewExpenseVM by viewModels()
    private lateinit var expenseCategoriesAdapter: ExpenseCategoriesRecyclerAdapter

    override fun onBackPressed() {
        startActivity(Intent(this, ActivityExpenses::class.java))
        finish()
    }

    override fun initViews() {
        with(new_expense_categories_rv) {
            expenseCategoriesAdapter = ExpenseCategoriesRecyclerAdapter(
                viewModel::onCategoryClick
            ).also {
                adapter = it
            }
        }
    }

    override fun observeVM() {
        with(viewModel) {
            showBottomSheetWithCategoryLiveData.observe(lifecycleOwner) { category ->
                category?.let {
                    showBottomSheetFor(it)
                }
            }

            isDataInputCorrectLiveData.observe(lifecycleOwner) { isInputCorrect ->
                if (bottomSheetLayout != null) {
                    if (!isInputCorrect) {
                        toast(R.string.failure_msg)
                    } else {
                        onInputValidated()
                    }
                }
            }

            finishActivityLiveData.observe(lifecycleOwner) {
                if (it) {
                    onBackPressed()
                }
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@ActivityNewExpense)
                else
                    DialogLoading.dismiss()
            }

            onCreateFailedLiveData.observe(lifecycleOwner) {
                if (it) toast(R.string.data_saved_locally)
            }
        }
    }

    override fun getInputFieldsData(): Array<String> {
        val fields = mutableListOf<String>()

        bottomSheetLayout?.let {
            fields.add(it.bs_new_expense_sum_et.input)
            fields.add(it.bs_new_expense_note_et.input)
        }

        return fields.toTypedArray()
    }

    private fun showBottomSheetFor(category: ExpenseCategory) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_new_expense, null).apply {
            bs_new_expense_category_iv.setImageDrawable(drawable(category.iconId))
            bs_new_expense_category_name_tv.text = getString(category.nameId)
            bs_new_expense_btn.setOnClickListener {
                viewModel.onDataChanged(getInputFieldsData())
            }
        }
        categoryOfBottomSheet = category
        bottomSheetLayout = view

        dialog.setContentView(view)
        dialog.show()
    }

    private fun onInputValidated() {
        val bsView = bottomSheetLayout ?: return
        val category = categoryOfBottomSheet ?: return

        viewModel.onCreateExpenseClick(
            category,
            bsView.bs_new_expense_sum_et.floatInput,
            bsView.bs_new_expense_note_et.input
        )
    }
}