package br.unisanta.tp5_kotlin.approom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val email: String = "",

    @ColumnInfo
    val password: String = "",

    @ColumnInfo
    val curso:String,

    @ColumnInfo
    val idade:Int,

    @ColumnInfo
    val telefone:Long,


)