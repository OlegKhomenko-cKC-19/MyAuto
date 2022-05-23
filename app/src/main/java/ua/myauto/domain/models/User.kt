package ua.myauto.domain.models

data class User(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String = "",
    val lastName: String = ""
)