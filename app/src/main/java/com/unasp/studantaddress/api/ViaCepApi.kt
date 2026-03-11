package com.unasp.studantaddress.api

import com.unasp.studantaddress.models.ViaCepResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepApi {
    @GET("ws/{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): Response<ViaCepResponse>
}

object RetrofitInstance {
    val api: ViaCepApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApi::class.java)
    }
}