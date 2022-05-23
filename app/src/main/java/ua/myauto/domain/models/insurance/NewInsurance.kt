package ua.myauto.domain.models.insurance

import java.io.Serializable
import kotlin.random.Random

private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

data class NewInsurance(val insuranceType: InsuranceType, val carNumber: String) : Serializable {

    val number: Int = generateRandomNumber()
    val serial: String = generateRandomSerial()

    private fun generateRandomNumber(): Int = Random.nextInt(1, 5000)

    private fun generateRandomSerial(): String {
        var serial = ""
        repeat(3) {
            serial += ALPHABET.random()
        }

        return serial
    }
}