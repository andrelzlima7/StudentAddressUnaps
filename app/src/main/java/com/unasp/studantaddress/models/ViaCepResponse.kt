package com.unasp.studantaddress.models

import com.google.gson.annotations.SerializedName

data class ViaCepResponse(
    @SerializedName("cep") val cep: String = "",
    @SerializedName("logradouro") val logradouro: String = "",
    @SerializedName("complemento") val complemento: String = "",
    @SerializedName("bairro") val bairro: String = "",
    @SerializedName("localidade") val localidade: String = "",
    @SerializedName("uf") val uf: String = "",
    @SerializedName("estado") val estado: String = "",
    @SerializedName("regiao") val regiao: String = "",
    @SerializedName("ddd") val ddd: String = "",
    @SerializedName("erro") val erro: Boolean? = null
)