package ua.myauto.data.db.entities.insurance

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ua.myauto.data.db.converters.ReportRiskTypeConverter
import ua.myauto.domain.models.report.ReportRiskType

@Entity(tableName = "InsuranceReports")
@TypeConverters(ReportRiskTypeConverter::class)
data class InsuranceReportEntity(
    @PrimaryKey
    val id: Int,
    val number: Int,
    val insuranceId: Int,
    val insuranceNumber: Int,
    val insuranceSerial: String,
    var issuedAt: Long,
    var fullName: String,
    var phoneNumber: String,
    var email: String,
    var registrationAddress: String,
    var liveAddress: String = registrationAddress,
    var reportRiskType: ReportRiskType,
    var dateTime: String,
    var carNumber: String,
    var carBodyNumber: String,
    var crashPlace: String,
    var crashDetails: String,
    var wasAskedOrgans: String,
    var carControl: String,
    var damage: String,
    var address: String
)