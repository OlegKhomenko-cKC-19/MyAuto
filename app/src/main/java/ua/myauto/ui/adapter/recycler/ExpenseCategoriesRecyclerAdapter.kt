package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.layout_category_item.view.category_item_iv
import kotlinx.android.synthetic.main.layout_category_item.view.category_name_tv
import ua.myauto.R
import ua.myauto.domain.models.expense.ExpenseCategory
import ua.myauto.ui.adapter.recycler.ExpenseCategoriesRecyclerAdapter.ExpenseCategoryVH
import ua.myauto.ui.extensions.drawable

class ExpenseCategoriesRecyclerAdapter(
    private val onCategoryClick: (ExpenseCategory) -> Unit
) : RecyclerView.Adapter<ExpenseCategoryVH>() {

    override fun onBindViewHolder(holder: ExpenseCategoryVH, position: Int) {
        holder.bind(ExpenseCategory.values()[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExpenseCategoryVH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_category_item,
            parent,
            false
        )
    )

    override fun getItemCount(): Int = ExpenseCategory.values().size

    inner class ExpenseCategoryVH(private val view: View) : ViewHolder(view) {

        fun bind(category: ExpenseCategory) {
            val categoryName = view.context.getString(category.nameId)
            val categoryIcon = view.context.drawable(category.iconId)

            with(view) {
                category_name_tv.text = categoryName
                category_item_iv.setImageDrawable(categoryIcon)

                view.allViews.forEach { currentView ->
                    currentView.setOnClickListener {
                        onCategoryClick(category)
                    }
                }
            }
        }
    }
}