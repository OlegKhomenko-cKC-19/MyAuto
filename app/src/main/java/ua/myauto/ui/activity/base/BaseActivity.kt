package ua.myauto.ui.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected val lifecycleOwner get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        observeVM()
    }

    protected abstract val layoutId: Int
    protected abstract fun initViews()
    protected abstract fun observeVM()
}