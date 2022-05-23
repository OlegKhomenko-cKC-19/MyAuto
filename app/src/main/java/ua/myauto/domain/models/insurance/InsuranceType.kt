package ua.myauto.domain.models.insurance

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import ua.myauto.R

private const val DEFAULT_OSCE_PRICE = 100
private const val DEFAULT_CASCO_PRICE = 1000

enum class InsuranceType(
    @StringRes val nameId: Int,
    @StringRes val shortNameId: Int,
    @StringRes val detailsId: Int,
    @DrawableRes val backgroundId: Int,
    val defaultPrice: Int
) {
    OSCE(
        R.string.osce_name,
        R.string.osce_name_short,
        R.string.osce_details,
        R.drawable.shape_osce_background,
        DEFAULT_OSCE_PRICE
    ),
    CASCO(
        R.string.casco_name,
        R.string.casco_name,
        R.string.casco_details,
        R.drawable.shape_casco_background,
        DEFAULT_CASCO_PRICE
    );

    companion object {
        fun by(id: Int) = values()[id]
    }
}