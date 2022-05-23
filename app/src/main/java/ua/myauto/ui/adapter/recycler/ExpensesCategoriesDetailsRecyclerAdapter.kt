package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.layout_operation_category_item.view.operation_category_item_amount_tv
import kotlinx.android.synthetic.main.layout_operation_category_item.view.operation_category_item_category_name_tv
import kotlinx.android.synthetic.main.layout_operation_category_item.view.operation_category_item_category_sum_tv
import kotlinx.android.synthetic.main.layout_operation_category_item.view.operation_category_item_percent_tv
import ua.myauto.R
import ua.myauto.domain.models.expense.statistics.ExpenseCategoryStatisticsItem
import ua.myauto.ui.adapter.recycler.ExpensesCategoriesDetailsRecyclerAdapter.ExpensesCategoryVH

class ExpensesCategoriesDetailsRecyclerAdapter(
    private val onCategoryClick: (ExpenseCategoryStatisticsItem) -> Unit
) : RecyclerView.Adapter<ExpensesCategoryVH>() {

    var statisticsItems = listOf<ExpenseCategoryStatisticsItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = statisticsItems.size

    override fun onBindViewHolder(holder: ExpensesCategoryVH, position: Int) {
        holder.bind(statisticsItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExpensesCategoryVH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_operation_category_item,
            parent,
            false
        )
    )

    inner class ExpensesCategoryVH(private val view: View) : ViewHolder(view) {

        fun bind(categoryStatisticsItem: ExpenseCategoryStatisticsItem) {
            val context = view.context
            with(view) {
                operation_category_item_percent_tv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        categoryStatisticsItem.category.colorId
                    )
                )

                operation_category_item_percent_tv.text = context.getString(
                    R.string.expenses_percent_placeholder,
                    categoryStatisticsItem.percent
                )

                operation_category_item_category_name_tv.text = context.getString(
                    categoryStatisticsItem.category.nameId
                )

                operation_category_item_amount_tv.text = context.getString(
                    R.string.expenses_operations_amount_placeholder,
                    categoryStatisticsItem.amount
                )

                operation_category_item_category_sum_tv.text = context.getString(
                    R.string.expenses_list_sum_placeholder,
                    categoryStatisticsItem.categorySum
                )

                allViews.forEach { view ->
                    view.setOnClickListener {
                        onCategoryClick(categoryStatisticsItem)
                    }
                }
            }
        }
    }
}