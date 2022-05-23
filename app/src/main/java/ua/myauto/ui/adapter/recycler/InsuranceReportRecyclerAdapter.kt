package ua.myauto.ui.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.layout_insurance_report_item.view.insurance_report_item_details_tv
import kotlinx.android.synthetic.main.layout_insurance_report_item.view.insurance_report_item_open_ib
import kotlinx.android.synthetic.main.layout_insurance_report_item.view.insurance_report_item_title_tv
import ua.myauto.R
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.ui.adapter.recycler.InsuranceReportRecyclerAdapter.InsuranceReportVH

class InsuranceReportRecyclerAdapter(
    private val onReportClick: (InsuranceReport) -> Unit
) : RecyclerView.Adapter<InsuranceReportVH>() {

    var reports: List<InsuranceReport> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = reports.size

    override fun onBindViewHolder(holder: InsuranceReportVH, position: Int) {
        holder.bind(reports[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InsuranceReportVH(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_insurance_report_item,
            parent,
            false
        )
    )

    inner class InsuranceReportVH(private val view: View) : ViewHolder(view) {

        fun bind(report: InsuranceReport) {
            with(view) {
                insurance_report_item_title_tv.text = view.context.getString(
                    R.string.insurance_report_number,
                    report.number
                )

                val risk = view.context.getString(report.reportRiskType.titleId)
                insurance_report_item_details_tv.text = view.context.getString(
                    R.string.insurance_report_short_details,
                    risk,
                    report.dateTime
                )

                insurance_report_item_open_ib.setOnClickListener {
                    onReportClick(report)
                }
            }
        }
    }
}