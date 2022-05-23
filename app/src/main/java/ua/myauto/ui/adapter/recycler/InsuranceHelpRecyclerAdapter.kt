package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.layout_insurance_help_item.view.insurance_help_item_expand_ib
import kotlinx.android.synthetic.main.layout_insurance_help_item.view.insurance_help_item_expanded_ll
import kotlinx.android.synthetic.main.layout_insurance_help_item.view.insurance_help_item_expanded_tv
import kotlinx.android.synthetic.main.layout_insurance_help_item.view.insurance_help_item_title_tv
import ua.myauto.R
import ua.myauto.domain.models.insurance.InsuranceHelpItem
import ua.myauto.ui.adapter.recycler.InsuranceHelpRecyclerAdapter.InsuranceHelpVH


class InsuranceHelpRecyclerAdapter : RecyclerView.Adapter<InsuranceHelpVH>() {

    var items: List<InsuranceHelpItem> = listOf()
        set(value) {
            field = value
            notifyItemRangeChanged(0, items.lastIndex)
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InsuranceHelpVH, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InsuranceHelpVH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_insurance_help_item,
            parent,
            false
        )
    )

    inner class InsuranceHelpVH(private val view: View) : ViewHolder(view) {

        fun bind(item: InsuranceHelpItem) {
            with(view) {
                insurance_help_item_title_tv.text = item.title
                insurance_help_item_expanded_tv.text = item.details

                if (!item.isExpanded) {
                    insurance_help_item_expand_ib.rotation = 180f
                }

                insurance_help_item_expanded_ll.isVisible = item.isExpanded
                insurance_help_item_expand_ib.setOnClickListener {
                    item.isExpanded = !item.isExpanded
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }
}