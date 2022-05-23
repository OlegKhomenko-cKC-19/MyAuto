package ua.myauto.data.db.entities.insurance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Insurances")
data class InsuranceEntity(
    @PrimaryKey
    val id: Int,
    val number: Int,
    val serial: String,
    var typeId: Int,
    var price: Float,
    var issuedAt: Long,
    var expiresAt: Long,
    var carNumber: String,

)