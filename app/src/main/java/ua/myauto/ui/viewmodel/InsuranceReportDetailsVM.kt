package ua.myauto.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.utils.KEY_INSURANCE_REPORT

class InsuranceReportDetailsVM : ViewModel() {

    val currentInsuranceReport = MutableLiveData<InsuranceReport?>(null)

    fun parseIntent(intent: Intent) {
        currentInsuranceReport.value = intent.getSerializableExtra(
            KEY_INSURANCE_REPORT
        ) as InsuranceReport
    }
}