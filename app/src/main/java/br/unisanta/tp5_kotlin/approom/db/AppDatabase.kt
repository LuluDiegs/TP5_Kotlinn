package br.unisanta.tp5_kotlin.approom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.model.User


@Database(entities =[User::class], version = 1 )
abstract class AppDatabase :RoomDatabase() {
    abstract fun userDao():UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db-user"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}