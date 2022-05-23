package ua.myauto.domain.models.report

import java.io.Serializable
import kotlin.random.Random

data class InsuranceReport(
    val id: Int = System.currentTimeMillis().toInt(),
    val number: Int = Random.nextInt(1, 10000),
    val insuranceId: Int,
    val insuranceNumber: Int,
    val insuranceSerial: String,
    val issuedAt: Long,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val registrationAddress: String,
    val liveAddress: String,
    val reportRiskType: ReportRiskType,
    val dateTime: String,
    val carNumber: String,
    val carBodyNumber: String,
    val crashPlace: String,
    val crashDetails: String,
    val wasAskedOrgans: String,
    val carControl: String,
    val damage: String,
    val address: String
) : Serializable