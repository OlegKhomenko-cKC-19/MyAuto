package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_operation_details_item.view.operation_category_icon_iv
import kotlinx.android.synthetic.main.layout_operation_details_item.view.operation_item_category_name_tv
import kotlinx.android.synthetic.main.layout_operation_details_item.view.operation_item_category_sum_tv
import kotlinx.android.synthetic.main.layout_operation_details_item.view.operation_item_date_tv
import kotlinx.android.synthetic.main.layout_operation_details_item.view.operation_item_delete_ib
import kotlinx.android.synthetic.main.layout_operation_details_item.view.operation_item_details_tv
import ua.myauto.R
import ua.myauto.domain.models.expense.Expense
import ua.myauto.ui.adapter.recycler.ExpensesRecyclerAdapter.ExpenseVH
import ua.myauto.ui.extensions.drawable
import ua.myauto.utils.TimeUtils

class ExpensesRecyclerAdapter(
    private val onItemDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseVH>() {

    var expenses = mutableListOf<Expense>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = expenses.size

    override fun onBindViewHolder(holder: ExpenseVH, position: Int) {
        holder.bind(expenses[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExpenseVH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_operation_details_item,
            parent,
            false
        )
    )

    inner class ExpenseVH(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(expense: Expense) {
            val context = view.context
            val category = expense.category
            with(view) {
                operation_category_icon_iv.setImageDrawable(context.drawable(category.iconId))
                operation_item_date_tv.text = TimeUtils.formatMillis(
                    expense.issuedAt,
                    shortFormat = true
                )
                operation_item_category_name_tv.text = context.getString(category.nameId)
                operation_item_details_tv.text = expense.comment
                operation_item_category_sum_tv.text = context.getString(
                    R.string.expenses_list_sum_placeholder,
                    expense.sum
                )

                operation_item_delete_ib.setOnClickListener {
                    expenses.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    onItemDelete(expense)
                }
            }
        }
    }
}