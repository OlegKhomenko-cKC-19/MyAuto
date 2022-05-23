package ua.myauto.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class UserEntity(
    @PrimaryKey
    var username: String,
    var email: String,
    var password: String,
    var firstName: String,
    val lastName: String
)