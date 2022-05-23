package ua.myauto.domain.models.report

import androidx.annotation.StringRes
import java.io.Serializable
import ua.myauto.R

sealed class ReportRiskType(@StringRes val titleId: Int) : Serializable {
    class PDTO(titleId: Int = R.string.new_insurance_report_risk_type_pdto) : ReportRiskType(titleId)
    class Animals(titleId: Int = R.string.new_insurance_report_risk_type_items_or_animals) : ReportRiskType(titleId)
    class Disaster(titleId: Int = R.string.new_insurance_report_risk_type_disaster) : ReportRiskType(titleId)

    class Crash(
        val carsAmount: Int,
        val wasVictims: Boolean,
        val guilty: String,
        val guiltyDetails: String,
        val wasFixed: Boolean,
        val compensation: String,
        titleId: Int = R.string.new_insurance_report_risk_type_crash
    ) : ReportRiskType(titleId)

    var riskType: String = this::class.java.name

    companion object {
        val default = Crash(0, false, "", "", false, "")
    }
}