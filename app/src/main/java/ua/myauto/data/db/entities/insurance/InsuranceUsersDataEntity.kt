package ua.myauto.data.db.entities.insurance

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID
import ua.myauto.data.db.entities.UserEntity

@Entity(
    tableName = "Insurances_Users",
    foreignKeys = [
        ForeignKey(
            entity = InsuranceEntity::class,
            parentColumns = ["id"],
            childColumns = ["insuranceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["username"],
            childColumns = ["insuranceUsername"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InsuranceUsersDataEntity(
    @PrimaryKey
    var ins_us_id: Int = System.currentTimeMillis().toInt(),
    var insuranceId: Int, var insuranceUsername: String
)