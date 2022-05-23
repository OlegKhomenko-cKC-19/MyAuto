package ua.myauto.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import ua.myauto.R

class DialogLoading : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_loading, container, false)
    }

    override fun onStart() {
        super.onStart()

        with(dialog?.window ?: return) {
            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))

            attributes = this.attributes.apply {
                gravity = Gravity.CENTER
            }
        }
    }

    companion object {
        private var currentLoadingDialog: DialogLoading? = null

        fun show(activity: AppCompatActivity): DialogLoading {
            return DialogLoading().apply {
                show(activity.supportFragmentManager, "DIALOG_LOADING")
            }.also {
                currentLoadingDialog = it
            }
        }

        fun dismiss() {
            currentLoadingDialog?.dismiss()
        }
    }
}