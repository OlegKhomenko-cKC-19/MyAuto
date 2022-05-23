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
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import kotlinx.android.synthetic.main.dialog_select_period.dialog_period_custom_ll
import kotlinx.android.synthetic.main.dialog_select_period.dialog_period_month_ll
import kotlinx.android.synthetic.main.dialog_select_period.dialog_period_week_ll
import kotlinx.android.synthetic.main.dialog_select_period.dialog_period_year_ll
import ua.myauto.R
import ua.myauto.domain.models.period.Period
import ua.myauto.domain.models.period.Period.Custom
import ua.myauto.domain.models.period.Period.Month
import ua.myauto.domain.models.period.Period.Week
import ua.myauto.domain.models.period.Period.Year
import ua.myauto.utils.ONE_DAY_MILLIS

class DialogSelectPeriod : DialogFragment() {

    var selectedPeriod: Period? = null
    var onPeriodSelected: (Period) -> Unit = {}
    private val configuredRangePicker: MaterialDatePicker<Pair<Long, Long>>

    init {
        configuredRangePicker = createDateRangePicker()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_select_period, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    override fun onStart() {
        super.onStart()

        with(dialog?.window ?: return) {
            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))

            attributes = this.attributes.apply {
                gravity = Gravity.CENTER
                horizontalMargin = 32f
            }
        }
    }

    private fun initViews() {
        updateSelected()
    }

    private fun updateSelected() {
        val ctx = context ?: return
        selectedPeriod?.let {
            val selectedView = when (it) {
                is Week -> dialog_period_week_ll
                is Month -> dialog_period_month_ll
                is Year -> dialog_period_year_ll
                is Custom -> dialog_period_custom_ll
            }

            selectedView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.dark_gray_violet))
        }
    }

    private fun initListeners() {
        dialog_period_week_ll.allViews.forEach {
            it.setOnClickListener {
                onPeriodSelected(Week(isReversed = true))
                this.dismiss()
            }
        }
        dialog_period_month_ll.allViews.forEach {
            it.setOnClickListener {
                onPeriodSelected(Month(isReversed = true))
                this.dismiss()
            }
        }
        dialog_period_year_ll.allViews.forEach {
            it.setOnClickListener {
                onPeriodSelected(Year(isReversed = true))
                this.dismiss()
            }
        }

        dialog_period_custom_ll.allViews.forEach {
            it.setOnClickListener {
                configuredRangePicker.show(parentFragmentManager, "TAG_DATE_PERIOD_PICKER")
            }
        }
    }

    private fun createDateRangePicker(): MaterialDatePicker<Pair<Long, Long>> {
        val calendar = Calendar.getInstance()

        val calendarConstraints = CalendarConstraints.Builder()
            .setOpenAt(calendar.timeInMillis)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setCalendarConstraints(calendarConstraints)
            .setSelection(Pair(calendar.timeInMillis, calendar.timeInMillis))
            .build()

        with(datePicker) {
            addOnPositiveButtonClickListener {
                onPeriodSelected(Period.newCustomInstance(it.first, it.second + ONE_DAY_MILLIS))
                this@DialogSelectPeriod.dismiss()
            }

            addOnCancelListener {
                updateSelected()
            }
        }

        return datePicker
    }
}