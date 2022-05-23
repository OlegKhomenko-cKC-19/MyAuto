package ua.myauto.domain.mappers

import ua.myauto.data.db.entities.UserEntity
import ua.myauto.domain.models.User

fun User.toUserEntity() = UserEntity(
    username = username,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName
)

fun UserEntity.toUser() = User(
    username = username,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName
)
