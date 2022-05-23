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
import ua.myauto.domain.models.categories.ScreenCategory
import ua.myauto.ui.adapter.recycler.CategoriesRecyclerAdapter.CategoryVH
import ua.myauto.ui.extensions.drawable

class CategoriesRecyclerAdapter(
    private val onCategoryClick: (ScreenCategory) -> Unit
) : RecyclerView.Adapter<CategoryVH>() {

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.bind(ScreenCategory.values()[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryVH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_category_item,
            parent,
            false
        )
    )

    override fun getItemCount(): Int = ScreenCategory.values().size

    inner class CategoryVH(private val view: View) : ViewHolder(view) {

        fun bind(category: ScreenCategory) {
            val categoryName = view.context.getString(category.nameId)
            val categoryIcon = view.context.drawable(category.drawableId)

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