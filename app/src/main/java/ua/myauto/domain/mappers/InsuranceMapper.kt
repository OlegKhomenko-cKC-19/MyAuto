package ua.myauto.domain.mappers

import ua.myauto.data.db.entities.insurance.InsuranceEntity
import ua.myauto.data.db.entities.insurance.InsuranceReportEntity
import ua.myauto.domain.models.insurance.Insurance
import ua.myauto.domain.models.report.InsuranceReport
import ua.myauto.domain.models.insurance.InsuranceType

fun Insurance.toInsuranceEntity() = InsuranceEntity(
        id = id,
        number = number,
        serial = serial,
        typeId = type.ordinal,
        price = price,
        issuedAt = issuedAt,
        expiresAt = expiresAt,
        carNumber = carNumber
    )

fun InsuranceEntity.toInsurance() = Insurance(
    id = id,
    number = number,
    serial = serial,
    type = InsuranceType.by(typeId),
    price = price,
    issuedAt = issuedAt,
    expiresAt = expiresAt,
    carNumber = carNumber
)

fun InsuranceReport.toReportEntity() = InsuranceReportEntity(
    id,
    number,
    insuranceId,
    insuranceNumber,
    insuranceSerial,
    issuedAt,
    fullName,
    phoneNumber,
    email,
    registrationAddress,
    liveAddress,
    reportRiskType,
    dateTime,
    carNumber,
    carBodyNumber,
    crashPlace,
    crashDetails,
    wasAskedOrgans,
    carControl,
    damage,
    address
)

fun InsuranceReportEntity.toReport() = InsuranceReport(
    id,
    number,
    insuranceId,
    insuranceNumber,
    insuranceSerial,
    issuedAt,
    fullName,
    phoneNumber,
    email,
    registrationAddress,
    liveAddress,
    reportRiskType,
    dateTime,
    carNumber,
    carBodyNumber,
    crashPlace,
    crashDetails,
    wasAskedOrgans,
    carControl,
    damage,
    address
)