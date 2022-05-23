package ua.myauto.ui.activity

import android.view.View
import kotlinx.android.synthetic.main.activity_web.web_toolbar
import kotlinx.android.synthetic.main.activity_web.web_view
import ua.myauto.R
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.extensions.load

private const val FINES_URL = "https://www.portmone.com.ua/fines"

class ActivityFines : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_web
    override val toolbarTitleId: Int get() = R.string.fines_title
    override val toolbarView: View get() = web_toolbar

    override fun initViews() = web_view.load(FINES_URL)

    override fun observeVM() {}
}