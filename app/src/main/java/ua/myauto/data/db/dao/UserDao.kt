package ua.myauto.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ua.myauto.data.db.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM Users")
    suspend fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM Users WHERE Users.username = :username")
    suspend fun getUserByName(username: String): UserEntity

    @Query("SELECT COUNT(1) FROM Users WHERE Users.username = :username")
    suspend fun countUserByUsername(username: String): Long

    @Insert
    suspend fun insertUser(userEntity: UserEntity): Long

    @Query("DELETE FROM Users WHERE Users.username = :username")
    suspend fun deleteUser(username: String)
}