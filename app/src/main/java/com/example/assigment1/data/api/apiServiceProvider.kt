package com.example.assigment1.data.api

import com.example.assigment1.data.constants.ApiConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceProvider {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val mcqApiService: McqApiService = retrofit.create(McqApiService::class.java)
}
