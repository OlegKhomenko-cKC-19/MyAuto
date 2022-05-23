package ua.myauto.domain.models.insurance

data class InsuranceHelpItem(
    val title: String,
    val details: String,
    var isExpanded: Boolean = false
)
