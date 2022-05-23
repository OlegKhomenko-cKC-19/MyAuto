package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.layout_insurance_list_item.view.insurance_item_number_tv
import kotlinx.android.synthetic.main.layout_insurance_list_item.view.insurance_item_open_ib
import kotlinx.android.synthetic.main.layout_insurance_list_item.view.insurance_item_type_background_cv
import kotlinx.android.synthetic.main.layout_insurance_list_item.view.insurance_item_type_background_iv
import kotlinx.android.synthetic.main.layout_insurance_list_item.view.insurance_item_type_tv
import ua.myauto.R
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.adapter.recycler.InsurancesListRecyclerAdapter.InsuranceVH
import ua.myauto.ui.extensions.drawable

class InsurancesListRecyclerAdapter(
    private val onInsuranceClick: (Insurance) -> Unit
) : RecyclerView.Adapter<InsuranceVH>() {

    var insurances: List<Insurance> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = insurances.size

    override fun onBindViewHolder(holder: InsuranceVH, position: Int) {
        holder.bind(insurances[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_insurance_list_item, parent, false)

        return InsuranceVH(view)
    }

    inner class InsuranceVH(private val view: View) : ViewHolder(view) {

        fun bind(insurance: Insurance) {
            val background = view.context.drawable(insurance.type.backgroundId)
            val typeName = view.context.getString(insurance.type.shortNameId)
            val serialNumber = view.context.getString(
                R.string.insurance_number_placeholder,
                insurance.serial,
                insurance.number
            )

            with(view) {
                insurance_item_type_tv.text = typeName
                insurance_item_number_tv.text = serialNumber
                insurance_item_type_background_iv.setImageDrawable(background)

                view.setOnClickListener {
                    onInsuranceClick(insurance)
                }

                insurance_item_open_ib.setOnClickListener {
                    onInsuranceClick(insurance)
                }
            }
        }
    }
}