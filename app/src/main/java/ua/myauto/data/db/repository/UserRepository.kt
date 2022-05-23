package ua.myauto.data.db.repository

import ua.myauto.App
import ua.myauto.domain.mappers.toUser
import ua.myauto.domain.mappers.toUserEntity
import ua.myauto.domain.models.User

class UserRepository(application: App, private var currentUsername: String = "") {
    private val userDao = application.database.userDao()

    suspend fun getCurrentUser(): User {
        return userDao.getUserByName(currentUsername).toUser()
    }

    suspend fun isUserExists(username: String): Boolean {
        return userDao.countUserByUsername(username) > 0
    }

    suspend fun insertUser(user: User) {
        try {
            userDao.insertUser(user.toUserEntity())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun removeUser(user: User) {
        userDao.deleteUser(user.username)
    }

    companion object {
        fun newInstance() = UserRepository(App.instance)
        fun newInstance(username: String) = UserRepository(App.instance, username)
    }
}