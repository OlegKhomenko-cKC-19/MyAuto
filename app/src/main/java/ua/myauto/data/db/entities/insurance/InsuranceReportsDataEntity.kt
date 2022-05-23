package ua.myauto.data.db.entities.insurance

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "Insurances_InsuranceReports",
    foreignKeys = [
        ForeignKey(
            entity = InsuranceEntity::class,
            parentColumns = ["id"],
            childColumns = ["insuranceId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = InsuranceReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = CASCADE
        )
    ]
)
data class InsuranceReportsDataEntity(
    @PrimaryKey
    var ins_rep_id: Int = System.currentTimeMillis().toInt(),
    var insuranceId: Int, var reportId: Int
)