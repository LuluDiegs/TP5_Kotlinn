package br.unisanta.tp5_kotlin.approom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.model.User


@Database(
    entities = [User::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}