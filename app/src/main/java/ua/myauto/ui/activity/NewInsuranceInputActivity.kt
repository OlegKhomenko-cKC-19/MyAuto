package ua.myauto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import kotlinx.android.synthetic.main.activity_new_insurance_input.*
import ua.myauto.R
import ua.myauto.domain.models.insurance.FuelType
import ua.myauto.domain.models.insurance.FuelType.DIESEL
import ua.myauto.domain.models.insurance.FuelType.ELECTRICITY
import ua.myauto.domain.models.insurance.FuelType.GASOLINE
import ua.myauto.domain.models.insurance.NewInsuranceParams
import ua.myauto.domain.models.insurance.VehicleType
import ua.myauto.domain.models.insurance.VehicleType.BUS
import ua.myauto.domain.models.insurance.VehicleType.CAR
import ua.myauto.domain.models.insurance.VehicleType.MOTO
import ua.myauto.domain.models.insurance.VehicleType.TRUCK
import ua.myauto.domain.models.period.Period
import ua.myauto.ui.activity.base.ToolbarActivity
import ua.myauto.ui.common.InputFieldsPage
import ua.myauto.ui.dialog.DialogLoading
import ua.myauto.ui.dialog.NewInsuranceConfirmationDialog
import ua.myauto.ui.extensions.addTextChangedListener
import ua.myauto.ui.extensions.input
import ua.myauto.ui.extensions.isUserClickable
import ua.myauto.ui.extensions.numberInput
import ua.myauto.ui.extensions.toast
import ua.myauto.ui.viewmodel.NewInsuranceInputVM
import ua.myauto.utils.DEFAULT_INT
import ua.myauto.utils.DIALOG_TAG
import ua.myauto.utils.KEY_INSURANCE_DETAILS
import ua.myauto.utils.ONE_DAY_MILLIS

class NewInsuranceInputActivity : ToolbarActivity(), InputFieldsPage {
    override val layoutId: Int get() = R.layout.activity_new_insurance_input
    override val toolbarTitleId: Int get() = R.string.new_insurance_title
    override val toolbarView: View get() = new_insurance_input_toolbar
    override val isFinishRequired: Boolean get() = false

    private val viewModel: NewInsuranceInputVM by viewModels()
    private val configuredRangePicker: MaterialDatePicker<Pair<Long, Long>>

    init {
        configuredRangePicker = createDateRangePicker()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { viewModel.parseIntent(it) }
    }

    override fun initViews() {
        new_insurance_engine_volume_et.addTextChangedListener(this, viewModel)
        new_insurance_price_et.addTextChangedListener(this, viewModel)
        new_insurance_distance_et.addTextChangedListener(this, viewModel)

        new_insurance_price_et.addTextChangedListener {
            viewModel.onPriceChanged(new_insurance_price_et.numberInput)
        }
        new_insurance_distance_et.addTextChangedListener {
            viewModel.onDistanceChanged(new_insurance_distance_et.numberInput)
        }
        new_insurance_engine_volume_et.addTextChangedListener {
            viewModel.onEngineVolumeChanged(new_insurance_engine_volume_et.numberInput)
        }

        new_insurance_car_type_group.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onVehicleTypeSelected(
                when (checkedId) {
                    new_insurance_car_type_rb.id -> CAR
                    new_insurance_moto_type_rb.id -> MOTO
                    new_insurance_truck_type_rb.id -> TRUCK
                    new_insurance_bus_type_rb.id -> BUS
                    else -> VehicleType.default
                }
            )
        }
        new_insurance_fuel_type_group.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onFuelTypeSelected(
                when (checkedId) {
                    new_insurance_fuel_type_gasoline_rb.id -> GASOLINE
                    new_insurance_fuel_type_diesel_rb.id -> DIESEL
                    new_insurance_fuel_type_electricity_rb.id -> ELECTRICITY
                    else -> FuelType.default
                }
            )
        }
        new_insurance_period_group.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onPeriodSelected(
                when (checkedId) {
                    new_insurance_period_type_week_rb.id -> Period.Week()
                    new_insurance_period_type_month_rb.id -> Period.Month()
                    new_insurance_period_type_year_rb.id -> Period.Year()
                    else -> Period.default
                }
            )
        }

        new_insurance_create_btn.setOnClickListener {
            viewModel.onCreateButtonClick()
        }
    }

    override fun observeVM() {
        with(viewModel) {
            titleLiveDataResLiveData.observe(lifecycleOwner) {
                if (it != DEFAULT_INT) {
                    val typeName = getString(it)
                    new_insurance_input_title_tv.text = getString(
                        R.string.new_insurance_page_title,
                        typeName
                    )
                }
            }

            expiredAtLiveData.observe(lifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    new_insurance_expire_date_tv.text = getString(
                        R.string.new_insurance_expire_date,
                        it
                    )
                }
            }

            isDataInputCorrectLiveData.observe(lifecycleOwner) { isCorrect ->
                new_insurance_create_btn.isUserClickable = isCorrect
            }

            showDatePickerDialogLiveData.observe(lifecycleOwner) { showDialog ->
                if (showDialog)
                    configuredRangePicker.show(supportFragmentManager, DIALOG_TAG)
            }

            showConfirmationDialogWithParamsLiveData.observe(lifecycleOwner) { params ->
                params?.let {
                    createConfirmationDialog(it).show(
                        supportFragmentManager,
                        DIALOG_TAG
                    )
                }
            }

            showCascoFieldsLiveData.observe(lifecycleOwner) { show ->
                new_insurance_casco_input_ll.isVisible = show
            }

            onCreateSuccessLiveData.observe(lifecycleOwner) { insurance ->
                insurance ?: return@observe
                startActivity(
                    Intent(
                        this@NewInsuranceInputActivity,
                        PaymentAnimationActivity::class.java
                    ).apply {
                        putExtras(bundleOf(KEY_INSURANCE_DETAILS to insurance))
                    }
                )
            }

            showLoadingLiveData.observe(lifecycleOwner) {
                if (it)
                    DialogLoading.show(this@NewInsuranceInputActivity)
                else
                    DialogLoading.dismiss()
            }

            onCreateFailedLiveData.observe(lifecycleOwner) {
                if (it) toast(R.string.data_saved_locally)
            }
        }
    }

    override fun getInputFieldsData(): Array<String> = arrayOf(
        new_insurance_engine_volume_et.input,
        new_insurance_price_et.input,
        new_insurance_distance_et.input
    )

    private fun createDateRangePicker(): MaterialDatePicker<Pair<Long, Long>> {
        val calendar = Calendar.getInstance()

        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .setOpenAt(calendar.timeInMillis)
            .setStart(calendar.timeInMillis)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setCalendarConstraints(calendarConstraints)
            .setSelection(Pair(calendar.timeInMillis, calendar.timeInMillis))
            .build()

        with(datePicker) {
            addOnPositiveButtonClickListener {
                viewModel.onCustomPeriodReceived(
                    Period.newCustomInstance(
                        it.first,
                        it.second + ONE_DAY_MILLIS
                    )
                )
            }
        }

        return datePicker
    }

    private fun createConfirmationDialog(params: NewInsuranceParams): NewInsuranceConfirmationDialog {
        val dialog = NewInsuranceConfirmationDialog().apply {
            this.params = params
            this.onYesClicked = viewModel::onConfirmed
        }

        return dialog
    }
}