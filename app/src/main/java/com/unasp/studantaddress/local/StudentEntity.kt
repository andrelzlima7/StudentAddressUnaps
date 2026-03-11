package com.unasp.studantaddress.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val remoteId: Long? = null,
    val nome: String,
    val ddd: String,
    val telefone: String,
    val email: String,
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    val regiao: String,
    val synced: Boolean = false
)