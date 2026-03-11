package com.unasp.studantaddress.models

import java.util.UUID

data class Student(
    val localId: Long = 0,
    val id: Long? = null,
    val nome: String,
    val ddd: String,
    val telefone: String,
    val email: String,
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    val regiao: String
)