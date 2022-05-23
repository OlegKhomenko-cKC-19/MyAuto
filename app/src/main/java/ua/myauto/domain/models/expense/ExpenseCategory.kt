package ua.myauto.domain.models.expense

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import java.io.Serializable
import ua.myauto.R

enum class ExpenseCategory(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int,
    @ColorRes val colorId: Int
) : Serializable {
    BATTERY(
        R.string.expense_category_battery,
        R.drawable.ic_exp_cat_battery,
        R.color.expense_category_battery_background
    ),
    TIRES(
        R.string.expense_category_tires,
        R.drawable.ic_exp_cat_tires,
        R.color.expense_category_tires_background
    ),
    OIL(
        R.string.expense_category_oil,
        R.drawable.ic_exp_cat_oil,
        R.color.expense_category_oil_background
    ),
    GAS(
        R.string.expense_category_gas,
        R.drawable.ic_exp_cat_gas,
        R.color.expense_category_gas_background
    ),
    PARKING(
        R.string.expense_category_parking,
        R.drawable.ic_exp_cat_parking,
        R.color.expense_category_parking_background
    ),
    INSURANCE(
        R.string.expense_category_insurance,
        R.drawable.ic_exp_cat_insurance,
        R.color.expense_category_insurance_background
    ),
    ENGINE_REPAIR(
        R.string.expense_category_engine_repair,
        R.drawable.ic_exp_cat_engine,
        R.color.expense_category_engine_repair_background
    ),
    GEAR_REPAIR(
        R.string.expense_category_gear_repair,
        R.drawable.ic_exp_cat_gears,
        R.color.expense_category_gear_repair_background
    ),
    CONSUMABLES(
        R.string.expense_category_consumables,
        R.drawable.ic_exp_cat_consumables,
        R.color.expense_category_consumables_background
    ),
    OTHER(
        R.string.expense_category_other,
        R.drawable.ic_exp_cat_other,
        R.color.expense_category_other_background
    );

    companion object {
        fun by(id: Int) = values()[id]
    }
}