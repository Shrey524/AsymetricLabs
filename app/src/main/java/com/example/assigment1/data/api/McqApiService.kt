package com.example.assigment1.data.api

import com.example.assigment1.data.model.QuestionResponse
import retrofit2.http.GET

interface McqApiService {

    @GET("content")
    suspend fun getQuestions(): List<QuestionResponse>

    companion object {
        fun create(): McqApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl("https://6789df4ddd587da7ac27e4c2.mockapi.io/api/v1/mcq/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()

            return retrofit.create(McqApiService::class.java)
        }
    }
}
