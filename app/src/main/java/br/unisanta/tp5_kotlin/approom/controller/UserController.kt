package br.unisanta.tp5_kotlin.approom.controller

import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.model.User

class UserController(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun getUserById(uid: Int): User? {
        return userDao.getUserById(uid)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}