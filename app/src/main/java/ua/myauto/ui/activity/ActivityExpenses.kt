package ua.myauto.ui.activity

import android.content.Intent
import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_expenses.expenses_add_fab
import kotlinx.android.synthetic.main.activity_expenses.expenses_center_button
import kotlinx.android.synthetic.main.activity_expenses.expenses_chart
import kotlinx.android.synthetic.main.activity_expenses.expenses_no_history_iv
import kotlinx.android.synthetic.main.activity_expenses.expenses_no_history_tv
import kotlinx.android.synthetic.main.activity_expenses.expenses_operation_categories_rv
import kotlinx.android.synthetic.main.activity_expenses.expenses_period_ib
import kotlinx.android.synthetic.main.activity_expenses.expenses_period_tv
import kotlinx.android.synthetic.main.activity_expenses.expenses_progress
import kotlinx.android.synthetic.main.activity_expenses.expenses_toolbar
import ua.myauto.R
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.domain.models.expense.ExpenseCategory.ENGINE_REPAIR
import ua.myauto.domain.models.expense.statistics.ExpenseCategoryStatisticsItem
import ua.myauto.domain.models.expense.statistics.ExpenseStatistics
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.adapter.recycler.ExpensesCategoriesDetailsRecyclerAdapter
import ua.myauto.ui.dialog.DialogSelectPeriod
import ua.myauto.ui.viewmodel.ExpensesVM
import ua.myauto.utils.DIALOG_TAG
import ua.myauto.utils.TimeUtils

private const val PIE_CHART_ANIM_MILLIS = 1500
private const val PIE_TRANSPARENT_CIRCLE_RADIUS = 65f

class ActivityExpenses : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_expenses
    override val toolbarTitleId: Int get() = R.string.expenses_title
    override val toolbarView: View get() = expenses_toolbar
    private val viewModel: ExpensesVM by viewModels()

    private var selectedCategory: ExpenseCategoryStatisticsItem? = null
    private lateinit var expensesCategoriesDetailsAdapter: ExpensesCategoriesDetailsRecyclerAdapter

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun initViews() {
        with(expenses_operation_categories_rv) {
            expensesCategoriesDetailsAdapter = ExpensesCategoriesDetailsRecyclerAdapter(
                viewModel::onCategoryDetailsClick
            ).also {
                adapter = it
            }
        }

        with(expenses_chart) {
            isDrawHoleEnabled = true
            isRotationEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            holeRadius = PIE_TRANSPARENT_CIRCLE_RADIUS
            transparentCircleRadius = PIE_TRANSPARENT_CIRCLE_RADIUS
            setCenterTextSize(15f)
            setTouchEnabled(true)
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            setHoleColor(ContextCompat.getColor(this@ActivityExpenses, R.color.white))
        }

        expenses_period_ib.setOnClickListener {
            viewModel.onCalendarClicked()
        }

        expenses_add_fab.setOnClickListener {
            viewModel.onNewExpenseClick()
        }
    }

    override fun observeVM() {
        with(viewModel) {
            statisticsLiveData.observe(lifecycleOwner) { statistics ->
                if (statistics == null) {
                    showFailure()
                    return@observe
                }

                if (statistics.operations.isEmpty()) {
                    showFailure()
                    return@observe
                }

                fillStatistics(statistics)
            }

            periodLiveData.observe(lifecycleOwner) { period ->
                expenses_period_tv.text = getString(
                    R.string.expenses_period_placeholder,
                    TimeUtils.formatMillis(period.periodStart),
                    TimeUtils.formatMillis(period.periodEnd)
                )
            }

            showProgressLiveData.observe(lifecycleOwner) {
                expenses_progress.isVisible = it
            }

            showPeriodsDialogLiveData.observe(lifecycleOwner) { period ->
                period?.let {
                    DialogSelectPeriod().apply {
                        selectedPeriod = it
                        onPeriodSelected = viewModel::onPeriodSelected
                    }.show(
                        supportFragmentManager,
                        DIALOG_TAG
                    )
                }
            }

            navigateNewExpenseLiveData.observe(lifecycleOwner) {
                if (it) {
                    startActivity(Intent(this@ActivityExpenses, ActivityNewExpense::class.java))
                    finish()
                }
            }

            navigateCategoryDetailsLiveData.observe(lifecycleOwner) { categoryItem ->
                categoryItem?.let {
                    startActivity(
                        Intent(this@ActivityExpenses, ActivityExpenseDetails::class.java).putExtras(
                            bundleOf(KEY_EXPENSE_CATEGORY_DETAILS to it)
                        )
                    )
                }
            }
        }
    }

    private fun fillStatistics(statistics: ExpenseStatistics) {
        val categoryStatistics = statistics.categoryStatistics

        //fill pie chart
        val categoryColors = categoryStatistics.map {
            ContextCompat.getColor(this, it.category.colorId)
        }
        val pieEntries = categoryStatistics.map {
            PieEntry(it.percent, getString(it.category.nameId))
        }
        val dataSet = PieDataSet(pieEntries, "").apply {
            sliceSpace = 3f
            colors = categoryColors
        }
        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setDrawValues(false)
        }

        with(expenses_chart) {
            data = pieData
            highlightValues(null)
            onTouchListener.setLastHighlighted(null)
            centerText = getGeneralCenterText(statistics)
            animateY(PIE_CHART_ANIM_MILLIS, Easing.EaseInOutQuad)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onNothingSelected() {
                    centerText = getGeneralCenterText(statistics)
                    expenses_center_button.isVisible = false
                    expenses_center_button.isEnabled = false
                    selectedCategory = null
                }

                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val data = e as? PieEntry ?: return

                    val category = ExpenseCategory.values().find {
                        getString(it.nameId) == data.label
                    } ?: return

                    val dataByCategory = categoryStatistics.find {
                        it.category == category
                    } ?: return

                    centerText = getCategoryCenterText(dataByCategory)
                    selectedCategory = dataByCategory
                    expenses_center_button.isVisible = true
                    expenses_center_button.isEnabled = true
                }
            })
        }

        expenses_center_button.setOnClickListener {
            selectedCategory?.let { category ->
                viewModel.onCategoryDetailsClick(category)
            }
        }

        //fill recycler
        expenses_operation_categories_rv.isVisible = true
        expenses_chart.isInvisible = false
        expenses_no_history_tv.isVisible = false
        expenses_no_history_iv.isVisible = false
        expensesCategoriesDetailsAdapter.statisticsItems = categoryStatistics
    }

    private fun getGeneralCenterText(statistics: ExpenseStatistics): SpannableString {
        val text = getString(
            R.string.expenses_center_general_placeholder,
            statistics.fullSum
        )

        val smallTextIndex = text.indexOf("\n")

        val spannable = SpannableString(text).apply {
            toSmallText(0, smallTextIndex)
            toBigText(smallTextIndex, text.length)
        }

        return spannable
    }

    private fun getCategoryCenterText(categoryStatistics: ExpenseCategoryStatisticsItem): SpannableString {
        val text = getString(
            R.string.expenses_center_category_placeholder,
            getString(categoryStatistics.category.nameId),
            categoryStatistics.categorySum
        )
        val smallTextIndex = when (categoryStatistics.category) {
            ExpenseCategory.GEAR_REPAIR, ENGINE_REPAIR -> text.indexOf("\n", startIndex = text.indexOf("\n") + 1)
            else -> text.indexOf("\n")
        }

        val spannable = SpannableString(text).apply {
            toSmallText(0, smallTextIndex)
            toBigText(smallTextIndex, length)
        }

        return spannable
    }

    private fun SpannableString.toSmallText(from: Int, to: Int) {
        setSpan(RelativeSizeSpan(1f), from, to, 0)
        setSpan(StyleSpan(BOLD), from, to, 0)
        setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this@ActivityExpenses,
                    R.color.dark_gray_violet
                )
            ), from, to, 0
        )
    }

    private fun SpannableString.toBigText(from: Int, to: Int) {
        setSpan(RelativeSizeSpan(1.5f), from, to, 0)
        setSpan(LeadingMarginSpan.Standard(8), from, to, 0)
        setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this@ActivityExpenses,
                    R.color.dark_violet
                )
            ), from, to, 0
        )
    }

    private fun showFailure() {
        expenses_operation_categories_rv.isVisible = false
        expenses_chart.isInvisible = true
        expenses_no_history_tv.isVisible = true
        expenses_no_history_iv.isVisible = true
    }
}