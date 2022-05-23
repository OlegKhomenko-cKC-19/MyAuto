package ua.myauto.domain.models.insurance

import android.util.Log
import ua.myauto.domain.models.period.Period

private const val MONTH_DAYS = 30f
private const val ONE_DAY_COEFFICIENT = 0.25f

data class NewInsuranceParams(val newInsurance: NewInsurance) {

    var period: Period = Period.default

    var fuelType: FuelType = FuelType.default

    var vehicleType: VehicleType = VehicleType.default

    private var carPrice: CarPrice = CarPrice.default

    private var distance: Distance = Distance.default

    private var engineVolume: EngineVolumes = EngineVolumes.default

    var engineVolumeCubes: Int = 0
        set(value) {
            field = value
            engineVolume = EngineVolumes.from(value)
        }

    var distanceK: Int = 0
        set(value) {
            field = value
            distance = Distance.from(value)
        }

    var priceUSD: Int = 0
        set(value) {
            field = value
            carPrice = CarPrice.from(value)
        }

    fun calculatePrice(): Float {
        var price = newInsurance.insuranceType.defaultPrice.toFloat()
        price += engineVolume.price
        price *= fuelType.coefficient
        price *= vehicleType.coefficient
        price *= carPrice.coefficient
        price *= distance.coefficient

        //prices and coefficients from month to days
        price /= MONTH_DAYS

        //one day selected
        val days = if(period.days > 0) period.days else 1
        price *= days * ONE_DAY_COEFFICIENT

        return price
    }
}

private const val CAR_COEFFICIENT = 1.01f
private const val MOTO_COEFFICIENT = 1.025f
private const val TRUCK_COEFFICIENT = 1.025f
private const val BUS_COEFFICIENT = 1.05f

enum class VehicleType(val coefficient: Float) {
    CAR(CAR_COEFFICIENT),
    TRUCK(TRUCK_COEFFICIENT),
    MOTO(MOTO_COEFFICIENT),
    BUS(BUS_COEFFICIENT);

    companion object {
        val default = CAR
    }
}

private const val GASOLINE_COEFFICIENT = 1.01f
private const val DIESEL_COEFFICIENT = 1.015f
private const val ELECTRICITY_COEFFICIENT = 1f

enum class FuelType(val coefficient: Float) {
    GASOLINE(GASOLINE_COEFFICIENT),
    DIESEL(DIESEL_COEFFICIENT),
    ELECTRICITY(ELECTRICITY_COEFFICIENT);

    companion object {
        val default = GASOLINE
    }
}

private const val VOLUME_1600_TO_2400_PRICE = 200
private const val VOLUME_2400_TO_3200_PRICE = 300
private const val VOLUME_MORE_3200_PRICE = 500
private const val VOLUME_LESS_1600_PRICE = 150

enum class EngineVolumes(val price: Int) {
    LESS_1600(VOLUME_LESS_1600_PRICE),
    FROM_1600_TO_2400(VOLUME_1600_TO_2400_PRICE),
    FROM_2400_TO_3200(VOLUME_2400_TO_3200_PRICE),
    MORE_3200(VOLUME_MORE_3200_PRICE);

    companion object {
        fun from(volume: Int) = when {
            volume < 1600 -> LESS_1600
            volume in 1601..2399 -> FROM_1600_TO_2400
            volume in 2400..3199 -> FROM_2400_TO_3200
            else -> MORE_3200
        }

        val default = LESS_1600
    }
}

private const val DISTANCE_LESS_200k_COEFFICIENT = 1.05f
private const val DISTANCE_MORE_200k_COEFFICIENT = 1.1f
private const val DISTANCE_200K = 200

enum class Distance(val coefficient: Float) {
    LESS_200K(DISTANCE_LESS_200k_COEFFICIENT),
    MORE_200K(DISTANCE_MORE_200k_COEFFICIENT);

    companion object {
        fun from(distance: Int) = when {
            distance < DISTANCE_200K -> LESS_200K
            else -> MORE_200K
        }

        val default = LESS_200K
    }
}

private const val CAR_PRICE_LESS_5K_COEFFICIENT = 1.01f
private const val CAR_PRICE_5K_TO_10K_COEFFICIENT = 1.05f
private const val CAR_PRICE_10K_TO_15K_COEFFICIENT = 1.1f
private const val CAR_PRICE_15K_TO_50K_COEFFICIENT = 1.25f
private const val CAR_PRICE_MORE_50K_COEFFICIENT = 1.5f

enum class CarPrice(val coefficient: Float) {
    LESS_5K(CAR_PRICE_LESS_5K_COEFFICIENT),
    FROM_5K_TO_10K(CAR_PRICE_5K_TO_10K_COEFFICIENT),
    FROM_10K_TO_15K(CAR_PRICE_10K_TO_15K_COEFFICIENT),
    FROM_15K_TO_50K(CAR_PRICE_15K_TO_50K_COEFFICIENT),
    MORE_50K(CAR_PRICE_MORE_50K_COEFFICIENT);

    companion object {
        fun from(price: Int) = when {
            price < 5000 -> LESS_5K
            price in 5001..9999 -> FROM_5K_TO_10K
            price in 10000..14999 -> FROM_10K_TO_15K
            price in 15000..49999 -> FROM_15K_TO_50K
            else -> MORE_50K
        }

        val default = LESS_5K
    }
}