package ua.myauto.domain.models.insurance

import java.io.Serializable
import ua.myauto.data.local.LocalRepository
import ua.myauto.utils.ONE_DAY_MILLIS
import ua.myauto.utils.TimeUtils
import kotlin.math.roundToInt

data class Insurance(
    val id: Int = System.currentTimeMillis().toInt(),
    val number: Int,
    val serial: String,
    val type: InsuranceType,
    var price: Float = 0f,
    val issuedAt: Long,
    var expiresAt: Long,
    val carNumber: String
) : Serializable {

    val isPaymentRequired: Boolean
        get() = (expiresAt - System.currentTimeMillis()) <= ONE_DAY_MILLIS * LocalRepository.daysBeforePush

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAt

    val days: Int
        get() {
            var days =
                ((expiresAt - System.currentTimeMillis()) / ONE_DAY_MILLIS.toFloat()).roundToInt()
            if (days == 0) days = 1
            return days
        }

    val formattedIssueDate: String
        get() = TimeUtils.formatMillis(issuedAt)

    val formattedExpireDate: String
        get() = TimeUtils.formatMillis(expiresAt)

    companion object {
        fun from(insuranceParams: NewInsuranceParams): Insurance =
            Insurance(
                number = insuranceParams.newInsurance.number,
                serial = insuranceParams.newInsurance.serial,
                type = insuranceParams.newInsurance.insuranceType,
                price = insuranceParams.calculatePrice(),
                issuedAt = insuranceParams.period.periodStart,
                expiresAt = insuranceParams.period.periodEnd,
                carNumber = insuranceParams.newInsurance.carNumber
            )
    }
}