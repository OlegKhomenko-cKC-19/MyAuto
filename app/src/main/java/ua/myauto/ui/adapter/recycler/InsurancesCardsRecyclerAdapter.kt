package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.allViews
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.layout_card_insurance_item.view.insurance_item_background_cl
import kotlinx.android.synthetic.main.layout_card_insurance_item.view.insurance_item_car_number_tv
import kotlinx.android.synthetic.main.layout_card_insurance_item.view.insurance_item_expired_at_tv
import kotlinx.android.synthetic.main.layout_card_insurance_item.view.insurance_item_number_tv
import kotlinx.android.synthetic.main.layout_card_insurance_item.view.insurance_item_type_tv
import ua.myauto.R
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.ui.adapter.model.InsuranceAdapterType.TYPE_INSURANCE
import ua.myauto.ui.adapter.model.InsuranceAdapterType.TYPE_NEW_INSURANCE
import ua.myauto.ui.extensions.drawable
import kotlin.math.roundToInt

private const val CARD_MARGIN_PX = 20

class InsurancesCardsRecyclerAdapter(
    private val onInsuranceClick: (Insurance) -> Unit,
    private val onNewInsuranceClick: () -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    var insurances: List<Insurance> = listOf()
        set(value) {
            field = listOf()
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int =
        (if (position == itemCount - 1) TYPE_NEW_INSURANCE else TYPE_INSURANCE).ordinal

    override fun getItemCount(): Int = insurances.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_NEW_INSURANCE.ordinal -> (holder as NewInsuranceVH).bind()
            TYPE_INSURANCE.ordinal -> (holder as InsuranceVH).bind(insurances[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (viewType == TYPE_NEW_INSURANCE.ordinal) {
            R.layout.layout_card_add_insurance_item
        } else {
            R.layout.layout_card_insurance_item
        }

        val width = if (viewType == TYPE_NEW_INSURANCE.ordinal) parent.width else (parent.width * 0.85f).roundToInt()
        val isMarginRequired = viewType == TYPE_INSURANCE.ordinal
        val layoutParams = MarginLayoutParams(width, MATCH_PARENT).apply {
            if (isMarginRequired) setMargins(CARD_MARGIN_PX, 0, CARD_MARGIN_PX, 0)
        }

        val view = layoutInflater.inflate(layout, parent, false).apply {
            this.layoutParams = layoutParams
        }

        return if (viewType == TYPE_NEW_INSURANCE.ordinal) {
            NewInsuranceVH(view)
        } else {
            InsuranceVH(view)
        }
    }

    inner class InsuranceVH(private val view: View) : ViewHolder(view) {

        fun bind(insurance: Insurance) {
            val typeName = view.context.getString(insurance.type.shortNameId)
            val background = view.context.drawable(insurance.type.backgroundId)

            val expiresAt = view.context.getString(
                R.string.insurance_expired_at,
                insurance.formattedExpireDate
            )
            val serialNumber = view.context.getString(
                R.string.insurance_number_placeholder,
                insurance.serial,
                insurance.number
            )

            with(view) {
                insurance_item_type_tv.text = typeName
                insurance_item_car_number_tv.text = insurance.carNumber
                insurance_item_expired_at_tv.text = expiresAt
                insurance_item_number_tv.text = serialNumber
                insurance_item_background_cl.background = background

                view.allViews.forEach { currentView ->
                    currentView.setOnClickListener {
                        onInsuranceClick(insurance)
                    }
                }
            }
        }
    }

    inner class NewInsuranceVH(private val view: View) : ViewHolder(view) {

        fun bind() {
            view.allViews.forEach { currentView ->
                currentView.setOnClickListener {
                    onNewInsuranceClick()
                }
            }
        }
    }
}