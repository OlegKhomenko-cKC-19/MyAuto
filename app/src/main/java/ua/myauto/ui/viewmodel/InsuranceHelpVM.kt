package ua.myauto.ui.viewmodel

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.myauto.R
import ua.myauto.domain.models.insurance.InsuranceHelpItem

class InsuranceHelpVM : ViewModel() {

    val itemsLiveData = MutableLiveData<List<InsuranceHelpItem>>()

    fun onCreate(resources: Resources) {
        val titles = resources.getStringArray(R.array.insurance_help_titles)
        val details = resources.getStringArray(R.array.insurance_help_details)

        val helpItems = mutableListOf<InsuranceHelpItem>()
        titles.forEachIndexed { index, title ->
            helpItems.add(InsuranceHelpItem(title, details[index]))
        }

        itemsLiveData.value = helpItems
    }
}