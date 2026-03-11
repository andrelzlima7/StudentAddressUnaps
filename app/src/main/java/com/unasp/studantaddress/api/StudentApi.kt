package com.unasp.studantaddress.api

import com.unasp.studantaddress.models.Student
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface StudentApi {

    @GET("students")
    suspend fun findAll(): Response<List<Student>>

    @GET("students/{id}")
    suspend fun findById(@Path("id") id: Long): Response<Student>

    @POST("students")
    suspend fun create(@Body student: Student): Response<Student>

    @PUT("students/{id}")
    suspend fun update(@Path("id") id: Long, @Body student: Student): Response<Student>

    @DELETE("students/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Void>
}

object StudentRetrofitInstance {
    val api: StudentApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.128:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StudentApi::class.java)
    }
}