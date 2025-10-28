package br.unisanta.tp5_kotlin.approom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.unisanta.tp5_kotlin.approom.model.User


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll():List<User>

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUserById(uid: Int): User?

    @Update
    suspend fun update(user: User)

    @Insert
    suspend fun insertUser(vararg user:User)

    @Delete
    suspend fun deleteUser(user: User)
}