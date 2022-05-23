package ua.myauto.ui.activity

import android.view.View
import kotlinx.android.synthetic.main.activity_web.web_toolbar
import kotlinx.android.synthetic.main.activity_web.web_view
import ua.myauto.R
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.extensions.load

private const val NEWS_URL = "https://thepage.ua/ua/auto/news"

class NewsActivity : ToolbarActivity() {
    override val layoutId: Int get() = R.layout.activity_web
    override val toolbarTitleId: Int get() = R.string.category_news
    override val toolbarView: View get() = web_toolbar
    override val isFinishRequired: Boolean get() = false

    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun initViews() = web_view.load(NEWS_URL)

    override fun observeVM() {}
}