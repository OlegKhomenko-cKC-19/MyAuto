package ua.myauto.ui.activity.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.layout_toolbar.view.toolbar_back_ib
import kotlinx.android.synthetic.main.layout_toolbar.view.toolbar_settings_ib
import kotlinx.android.synthetic.main.layout_toolbar.view.toolbar_title_tv
import ua.myauto.ui.activity.SettingsActivity

abstract class ToolbarActivity(
    private val isArrowBackVisible: Boolean = true,
    private val isSettingsButtonVisible: Boolean = true
) : BaseActivity() {

    protected open val isFinishRequired = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

    override fun onBackPressed() {
        if (isFinishRequired) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun initToolbar() {
        with(toolbarView) {
            toolbar_back_ib.isInvisible = !isArrowBackVisible
            toolbar_title_tv.text = getString(toolbarTitleId)

            toolbar_back_ib.setOnClickListener {
                onBackPressed()
            }

            toolbar_settings_ib.isInvisible = !isSettingsButtonVisible
            toolbar_settings_ib.setOnClickListener {
                startActivity(Intent(this@ToolbarActivity, SettingsActivity::class.java))
            }
        }
    }

    protected abstract val toolbarTitleId: Int
    protected abstract val toolbarView: View
}