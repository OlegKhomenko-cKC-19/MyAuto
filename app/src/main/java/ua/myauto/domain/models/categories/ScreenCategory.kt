package ua.myauto.domain.models.categories

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ua.myauto.R

enum class ScreenCategory(@DrawableRes val drawableId: Int, @StringRes val nameId: Int) {
    FINES(R.drawable.ic_category_fines, R.string.category_fines),
    RULES(R.drawable.ic_category_news, R.string.category_news),
    EXPENSES(R.drawable.ic_category_expenses, R.string.category_expenses),
    FINALIZATION(R.drawable.ic_category_finalization, R.string.category_finalization),
    SUPPORT(R.drawable.ic_category_support, R.string.category_support)
}